package aero.minova.rcp.rcp.widgets;

import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_BORDER;

import java.text.MessageFormat;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.css.CssData;
import aero.minova.rcp.css.CssType;
import aero.minova.rcp.css.ICssStyler;
import aero.minova.rcp.rcp.fields.FieldUtil;
import aero.minova.rcp.rcp.parts.Preview;

public class BrowserSection {

	IEclipsePreferences prefsDetailSections = InstanceScope.INSTANCE.getNode(Constants.PREFERENCES_DETAILSECTIONS);

	@Inject
	private MWindow mwindow;

	private Composite composite;
	private Browser browser;
	private boolean loading;

	public BrowserSection(Composite composite) {
		this.composite = composite;
	}

	public void createBrowser() {
		browser = new Browser(composite, SWT.BORDER);
		
		FormData fd = new FormData();
		fd.right = new FormAttachment(100);
		fd.left = new FormAttachment(0);
		fd.height = ICssStyler.CSS_ROW_HEIGHT * 10;
		browser.setLayoutData(fd);

		browser.addProgressListener(new ProgressListener() {
			@Override
			public void completed(ProgressEvent event) {
				loading = false;
			}

			@Override
			public void changed(ProgressEvent event) {
				loading = true;
			}
		});
	}

	/**
	 * öffne die Adresse im integrierten Browser
	 *
	 * @param url
	 */
	public void openURL(String url) {
		System.out.println(MessageFormat.format("verwende Browser {0} um URL {1} zu öffnen", browser.getBrowserType(), url));
		loadPage(url);
	}

	/**
	 * Synchrone Methode eine URL zu laden
	 * 
	 * @param url
	 * @return
	 */
	private boolean loadPage(String url) {
		Display display = Display.getCurrent();
		boolean set = browser.setUrl(url); // URL content loading is asynchronous
		loading = true;
		while (loading) { // Add synchronous behavior: wait till it finishes loading
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return set;
	}

	/**
	 * zeigt im Browser eine leere Seite an, um die angezeigte Datei wieder freizugeben
	 *
	 * @author wild
	 */
	public void clear() {
		loadPage("about:blank");
		System.out.println("--Setting blank");
		// lasse dem Thread Zeit, den Zugriff auf die geöffnete Datei zu schließen
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {}
	}
}
