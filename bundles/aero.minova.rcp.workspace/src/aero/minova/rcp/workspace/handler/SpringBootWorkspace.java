package aero.minova.rcp.workspace.handler;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;

import aero.minova.rcp.workspace.WorkspaceException;

@SuppressWarnings("restriction")
public class SpringBootWorkspace extends WorkspaceHandler {

	public SpringBootWorkspace(String profile, URL connection, Logger logger) {
		super(logger);
		workspaceData.setConnection(connection);
		workspaceData.setProfile(profile);
	}

	@Override
	public boolean checkConnection(String username, String password, String applicationArea) throws WorkspaceException {
		String profile = getProfile();
		List<ISecurePreferences> workspaceAccessDatas = WorkspaceAccessPreferences.getSavedWorkspaceAccessData(logger);
		ISecurePreferences store = null;
		try {
			for (ISecurePreferences iSecurePreferences : workspaceAccessDatas) {
				if (profile.equals(iSecurePreferences.get(WorkspaceAccessPreferences.PROFILE, null))) {
					store = iSecurePreferences;
					break;
				}
			}
			if (store != null) {
				if (username == null || username.length() == 0) {
					username = store.get(WorkspaceAccessPreferences.USER, "");
				}
				if (password == null || password.length() == 0) {
					password = store.get(WorkspaceAccessPreferences.PASSWORD, "");
				}
				if (applicationArea == null || applicationArea.length() == 0) {
					applicationArea = store.get(WorkspaceAccessPreferences.APPLICATION_AREA, "");
				}
				if (getConnectionString() == null || getConnectionString().length() == 0) {
					workspaceData.setConnection(new URL(store.get(WorkspaceAccessPreferences.URL, "")));
				}
			}
		} catch (StorageException | MalformedURLException e) {
			throw new WorkspaceException(e.getMessage());
		}

		workspaceData.setUsername(username);
		workspaceData.setPassword(password);
		workspaceData.setProfile(profile);
		workspaceData.setApplicationArea(applicationArea);

		// Verbindung zu URL prüfen

		// Profil speichern
		WorkspaceAccessPreferences.storeWorkspaceAccessData(profile, getConnectionString(), getUsername(), getPassword(), getProfile(), applicationArea, false);

		return true;
	}

	@Override
	public void open() throws WorkspaceException {
		if (!Platform.getInstanceLocation().isSet()) {
			String defaultPath = System.getProperty("user.home");

			// build the desired path for the workspace
			String path = defaultPath + "/.minwfc/" + workspaceData.getWorkspaceHashHex() + "/";
			URL instanceLocationUrl = null;
			try {
				instanceLocationUrl = new URL("file", null, path);
				Platform.getInstanceLocation().set(instanceLocationUrl, false);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			URL workspaceURL = Platform.getInstanceLocation().getURL();
			File workspaceDir = new File(workspaceURL.getPath());
			if (!workspaceDir.exists()) {
				workspaceDir.mkdir();
				logger.info(MessageFormat.format("Workspace {0} neu angelegt.", workspaceDir));
			}
			checkDir(workspaceDir, "config");
			checkDir(workspaceDir, "data");
			checkDir(workspaceDir, "i18n");
			checkDir(workspaceDir, "plugins");

			for (ISecurePreferences store : WorkspaceAccessPreferences.getSavedWorkspaceAccessData(logger)) {
				try {
					if (getProfile().equals(store.get(WorkspaceAccessPreferences.PROFILE, null))) {
						store.put(WorkspaceAccessPreferences.PASSWORD, getPassword(), true);
						store.put(WorkspaceAccessPreferences.APPLICATION_AREA, instanceLocationUrl.toString(), false);
						store.flush();
						break;
					}
				} catch (StorageException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
//		} else {
//			System.exit(0);
		}

	}

}
