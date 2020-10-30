package aero.minova.rcp.rcp.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IMinovaJsonService;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.rcp.util.NatTableWrapper;
import aero.minova.rcp.rcp.util.PersistTableSelection;

public class XMLIndexPart {

	@Inject
	@Preference
	IEclipsePreferences prefs;

	@Inject
	private IMinovaJsonService mjs;

	@Inject
	private IDataFormService dataFormService;


	private Table data;

	@Inject
	IEventBroker broker;

	@Inject
	ESelectionService selectionService;

	private NatTableWrapper natTable;

	@PostConstruct
	public void createComposite(Composite parent) {

		Form form = dataFormService.getForm();
		String tableName = form.getIndexView().getSource();

		String string = prefs.get(tableName, null);
		data = dataFormService.getTableFromFormIndex(form);
		data.addRow();
		if (string != null) {
			data = mjs.json2Table(string);
		}

		parent.setLayout(new GridLayout());

		natTable = new NatTableWrapper().createNatTable(parent, form, data, true, selectionService);
	}

	@PersistTableSelection
	public void savePrefs() {
		// TODO INDEX Part reihenfolge + Gruppierung speichern
	}

	/**
	 * Diese Methode ließt die Index-Apalten aus und erstellet daraus eine Tabel,
	 * diese wir dann an den CAS als Anfrage übergeben.
	 */
	@Inject
	@Optional
	public void load(@UIEventTopic("PLAPLA") Table table) {
		natTable.updateData(table.getRows());
	}

	// Aufruf von resize handler, ev. Umstellen auf Event auf welches die NatTable
	// reagiert
	public NatTableWrapper getNatTable() {
		return natTable;
	}


}
