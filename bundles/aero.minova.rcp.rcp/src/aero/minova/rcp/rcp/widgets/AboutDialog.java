package aero.minova.rcp.rcp.widgets;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.rcp.util.ImageUtil;

public class AboutDialog extends TitleAreaDialog {
	private Font lizenzFont;
	private Font infoFont;
	private String version;


	public AboutDialog(Shell parentShell, String versionString) {
		super(parentShell);
		this.version = versionString;
		lizenzFont = new Font(parentShell.getDisplay(), new FontData("Arial", 10, SWT.NORMAL));
		infoFont = new Font(parentShell.getDisplay(), new FontData("Arial", 12, SWT.NORMAL));
	}

	@Override
	public void create() {
		super.create();

		setTitleImage(ImageUtil.getImageDefault("MINOVAT.png"));

		// IMG-Positionierung des 10er-Stands wurde in v.11 entfernt, weil das Img sonst
		// gar nicht mehr sichtbar war

		if (getShell() != null) {
			getShell().pack();
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);
		GridData gd = null;

		Composite address = createAddress(parent);
		gd = new GridData();
		address.setLayoutData(gd);

		Composite info = createAppInfo(parent);
		gd = new GridData();
		gd.verticalAlignment = SWT.TOP;
		info.setLayoutData(gd);

		return parent;
	}

	protected Composite createAppInfo(Composite parent) {
		Composite info = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 1;
		info.setLayout(layout);
		createText(info, ImageUtil.getImageDefault("wfc.png"), "WebFatClient CoreApplicationService", infoFont, IMessageProvider.INFORMATION);
		return info;
	}

	protected Composite createAddress(Composite parent) {
		Composite info = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 1;
		info.setLayout(layout);

		createText(info, ImageUtil.getImageDefault("home.png"), "MINOVA Information Services GmbH", lizenzFont,
				IMessageProvider.INFORMATION);
		createText(info, null, "Tröltschstraße 4", lizenzFont, IMessageProvider.INFORMATION);
		createText(info, null, "97072 Würzburg", lizenzFont, IMessageProvider.INFORMATION);
		createText(info, null, "AG Würzburg HRB 7625", lizenzFont, IMessageProvider.INFORMATION);

		createText(info, ImageUtil.getImageDefault("phone.png"), "+49(0)931-32235 -0", lizenzFont, IMessageProvider.INFORMATION);
		createText(info, ImageUtil.getImageDefault("fax.png"), "+49(0)931-32235 -55", lizenzFont, IMessageProvider.INFORMATION);
		createText(info, ImageUtil.getImageDefault("mail.png"), "info@minova.de", lizenzFont, IMessageProvider.INFORMATION);

		createText(info, ImageUtil.getImageDefault("version.png"), version, lizenzFont, IMessageProvider.INFORMATION);

		return info;
	}

	protected Label createText(Composite parent, String text) {
		return createText(parent, null, text, null, IMessageProvider.INFORMATION);
	}

	/**
	 * @param parent
	 * @param img
	 * @param text
	 * @param font
	 * @param color  (not used)
	 * @return
	 */
	protected Label createText(Composite parent, Image img, String text, Font font, int color) {
		Label lblImg = new Label(parent, SWT.NONE);
		lblImg.setSize(16, 16);
		if (img != null) {
			lblImg.setImage(img);
		}
		Label toRet = new Label(parent, SWT.NONE);
		toRet.setText(text);
		if (font != null) {
			toRet.setFont(font);
		}
		return toRet;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.CENTER;
		parent.setLayoutData(gridData);

		createOkButton(parent, OK, "OK");
		parent.pack();
	}

	protected Button createOkButton(Composite parent, int id, String label) {
		// increment the number of columns in the button bar
		((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.setFont(JFaceResources.getDialogFont());
		button.setData(new Integer(id));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				okPressed();
			}
		});

		parent.pack();
		Shell shell = parent.getShell();
		if (shell != null) {
			shell.setDefaultButton(button);
			shell.pack();
		}

		setButtonLayoutData(button);
		return button;
	}

	@Override
	protected boolean isResizable() {
		return false;
	}
}