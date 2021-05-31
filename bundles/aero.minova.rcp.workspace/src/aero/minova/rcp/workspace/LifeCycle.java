package aero.minova.rcp.workspace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import javax.inject.Inject;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.preferences.WorkspaceAccessPreferences;
import aero.minova.rcp.translate.lifecycle.Manager;
import aero.minova.rcp.workspace.dialogs.WorkspaceDialog;

@SuppressWarnings("restriction")
public class LifeCycle {

	@Inject
	Logger logger;

	@Inject
	UISynchronize sync;

	@Inject
	IDataService dataService;

	@PostContextCreate
	void postContextCreate(IEclipseContext workbenchContext) throws IllegalStateException, IOException {
		WorkspaceDialog workspaceDialog;

		// Show login dialog to the user
		workspaceDialog = new WorkspaceDialog(null, logger, sync);
		URI workspaceLocation = null;

		if (!WorkspaceAccessPreferences.getSavedPrimaryWorkspaceAccessData(logger).isEmpty()) {
			try {
				ISecurePreferences sPrefs = WorkspaceAccessPreferences.getSavedPrimaryWorkspaceAccessData(logger).get();
				if (!Platform.getInstanceLocation().isSet()) {
					Platform.getInstanceLocation().set(new URL(sPrefs.get(WorkspaceAccessPreferences.APPLICATION_AREA, null)), false);
					try {
						workspaceLocation = Platform.getInstanceLocation().getURL().toURI();
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
					if (workspaceLocation == null) {
						WorkspaceAccessPreferences.resetDefaultWorkspace(logger);
						loadWorkspaceConfigManually(workspaceDialog, workspaceLocation);
					} else {
						dataService.setCredentials(sPrefs.get(WorkspaceAccessPreferences.USER, null), sPrefs.get(WorkspaceAccessPreferences.PASSWORD, null),
								sPrefs.get(WorkspaceAccessPreferences.URL, null), workspaceLocation);
						dataService.setLogger(logger);
					}
				}
			} catch (Exception e) {
				logger.error(e);
				loadWorkspaceConfigManually(workspaceDialog, workspaceLocation);
			}
		} else {
			loadWorkspaceConfigManually(workspaceDialog, workspaceLocation);
		}

		checkModelVersion(workspaceLocation);

		Manager manager = new Manager();
		manager.postContextCreate(workbenchContext);

	}

	/**
	 * Vergleicht die ModelVersion mit der Datei die in der Application mitgeliefert wird. Ist diese Datei in der Workspacelocation nicht vorhanden, müssen wir
	 * in jedem Fall clearPersistedState aufrufen. Ist die Datei vorhanden aber hat eine zu alte Version gilt das Gleiche. Andernfalls machen nichts!
	 */
	private void checkModelVersion(URI workspaceLocation) {
		// lese WorkSpaceFile aus der WorkSpaceLocation
		String readString = null;
		Path resolve = Path.of(workspaceLocation).resolve("ModelVersion.txt");
		String modelVersionPlugin = checkModelVersionFromPlugin();

		try {
			readString = Files.readString(resolve);
		} catch (IOException e) {
			// es gibt keins oder kann nicht gelesen werden!
		}
		if (!modelVersionPlugin.equals(readString)) {
			try {
				Files.deleteIfExists(resolve);
				Files.createFile(resolve);
				// neue Versionsnummer schreiben
				Files.writeString(resolve, modelVersionPlugin);
				Files.deleteIfExists(Path.of(workspaceLocation).resolve(".metadata/.plugins/org.eclipse.e4.workbench/workbench.xmi"));
				showUserDialog();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Lädt aus dem aero.minova.rcp.workspaceplugin den Inhalt aus der ModelVersion.txt. Diese stellt die aktuelle Modelversion bereit.
	 *
	 * @return
	 */
	private String checkModelVersionFromPlugin() {
		final Bundle bundle = FrameworkUtil.getBundle(LifeCycle.class);
		final URL url = FileLocator.find(bundle, new org.eclipse.core.runtime.Path("ModelVersion.txt"), null);
		try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
			String readOut = null;
			readOut = in.readLine();
			return readOut;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	private void showUserDialog() {
		MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Reset Workspace",
				"Due to structural changes, the application area to be loaded is reset!");

	}

	private void loadWorkspaceConfigManually(WorkspaceDialog workspaceDialog, URI workspaceLocation) {
		int returnCode;
		if ((returnCode = workspaceDialog.open()) != 0) {
			logger.info("ReturnCode: " + returnCode);
			System.exit(returnCode); // sollte nie aufgerufen werden, aber der Benutzer hat keinen Workspace
										// ausgesucht
		}
		try {
			workspaceLocation = Platform.getInstanceLocation().getURL().toURI();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		Objects.requireNonNull(workspaceLocation);
		dataService.setCredentials(workspaceDialog.getUsername(), //
				workspaceDialog.getPassword(), //
				workspaceDialog.getConnection(), //
				workspaceLocation);
		dataService.setLogger(logger);
	}
}