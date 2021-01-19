package aero.minova.rcp.preferencewindow.control;

import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.preferences.WorkspaceAccessPreferences;

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
		super(label, propertyKey, 3, true);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#build(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control build(final Composite parent) {
		final Label label = new Label(parent, SWT.NONE);

		if (getLabel() == null) {
			throw new UnsupportedOperationException("Test");
		} else {
			label.setText(getLabel());
		}
		addControl(label);
		final GridData labelGridData = new GridData(SWT.END, SWT.CENTER, false, false);
		labelGridData.horizontalIndent = getIndent();
		label.setLayoutData(labelGridData);
		
		final Text text = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
		addControl(text);
		final GridData textGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		text.setLayoutData(textGridData);

		// Auslesen des PrimaryWorksapces
		if (!WorkspaceAccessPreferences.getSavedPrimaryWorkspaceAccessData(logger).isEmpty()) {
			ISecurePreferences prefs = WorkspaceAccessPreferences.getSavedPrimaryWorkspaceAccessData(logger).get();
			try {
				String profil = prefs.get(WorkspaceAccessPreferences.PROFILE, null);
				text.setText(profil);
			} catch (StorageException e1) {
				e1.printStackTrace();
			}
		} else {
			text.setText("Nicht gesetzt!");
		}

		final Button button = new Button(parent, SWT.PUSH);
		final GridData buttonGridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		button.setText("Reset");
		button.setLayoutData(buttonGridData);

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text.setText("Nicht gesetzt!");
				if (!WorkspaceAccessPreferences.getSavedPrimaryWorkspaceAccessData(logger).isEmpty()) {
					ISecurePreferences prefs = WorkspaceAccessPreferences.getSavedPrimaryWorkspaceAccessData(logger)
							.get();
					try {
						prefs.putBoolean(WorkspaceAccessPreferences.IS_PRIMARY_WORKSPACE, false, false);
						prefs.flush();
					} catch (StorageException | IOException e1) {
						e1.printStackTrace();
					}
				}
			}
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
				throw new UnsupportedOperationException("The property '" + getCustomPropertyKey()
						+ "' has to be a Boolean because it is associated to a checkbox");
			}
		}
	}

}
