package aero.minova.rcp.model;

import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;

import aero.minova.rcp.model.util.NumberFormatUtil;

public class QuantityValue extends Value {
	private static final long serialVersionUID = 202212081413L;
	private String unit;
	private DataType dataType;

	public QuantityValue(String number, String unit, DataType dataType, DecimalFormatSymbols dfs) {
		super(NumberFormatUtil.getNumberObjectFromString(number, dataType, dfs));
		this.unit = unit == null ? "" : unit;
		this.dataType = dataType;
	}

	public String getUnit() {
		return unit;
	}

	@Override
	public String toString() {
		return MessageFormat.format("QuantityValue [type=" + dataType.toString() +  ", value={0},unit={1}]", String.valueOf(getValue()), unit);
	}
	
}
