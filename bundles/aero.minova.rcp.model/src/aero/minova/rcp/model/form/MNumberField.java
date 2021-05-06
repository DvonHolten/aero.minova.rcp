package aero.minova.rcp.model.form;

public class MNumberField extends MField {

	public MNumberField(int decimals) {
		super(decimals);
	}

	@Override
	public boolean isValid() {
		if (getValue() == null) {
			return super.isValid();
		}

		double numberValue = 0;
		if (getValue().getDoubleValue() != null) {
			numberValue = getValue().getDoubleValue();
		} else if (getValue().getIntegerValue() != null) {
			numberValue = getValue().getIntegerValue();
		}

		boolean maxFits = true;
		if (getMaximumValue() != null) {
			maxFits = numberValue <= getMaximumValue();
		}
		boolean minFits = true;
		if (getMinimumValue() != null) {
			minFits = numberValue >= getMinimumValue();
		}

		boolean validTest = super.isValid() && minFits && maxFits;
		if (!validTest) {
			setInvalidColor();
		}
		return validTest;
	}
}
