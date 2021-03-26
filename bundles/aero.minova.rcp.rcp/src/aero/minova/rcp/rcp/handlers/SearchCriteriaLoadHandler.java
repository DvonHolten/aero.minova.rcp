
package aero.minova.rcp.rcp.handlers;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.osgi.service.prefs.BackingStoreException;

import aero.minova.rcp.model.Table;
import aero.minova.rcp.rcp.parts.WFCSearchPart;

public class SearchCriteriaLoadHandler {


	@Inject
	@Preference
	private IEclipsePreferences prefs;

	@AboutToShow
	public void aboutToShow(EModelService service, List<MMenuElement> items, MPart mpart) {
		// Hier müssen wir wissen welche Form geladen ist, damit wir die Korrekten Kriterien laden.
		Table data = null;

		Object part = mpart.getObject();
		if (part instanceof WFCSearchPart) {
			data = ((WFCSearchPart) part).getData();
		}

		if(data != null) {
			try {
				String[] keys = prefs.keys();
				for (String s : keys) {
					if(s.endsWith(".table") && s.startsWith(data.getName()+".")) {
						MHandledMenuItem item = createMenuItem(service, s);
						prefs.get(s, null);
							items.add(item);
					}
				}
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}

		}

	}

	private MHandledMenuItem createMenuItem(EModelService service, String criteriaTableName) {
		MHandledMenuItem mi = service.createModelElement(MHandledMenuItem.class);
		// Name aus dem Eintrag suchen!
		// vWorkingTime.erlanger Heute.table
		// vWorkingTime.erlanger Heute
		// erlanger Heute
		String displayName = criteriaTableName.replace(".table", "");
		displayName = displayName.substring(displayName.indexOf(".") + 1, displayName.length());
		mi.setLabel(displayName);

		final MCommand cmd = MCommandsFactory.INSTANCE.createCommand();
		cmd.setElementId("aero.minova.rcp.rcp.command.searchCriteria");
		mi.setCommand(cmd);

		//action
		MParameter param = MCommandsFactory.INSTANCE.createParameter();
		param.setName("aero.minova.rcp.rcp.commandparameter.criteriaaction");
		param.setValue("LOAD");
		mi.getParameters().add(param);
		//Name
		param = MCommandsFactory.INSTANCE.createParameter();
		param.setName("aero.minova.rcp.rcp.commandparameter.criterianame");
		param.setValue(displayName);
		mi.getParameters().add(param);

		// Handler der aufgerufen werden soll, wenn wir auf den Button drücken
		mi.getPersistedState().put("persistState", "false");
		return mi;
	}

}