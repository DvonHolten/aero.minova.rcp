package aero.minova.rcp.preferencewindow.control;

import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class CustomPWCheckBox extends CustomPWWidget {

	PreferenceWindow pwindow;

	/**
	 * Constructor
	 *
	 * @param label
	 *            associated label
	 * @param propertyKey
	 *            associated key
	 */
	public CustomPWCheckBox(final String label, final String propertyKey, PreferenceWindow pwindow) {
		super(label, propertyKey, 1, true);
		this.pwindow = pwindow;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#build(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control build(final Composite parent) {
		Point widthWindow = pwindow.getShell().getSize();
		
		if (getLabel() == null) {
			throw new UnsupportedOperationException("Please specify a label for a checkbox");
		}
		final Button button = new Button(parent, SWT.CHECK);
		addControl(button);
//		GridData buttonGridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
//		buttonGridData.widthHint = widthWindow.x - 50;
//		button.setLayoutData(buttonGridData);
		button.setText(getLabel());
		final boolean originalSelection = (Boolean) PreferenceWindow.getInstance().getValueFor(getCustomPropertyKey());
		button.setSelection(originalSelection);

		button.addListener(SWT.Selection, e -> {
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), button.getSelection());
		});
		return button;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#check()
	 */
	@Override
	public void check() {
		final Object value = PreferenceWindow.getInstance().getValueFor(getCustomPropertyKey());
		if (value == null) {
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), Boolean.valueOf(false));
		} else {
			if (!(value instanceof Boolean)) {
				throw new UnsupportedOperationException(
						"The property '" + getCustomPropertyKey() + "' has to be a Boolean because it is associated to a checkbox");
			}
		}
	}
}
