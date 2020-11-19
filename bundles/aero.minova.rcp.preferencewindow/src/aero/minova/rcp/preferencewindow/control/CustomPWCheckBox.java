package aero.minova.rcp.preferencewindow.control;

import javax.inject.Inject;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class CustomPWCheckBox extends CustomPWWidget {

	@Inject
	Logger logger;
	
	@Inject
	TranslationService translationService;
	/**
	 * Constructor
	 *
	 * @param label       associated label
	 * @param propertyKey associated key
	 */
	public CustomPWCheckBox(final String label, final String propertyKey) {
		super(label, propertyKey, 1, true);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#build(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control build(final Composite parent) {
		buildLabel(parent, GridData.CENTER);
		final Text text = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
		addControl(text);
		final GridData textGridData = new GridData(GridData.FILL, GridData.BEGINNING, true, false);
		textGridData.horizontalIndent = getIndent();
		text.setLayoutData(textGridData);
		
		final Button button = new Button(parent, SWT.PUSH);
		final GridData buttonGridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false);
		buttonGridData.widthHint = 75;
		button.setText("Reset");
		button.setLayoutData(buttonGridData);
		

////		Optional<ISecurePreferences> sP = WorkspaceAccessPreferences.getSavedPrimaryWorkspaceAccessData(logger);
//		if(sP.isEmpty()) {
//			button.setSelection(false);
//			text.setText("");
//		}else {
//			button.setSelection(true);
//			ISecurePreferences isp = sP.get();
//			String profileName = "Default";
//			try {
//				profileName = isp.get(WorkspaceAccessPreferences.PROFILE, null);
//			} catch (StorageException e1) {
//				e1.printStackTrace();
//			}
//			text.setText(profileName);
//			
//		}
		
		button.addListener(SWT.Selection, e -> {
			
		});
		

		return text;
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
				throw new UnsupportedOperationException("The property '" + getCustomPropertyKey()
						+ "' has to be a Boolean because it is associated to a checkbox");
			}
		}
	}

}
