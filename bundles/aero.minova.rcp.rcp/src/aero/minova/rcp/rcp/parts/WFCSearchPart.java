package aero.minova.rcp.rcp.parts;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.action.MouseEditAction;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommandHandler;
import org.eclipse.nebula.widgets.nattable.edit.config.DefaultEditBindings;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsSortModel;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultRowHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.CellPainterMouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.service.prefs.BackingStoreException;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IMinovaJsonService;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.nattable.data.MinovaColumnPropertyAccessor;
import aero.minova.rcp.rcp.nattable.MinovaSearchConfiguration;
import aero.minova.rcp.rcp.util.LoadTableSelection;
import aero.minova.rcp.rcp.util.NatTableUtil;
import aero.minova.rcp.rcp.util.PersistTableSelection;
import aero.minova.rcp.rcp.widgets.TriStateCheckBoxPainter;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;

public class WFCSearchPart extends WFCFormPart {

	@Inject
	@Preference
	private IEclipsePreferences prefs;

	@Inject
	private IMinovaJsonService mjs;

	@Inject
	TranslationService translationService;

	private Table data;

	private NatTable natTable;
	@Inject
	MPart mPart;

	private SortedList<Row> sortedList;

	private SelectionLayer selectionLayer;

	private MinovaColumnPropertyAccessor columnPropertyAccessor;

	private ColumnHeaderLayer columnHeaderLayer;

	private ColumnReorderLayer columnReorderLayer;

	private DataLayer bodyDataLayer;

	@PostConstruct
	public void createComposite(Composite parent, IEclipseContext context) {

		new FormToolkit(parent.getDisplay());
		if (getForm(parent) == null) {
			return;
		}
		// perspective.getContext().set(Form.class, form); // Wir merken es uns im
		// Context; so können andere es nutzen
		String tableName = form.getIndexView().getSource();
		String string = prefs.get(tableName, null);
		Form searchForm = form;
		aero.minova.rcp.form.model.xsd.Column xsdColumn = new aero.minova.rcp.form.model.xsd.Column();
		xsdColumn.setBoolean(Boolean.FALSE);
		xsdColumn.setLabel("&");
		xsdColumn.setName("&");
		searchForm.getIndexView().getColumn().add(0, xsdColumn);

		data = dataFormService.getTableFromFormIndex(searchForm);
		if (string != null) {
			// Auslesen der zuletzt gespeicherten Daten
			data = mjs.json2Table(string);
		}

		// Es muss nur dann eine neue Zeile hinzugefügt werden wenn kein geladen wurden
		if (data.getRows().size() == 0) {
			data.addRow();
			// Wir setzen die Verundung auf false im Default-Fall!
			data.getRows().get(data.getRows().size() - 1).setValue(new Value(false), 0);
		}

		parent.setLayout(new GridLayout());
		mPart.getContext().set("NatTableDataSearchArea", data);

		natTable = createNatTable(parent, searchForm, data);

	}

	/**
	 * Setzt die größe der Spalten aus dem sichtbaren Bereiches im Index-Bereich auf
	 * die Maximale Breite des Inhalts.
	 *
	 * @param mPart
	 */
	@Inject
	@Optional
	public void resize(@UIEventTopic(Constants.BROKER_RESIZETABLE) MPart mPart) {
		if (!mPart.equals(this.mPart)) {
			return;
		}
		NatTableUtil.resize(natTable);
	}

	public NatTable createNatTable(Composite parent, Form form, Table table) {

		// Datenmodel für die Eingaben
		ConfigRegistry configRegistry = new ConfigRegistry();

		// create the body stack
		EventList<Row> eventList = GlazedLists.eventList(table.getRows());
		sortedList = new SortedList<>(eventList, null);
		columnPropertyAccessor = new MinovaColumnPropertyAccessor(table, form);
		columnPropertyAccessor.initPropertyNames(translationService);

		IDataProvider bodyDataProvider = new ListDataProvider<>(sortedList, columnPropertyAccessor);

		bodyDataLayer = new DataLayer(bodyDataProvider);
		bodyDataLayer.unregisterCommandHandler(UpdateDataCommand.class);
		bodyDataLayer.registerCommandHandler(new UpdateDataCommandHandler(bodyDataLayer) {
			@Override
			protected boolean doCommand(UpdateDataCommand command) {
				if (super.doCommand(command)) {
					Object newValue = command.getNewValue();
					if (data.getRows().size() - 1 == command.getRowPosition() && newValue != null) {
						Table dummy = data;
						dummy.addRow();
						// Datentablle muss angepasst weden, weil die beiden Listen sonst divergieren
						dummy.getRows().get(dummy.getRows().size() - 1).setValue(new Value(false), 0);
						sortedList.add(dummy.getRows().get(dummy.getRows().size() - 1));
					}
					return true;
				}
				return false;
			}
		});

		bodyDataLayer.setConfigLabelAccumulator(new ColumnLabelAccumulator());

		GlazedListsEventLayer<Row> eventLayer = new GlazedListsEventLayer<>(bodyDataLayer, sortedList);

		columnReorderLayer = new ColumnReorderLayer(eventLayer);
		ColumnHideShowLayer columnHideShowLayer = new ColumnHideShowLayer(columnReorderLayer);
		selectionLayer = new SelectionLayer(columnHideShowLayer);
		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);

		// as the selection mouse bindings are registered for the region label
		// GridRegion.BODY
		// we need to set that region label to the viewport so the selection via mouse
		// is working correctly
		viewportLayer.setRegionName(GridRegion.BODY);

		// build the column header layer
		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(
				columnPropertyAccessor.getPropertyNames(), columnPropertyAccessor.getTableHeadersMap());
		DataLayer columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
		columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer, viewportLayer, selectionLayer);

		SortHeaderLayer<Row> sortHeaderLayer = new SortHeaderLayer<>(columnHeaderLayer,
				new GlazedListsSortModel<>(sortedList, columnPropertyAccessor, configRegistry, columnHeaderDataLayer),
				false);

		// build the row header layer
		IDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(bodyDataProvider);
		DataLayer rowHeaderDataLayer = new DefaultRowHeaderDataLayer(rowHeaderDataProvider);
		ILayer rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer, viewportLayer, selectionLayer);

		// build the corner layer
		IDataProvider cornerDataProvider = new DefaultCornerDataProvider(columnHeaderDataProvider,
				rowHeaderDataProvider);
		DataLayer cornerDataLayer = new DataLayer(cornerDataProvider);
		ILayer cornerLayer = new CornerLayer(cornerDataLayer, rowHeaderLayer, columnHeaderLayer);

		// build the grid layer
		GridLayer gridLayer = new GridLayer(viewportLayer, sortHeaderLayer, rowHeaderLayer, cornerLayer);

		natTable = new NatTable(parent, gridLayer, false);

		// as the autoconfiguration of the NatTable is turned off, we have to
		// add the DefaultNatTableStyleConfiguration and the ConfigRegistry
		// manually
		natTable.setConfigRegistry(configRegistry);
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		natTable.addConfiguration(new SingleClickSortConfiguration());
//		natTable.registerCommandHandler(new DisplayPersistenceDialogCommandHandler(natTable));
//

		natTable.addConfiguration(new MinovaSearchConfiguration(table.getColumns(), translationService, form));

		// Hinzufügen von BindingActions, damit in der TriStateCheckBoxPainter der
		// Mouselistener anschlägt!
		natTable.addConfiguration(new DefaultEditBindings() {

			@Override
			public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
				MouseEditAction mouseEditAction = new MouseEditAction();
//				CellEditDragMode cellEditDragMode = new CellEditDragMode();
				super.configureUiBindings(uiBindingRegistry);
				uiBindingRegistry.registerFirstSingleClickBinding(new CellPainterMouseEventMatcher(GridRegion.BODY,
						MouseEventMatcher.LEFT_BUTTON, TriStateCheckBoxPainter.class), mouseEditAction);
//				uiBindingRegistry.registerFirstMouseDragMode(
//						new CellPainterMouseEventMatcher(GridRegion.BODY, MouseEventMatcher.LEFT_BUTTON, TristateCheckBoxPainter.class), cellEditDragMode);
			}

		});

		natTable.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		natTable.configure();
		// set the modern theme to visualize the summary better

//		ThemeConfiguration modernTheme = new ModernNatTableThemeConfiguration();
//		modernTheme.addThemeExtension(new ModernGroupByThemeExtension());
//
//		natTable.setTheme(modernTheme);
		return natTable;
	}

	@PersistTableSelection
	public void savePrefs(@Named("SaveRowConfig") Boolean saveRowConfig, @Named("ConfigName") String name) {

		// xxx.table
		// xxx.search.size (index,breite(int));
		// xxx.index.size (name,breite(int));
		// xxx.index.sortby (name,[a,d];name....);
		// xxx.index.groupby (expand[0,1];name;name2...);
		String tableName = data.getName();
		prefs.put(tableName + "." + name + ".table", mjs.table2Json(data, true));
		if (saveRowConfig) {
//			natTable.get
			String search = "";
			for(int i :columnReorderLayer.getColumnIndexOrder()) {
				search += i + ","+ bodyDataLayer.getColumnWidthByPosition(i) + ";"; 
			}
			prefs.put(tableName + "." + name + ".search.size", search);
		}
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	@LoadTableSelection
	public void loadPrefs(@Named("ConfigName") String name) {
		// Close Editor
		if (natTable.getActiveCellEditor() != null) {
			natTable.getActiveCellEditor().close();
		}

		String tableName = form.getIndexView().getSource();
		String string = prefs.get(tableName + "." + name + ".table", null);
		if (string == null || string.equals(""))
			return;
		Table prefTable = mjs.json2Table(string, true);

		string = prefs.get(tableName + "." + name + ".search.size", null);
		if (string == null || string.equals(""))
			return;

		String[] fields = string.split(";");
		ArrayList<Integer> order = new ArrayList<>();
		for (String s : fields) {
			String[] keyValue = s.split(",");
			int position = Integer.parseInt(keyValue[0].trim());
			int width = Integer.parseInt(keyValue[1].trim());
			order.add(position);
			bodyDataLayer.setColumnWidthByPosition(position, width);
		}
		// TODO längen prüfen und ggf ergänzen
		if (columnReorderLayer.getColumnIndexOrder().size() < order.size()) {
			ArrayList<Integer> toDelete = new ArrayList<>(); 
			for (int i : order) {
				if (!columnReorderLayer.getColumnIndexOrder().contains(i)) {
					toDelete.add(i);
				}
			}
			order.removeAll(toDelete);
		}
		columnReorderLayer.getColumnIndexOrder().removeAll(order);
		columnReorderLayer.getColumnIndexOrder().addAll(0, order);
		columnReorderLayer.reorderColumnPosition(0, 0); // Damit erzwingen wir einen redraw

		// Alle aktuellen Suchzeilen entfernen
		sortedList.clear();
		data.getRows().clear();

		// Gespeicherte Zeilen hinzufügen
		sortedList.addAll(prefTable.getRows());
		data.getRows().addAll(prefTable.getRows());
	}

	@Inject
	@Optional
	private void getNotified(@Named(TranslationService.LOCALE) Locale s) {
		if (columnPropertyAccessor != null) {
			columnPropertyAccessor.translate(translationService);
			String[] propertyNames = columnPropertyAccessor.getPropertyNames();
			for (int i = 0; i < columnPropertyAccessor.getColumnCount(); i++) {
				columnHeaderLayer.renameColumnIndex(i,
						columnPropertyAccessor.getTableHeadersMap().get(propertyNames[i]));
			}
		}
	}

	@Inject
	@Optional
	public void revertSearch(@UIEventTopic(Constants.BROKER_REVERTSEARCHTABLE) String id) {
		// Close Editor
		if (natTable.getActiveCellEditor() != null) {
			natTable.getActiveCellEditor().close();
		}

		// Alle Einträge entfernen
		data.getRows().clear();
		sortedList.clear();

		// Neue Zeile hinzufügen (erste Spalte darf nicht null sein)
		data.addRow();
		data.getRows().get(0).setValue(new Value(false), 0);
		sortedList.add(data.getRows().get(0));
	}

	@Inject
	@Optional
	public void deleteSearchRow(@UIEventTopic(Constants.BROKER_DELETEROWSEARCHTABLE) String id) {
		Set<Range> selectedRowPositions = selectionLayer.getSelectedRowPositions();
		List<Row> rows2delete = new ArrayList<>();
		for (Range range : selectedRowPositions) {
			for (int i = range.start; i < range.end; i++) {
				rows2delete.add(sortedList.get(i));
			}
		}
		// Close Editor
		if (natTable.getActiveCellEditor() != null) {
			natTable.getActiveCellEditor().close();
		}
		deleteSearchRow(rows2delete);
		refreshNatTable();
	}

	public void deleteSearchRow(List<Row> rows) {
		// Löscht eine Liste von Objekten
		sortedList.removeAll(rows);
		data.getRows().removeAll(rows);
		if (sortedList.isEmpty()) {
			Table dummy = data;
			dummy.addRow();
			data.getRows().get(0).setValue(new Value(false), 0);
			sortedList.add(dummy.getRows().get(dummy.getRows().size() - 1));
		}
	}

	public void refreshNatTable() {
		NatTableUtil.refresh(natTable);
	}

	@PreDestroy
	public void test(Composite parent) {
		// Form form = dataFormService.getForm();
	}

	public void saveNattable() {
		natTable.commitAndCloseActiveCellEditor();
	}

}
