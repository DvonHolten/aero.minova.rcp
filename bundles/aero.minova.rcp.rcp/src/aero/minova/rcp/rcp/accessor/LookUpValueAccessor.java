package aero.minova.rcp.rcp.accessor;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.widgets.Control;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.ILocalDatabaseService;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.ValueBuilder;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.rcp.util.Constants;
import aero.minova.rcp.rcp.util.LookupCASRequestUtil;
import aero.minova.rcp.rcp.widgets.Lookup;

public class LookUpValueAccessor extends AbstractValueAccessor {

	@Inject
	IDataService dataService;

	@Inject
	UISynchronize sync;

	@Inject
	ILocalDatabaseService localDatabaseService;

	private MDetail detail;

	public LookUpValueAccessor(MField field, MDetail detail, Control control) {
		super(field, control);
		this.detail = detail;

	}

	@Override
	/**
	 * Die Methode verändert den MessageWert und setzt den Textinhalt auf "". Sie überprüft die locale Datenbase, ob der value bereits bekannt ist. Andernfalls
	 * wird eine Abfrage an den CAS versendet
	 */
	protected void updateControlFromValue(Control control, Value value) {
		if (value == null) {
			((Lookup) control).getDescription().setText("");
			((Lookup) control).setText("");
		}

		if (value instanceof LookupValue) {
			LookupValue lv = (LookupValue) value;
			((Lookup) control).getDescription().setText(lv.description);
			((Lookup) control).setText(lv.keyText);
		} else {
			sync.asyncExec(() -> resolveKeyLong(control, value));
		}
	}

	/**
	 * @param control
	 *            Feld, das aktualisiert werden muss
	 * @param value
	 *            keyLong ohne weitere Informationen. KEIN {@link LookupValue}
	 */
	private void resolveKeyLong(Control control, Value value) {
		if (value != null) {
			((Lookup) control).setMessage("...");
			((Lookup) control).getDescription().setText("");
			((Lookup) control).setText("");

			Table options = ((MLookupField) field).getOptions();
			if (options != null) {
				for (Row r : options.getRows()) {
					if (r.getValue(options.getColumnIndex(Constants.TABLE_KEYLONG)).equals(value)) {
						((Lookup) control).setMessage("");
						((Lookup) control)
								.setText(r.getValue(options.getColumnIndex(Constants.TABLE_KEYTEXT)).getStringValue());
						if (r.getValue(options.getColumnIndex(Constants.TABLE_DESCRIPTION)) != null) {
							((Lookup) control).getDescription()
									.setText(r.getValue(options.getColumnIndex(Constants.TABLE_DESCRIPTION)).getStringValue());
						}
					}
				}
			}
			if (((Lookup) control).getMessage().equals("...")) {
				getLookUpConsumer(control, value);
			}

		}
	}

	/**
	 * @param control
	 * @param value
	 */
	private void getLookUpConsumer(Control control, Value value) {
		// hinterlegen einer Methode in die component, um stehts die Daten des richtigen
		// Indexes in der Detailview aufzulisten. Hierfür wird eine Anfrage an den CAS
		// gestartet, um die Werte des zugehörigen Keys zu erhalten

		CompletableFuture<?> tableFuture;
		tableFuture = LookupCASRequestUtil.getRequestedTable(value.getIntegerValue(), null, field, detail, dataService, "Resolve");
		// Diese Methode lauft auserhalb des Hauptthreads. Desshalb brauchen wir nochmal
		// den MainThread, damit die UI-Componenten aktualisiert werden können
		tableFuture.thenAccept(ta -> sync.asyncExec(() -> {
			Table t = null;
			if (ta instanceof SqlProcedureResult) {
				SqlProcedureResult sql = (SqlProcedureResult) ta;
				t = sql.getResultSet();
			} else if (ta instanceof Table) {
				t = (Table) ta;
			}
			localDatabaseService.addResultsForLookupField(field.getName(), t);
			updateSelectedLookupEntry(t, control);
		}));
	}

	/**
	 * Abfangen der Table der in der Consume-Methode versendeten CAS-Abfrage mit Bindung zur Componente
	 *
	 * @param table
	 * @param control
	 */
	public void updateSelectedLookupEntry(Table table, Control control) {
		Row r = table.getRows().get(0);
		Lookup lc = (Lookup) control;
		int index = table.getColumnIndex(Constants.TABLE_KEYTEXT);
		Value v = r.getValue(index);
		lc.setText((String) ValueBuilder.value(v).create());
		lc.setMessage("");
		if (lc.getDescription() != null && table.getColumnIndex(Constants.TABLE_DESCRIPTION) > -1) {
			Value v1 = r.getValue(table.getColumnIndex(Constants.TABLE_DESCRIPTION));
			if (v1 == null) {
				lc.getDescription().setText("");
			} else {
				lc.getDescription().setText((String) ValueBuilder.value(v1).create());
			}
		}
	}

	@Override
	/**
	 * Wenn das Feld dein Focus verliert wird der Textinhalt überprüft. Ist der Inhalt in keiner Option vorhanden oder ist der Inhalt leer wird das Feld und die
	 * Description bereinigt Ist der Wert vorhanden, so wird geschaut ob er bereits gesetzt wurde oder ob dies getan Werden muss
	 */
	public void setFocussed(boolean focussed) {
		if (!focussed && ((MLookupField) field).getOptions() != null && field.getValue() == getDisplayValue()) {
			((Lookup) control).setMessage("");
			String displayText = ((Lookup) control).getText();
			if (displayText != null && !displayText.equals("")) {

				Table optionTable = ((MLookupField) field).getOptions();
				int indexKeyText = optionTable.getColumnIndex(Constants.TABLE_KEYTEXT);
				int indexKeyLong = optionTable.getColumnIndex(Constants.TABLE_KEYLONG);

				for (Row r : optionTable.getRows()) {
					if (r.getValue(indexKeyText).getStringValue().toLowerCase().startsWith(displayText.toLowerCase())) {
						Value rowValue = r.getValue(indexKeyLong);
						// Der Wert wurde bereits gesetzt und wurde möglicherweise in der Zeile gekürzt
						if (field.getValue() != null && field.getValue().getValue().equals(rowValue.getValue())) {
							((Lookup) control).setText(r.getValue(indexKeyText).getStringValue());
							return;
						}
						// Ist der Wert noch nicht gesetzt, so wird dies nun getan
						else {
							field.setValue(rowValue, false);
							return;
						}
					}
				}
			}
			field.setValue(null, false);
			((Lookup) control).setMessage("");
		}
	}
}
