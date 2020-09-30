package aero.minova.rcp.rcp.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.extension.e4.selection.E4SelectionListener;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.widgets.Composite;

import aero.minova.rcp.form.model.xsd.Column;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.nattable.data.MinovaColumnPropertyAccessor;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;

public class NatTableUtil {

	public static NatTable createNatTable(Composite parent, Form form, Table table, Boolean groupByLayer, ESelectionService selectionService) {

		Map<String, String> tableHeadersMap = new HashMap<>();
		List<Column> columns = form.getIndexView().getColumn();
		String[] propertyNames = new String[columns.size()];
		int i = 0;
		for (Column column : columns) {
			tableHeadersMap.put(column.getName(), column.getTextAttribute());
			propertyNames[i++] = column.getName();
		}

		// Datenmodel für die Eingaben

		IColumnPropertyAccessor<Row> columnPropertyAccessor = new MinovaColumnPropertyAccessor(table);

		// build the body layer stack
		IRowDataProvider<Row> bodyDataProvider = new ListDataProvider<Row>(table.getRows(), columnPropertyAccessor);
		DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);
		ColumnReorderLayer columnReorderLayer = new ColumnReorderLayer(bodyDataLayer);

		//SelectionLayer selectionLayer = new SelectionLayer(columnReorderLayer);
		SelectionLayer selectionLayer = new SelectionLayer(columnReorderLayer);
		E4SelectionListener<Row> e4SelectionListener = new E4SelectionListener<Row>(selectionService, selectionLayer, bodyDataProvider);
		e4SelectionListener.setFullySelectedRowsOnly(false);
		e4SelectionListener.setHandleSameRowSelection(false);
		selectionLayer.addLayerListener(e4SelectionListener);
		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);

		// build the column header layer stack
		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(propertyNames, tableHeadersMap);
		DataLayer columnHeaderDataLayer = new DataLayer(columnHeaderDataProvider);
		ILayer columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer, viewportLayer, selectionLayer);

		// build the row header layer stack
		IDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(bodyDataProvider);
		DataLayer rowHeaderDataLayer = new DataLayer(rowHeaderDataProvider, 40, 20);
		ILayer rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer, viewportLayer, selectionLayer);

		// build the corner layer stack
		ILayer cornerLayer = new CornerLayer(
				new DataLayer(new DefaultCornerDataProvider(columnHeaderDataProvider, rowHeaderDataProvider)),
				rowHeaderLayer, columnHeaderLayer);



		// create the grid layer composed with the prior created layer stacks
		GridLayer gridLayer = new GridLayer(viewportLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);

		NatTable natTable = new NatTable(parent, gridLayer, true);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);
		return natTable;
	}
}
