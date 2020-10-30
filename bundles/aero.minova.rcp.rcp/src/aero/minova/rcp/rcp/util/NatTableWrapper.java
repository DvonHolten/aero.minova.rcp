package aero.minova.rcp.rcp.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByDataLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByHeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByModel;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultRowHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.persistence.command.DisplayPersistenceDialogCommandHandler;
import org.eclipse.nebula.widgets.nattable.resize.command.AutoResizeColumnsCommand;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.tree.TreeLayer;
import org.eclipse.nebula.widgets.nattable.tree.command.TreeCollapseAllCommand;
import org.eclipse.nebula.widgets.nattable.tree.command.TreeExpandAllCommand;
import org.eclipse.nebula.widgets.nattable.tree.command.TreeExpandToLevelCommand;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import aero.minova.rcp.form.model.xsd.Column;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.nattable.data.MinovaColumnPropertyAccessor;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TransformedList;

public class NatTableWrapper {

	private NatTable natTable;
	private BodyLayerStack<Row> bodyLayerStack;

	public NatTableWrapper createNatTable(Composite parent, Form form, Table table, Boolean groupByLayer,
			ESelectionService selectionService, IEclipseContext context) {

		Map<String, String> tableHeadersMap = new HashMap<>();
		List<Column> columns = form.getIndexView().getColumn();
		String[] propertyNames = new String[columns.size()];
		int i = 0;
		for (Column column : columns) {
			tableHeadersMap.put(column.getName(), column.getLabel());
			propertyNames[i++] = column.getName();
		}

		// Datenmodel für die Eingaben

		IColumnPropertyAccessor<Row> columnPropertyAccessor = new MinovaColumnPropertyAccessor(table);

		bodyLayerStack = new BodyLayerStack<>(table.getRows(), columnPropertyAccessor);

		// TODO Add this again, after selection works again
//		selectionLayer.addLayerListener(new ILayerListener() {
//
//			@Override
//			public void handleLayerEvent(ILayerEvent event) {
//				Object c = SelectionUtils.getSelectedRowObjects(selectionLayer, bodyDataProvider, false);
//				context.set("ActiveRows", c);
//			}
//		});

		// build the column header layer
		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(propertyNames, tableHeadersMap);
		DataLayer columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
		ColumnHeaderLayer columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer, bodyLayerStack,
				bodyLayerStack.getSelectionLayer());

		// build the row header layer
		IDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(bodyLayerStack.getBodyDataProvider());
		DataLayer rowHeaderDataLayer = new DefaultRowHeaderDataLayer(rowHeaderDataProvider);
		ILayer rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer, bodyLayerStack,
				bodyLayerStack.getSelectionLayer());

		// build the corner layer
		IDataProvider cornerDataProvider = new DefaultCornerDataProvider(columnHeaderDataProvider,
				rowHeaderDataProvider);
		DataLayer cornerDataLayer = new DataLayer(cornerDataProvider);
		ILayer cornerLayer = new CornerLayer(cornerDataLayer, rowHeaderLayer, columnHeaderLayer);

		// build the grid layer
		GridLayer gridLayer = new GridLayer(bodyLayerStack, columnHeaderLayer, rowHeaderLayer, cornerLayer);

		// set the group by header on top of the grid
		CompositeLayer compositeGridLayer = new CompositeLayer(1, 2);
		final GroupByHeaderLayer groupByHeaderLayer = new GroupByHeaderLayer(bodyLayerStack.getGroupByModel(),
				gridLayer, columnHeaderDataProvider, columnHeaderLayer);
		compositeGridLayer.setChildLayer(GroupByHeaderLayer.GROUP_BY_REGION, groupByHeaderLayer, 0, 0);
		compositeGridLayer.setChildLayer("Grid", gridLayer, 0, 1);

		natTable = new NatTable(parent, compositeGridLayer, false);

		ConfigRegistry configRegistry = new ConfigRegistry();

		// as the autoconfiguration of the NatTable is turned off, we have to
		// add the DefaultNatTableStyleConfiguration and the ConfigRegistry
		// manually
		natTable.setConfigRegistry(configRegistry);
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		natTable.addConfiguration(new SingleClickSortConfiguration());
		natTable.registerCommandHandler(new DisplayPersistenceDialogCommandHandler(natTable));

		// add group by configuration
		natTable.addConfiguration(new GroupByHeaderMenuConfiguration(natTable, groupByHeaderLayer));
		natTable.addConfiguration(new HeaderMenuConfiguration(natTable) {
			@Override
			protected PopupMenuBuilder createCornerMenu(NatTable natTable) {
				return super.createCornerMenu(natTable).withStateManagerMenuItemProvider()
						.withMenuItemProvider(new IMenuItemProvider() {

							@Override
							public void addMenuItem(NatTable natTable, Menu popupMenu) {
								MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
								menuItem.setText("Toggle Group By Header"); //$NON-NLS-1$
								menuItem.setEnabled(true);

								menuItem.addSelectionListener(new SelectionAdapter() {
									@Override
									public void widgetSelected(SelectionEvent event) {
										groupByHeaderLayer.setVisible(!groupByHeaderLayer.isVisible());
									}
								});
							}
						}).withMenuItemProvider(new IMenuItemProvider() {

							@Override
							public void addMenuItem(final NatTable natTable, Menu popupMenu) {
								MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
								menuItem.setText("Collapse All"); //$NON-NLS-1$
								menuItem.setEnabled(true);

								menuItem.addSelectionListener(new SelectionAdapter() {
									@Override
									public void widgetSelected(SelectionEvent event) {
										natTable.doCommand(new TreeCollapseAllCommand());
									}
								});
							}
						}).withMenuItemProvider(new IMenuItemProvider() {

							@Override
							public void addMenuItem(final NatTable natTable, Menu popupMenu) {
								MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
								menuItem.setText("Expand All"); //$NON-NLS-1$
								menuItem.setEnabled(true);

								menuItem.addSelectionListener(new SelectionAdapter() {
									@Override
									public void widgetSelected(SelectionEvent event) {
										natTable.doCommand(new TreeExpandAllCommand());
									}
								});
							}
						}).withMenuItemProvider(new IMenuItemProvider() {

							@Override
							public void addMenuItem(final NatTable natTable, Menu popupMenu) {
								MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
								menuItem.setText("Expand to Level 2"); //$NON-NLS-1$
								menuItem.setEnabled(true);

								menuItem.addSelectionListener(new SelectionAdapter() {
									@Override
									public void widgetSelected(SelectionEvent event) {
										natTable.doCommand(new TreeExpandToLevelCommand(2));
									}
								});
							}
						});
			}
		});

		natTable.addConfiguration(new AbstractRegistryConfiguration() {
			@Override
			public void configureRegistry(IConfigRegistry configRegistry) {
				configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE,
						IEditableRule.ALWAYS_EDITABLE);
			}
		});

//		natTable.addConfiguration(new EditorConfiguration());

		natTable.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
//		

//		DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);
//
//		SelectionLayer selectionLayer = new SelectionLayer(bodyDataLayer);
//		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);
//
//		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(
//				propertyNames,propertyToLabelMap);
//		DataLayer columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
//		ILayer columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer, viewportLayer, selectionLayer);
//
//		SortHeaderLayer<Person> sortHeaderLayer = new SortHeaderLayer<>(columnHeaderLayer,
//				new GlazedListsSortModel<>(sortedList, accessor, configRegistry, columnHeaderDataLayer));
//
//		CompositeLayer compositeLayer = new CompositeLayer(1, 2);
//		compositeLayer.setChildLayer(GridRegion.COLUMN_HEADER, sortHeaderLayer, 0, 0);
//		compositeLayer.setChildLayer(GridRegion.BODY, viewportLayer, 0, 1);

//		NatTable natTable = new NatTable(parent, compositeLayer, false);
//
//		natTable.setConfigRegistry(configRegistry);
//		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
//		natTable.addConfiguration(new SingleClickSortConfiguration());

		natTable.configure();

		return this;

	}

	/**
	 * Always encapsulate the body layer stack in an AbstractLayerTransform to
	 * ensure that the index transformations are performed in later commands.
	 *
	 * @param <T>
	 */
	class BodyLayerStack<T> extends AbstractLayerTransform {

		private final SortedList<T> sortedList;

		private final IDataProvider bodyDataProvider;

		private final SelectionLayer selectionLayer;

		private final GroupByModel groupByModel = new GroupByModel();

		private EventList<T> eventList;

		public BodyLayerStack(List<T> values, IColumnPropertyAccessor<T> columnPropertyAccessor) {
			eventList = GlazedLists.eventList(values);
			TransformedList<T, T> rowObjectsGlazedList = GlazedLists.threadSafeList(eventList);

			// use the SortedList constructor with 'null' for the Comparator
			// because the Comparator
			// will be set by configuration
			this.sortedList = new SortedList<>(rowObjectsGlazedList, null);

			// Use the GroupByDataLayer instead of the default DataLayer
			GroupByDataLayer<T> bodyDataLayer = new GroupByDataLayer<>(getGroupByModel(), this.sortedList,
					columnPropertyAccessor);

			// TODO Configure editors
//			final ColumnOverrideLabelAccumulator columnLabelAccumulator = new ColumnOverrideLabelAccumulator(
//					bodyDataLayer);
//			bodyDataLayer.setConfigLabelAccumulator(columnLabelAccumulator);
//			registerColumnLabels(columnLabelAccumulator);

			// get the IDataProvider that was created by the GroupByDataLayer
			this.bodyDataProvider = bodyDataLayer.getDataProvider();
			// layer for event handling of GlazedLists and PropertyChanges
			GlazedListsEventLayer<T> glazedListsEventLayer = new GlazedListsEventLayer<>(bodyDataLayer,
					this.sortedList);

			this.selectionLayer = new SelectionLayer(glazedListsEventLayer);

			// add a tree layer to visualise the grouping
			TreeLayer treeLayer = new TreeLayer(this.selectionLayer, bodyDataLayer.getTreeRowModel());

			ViewportLayer viewportLayer = new ViewportLayer(treeLayer);

			setUnderlyingLayer(viewportLayer);
		}

		public SelectionLayer getSelectionLayer() {
			return this.selectionLayer;
		}

		public SortedList<T> getSortedList() {
			return this.sortedList;
		}

		public IDataProvider getBodyDataProvider() {
			return this.bodyDataProvider;
		}

		public GroupByModel getGroupByModel() {
			return this.groupByModel;
		}

		public EventList<T> getList() {
			return eventList;
		}
	}

	public static void resizeTable(NatTable table) {
		if (!table.isDisposed()) {

			/*
			 * Collection<ILayer> underlyingLayersByColumnPosition =
			 * table.getUnderlyingLayersByColumnPosition(0); int[] selectedColumnPositions =
			 * null; for (ILayer iLayer : underlyingLayersByColumnPosition) {
			 * 
			 * if (iLayer instanceof ViewportLayer)
			 * 
			 * { int minColumnPosition = ((ViewportLayer)
			 * iLayer).getMinimumOriginColumnPosition();
			 * 
			 * int columnCount = ((ViewportLayer) iLayer).getColumnCount();
			 * 
			 * int maxColumnPosition = minColumnPosition + columnCount - 1;
			 * 
			 * selectedColumnPositions = new int[columnCount];
			 * 
			 * for (int i = minColumnPosition; i <= maxColumnPosition; i++) {
			 * 
			 * int idx = i - minColumnPosition;
			 * 
			 * selectedColumnPositions[idx] = i;
			 * 
			 * }
			 * 
			 * }
			 */

			int[] selectedColumnPositions = new int[table.getColumnCount()];

			for (int i = table.getColumnCount() - 1; i > -1; i--) {

				selectedColumnPositions[i] = i;

			}

			// }
			AutoResizeColumnsCommand columnCommand = new AutoResizeColumnsCommand(table, false,
					selectedColumnPositions);

			table.doCommand(columnCommand);
		}
	}

	public void updateData(List<Row> list) {
		bodyLayerStack.getSortedList().clear();
		bodyLayerStack.getSortedList().addAll(list);
	}
}
