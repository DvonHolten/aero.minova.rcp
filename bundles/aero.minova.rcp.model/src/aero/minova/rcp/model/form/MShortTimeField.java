package aero.minova.rcp.model.form;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Value;

public class MShortTimeField extends MField {
	@Override
	protected void checkDataType(Value value) {
		if (value == null) return;
		if (value.getType() != DataType.INSTANT) throw new IllegalArgumentException("Value of field " + getName() + " must be of type INSTANT!");
	}
}