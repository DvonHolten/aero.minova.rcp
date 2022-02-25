package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.basic.impl.PartImpl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.css.widgets.MinovaSection;
import aero.minova.rcp.model.form.MSection;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.util.TabUtil;

public class SetNewTabHandler {

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.SELECT_ALL_CONTROLS)
	boolean selectAllControls;

	@Execute
	public void execute(EModelService modelService, MWindow window) {
		// Wir holen und das PerspectiveStack, das alle Perspektiven enthält
		MPerspectiveStack perspectiveStack = (MPerspectiveStack) modelService.find("aero.minova.rcp.rcp.perspectivestack", window);

		// Für jede Perspektive wird die TabListe aktualisiert
		for (MPerspective perspective : perspectiveStack.getChildren()) {
			PartImpl detailPart = (PartImpl) modelService.find("aero.minova.rcp.rcp.part.details", perspective);
			Composite detail = (Composite) ((ScrolledComposite) ((WFCDetailPart) detailPart.getObject()).getComposite().getChildren()[0]).getChildren()[0];
			detail.getParent().getParent().getParent().setTabList(TabUtil.getTabListForPart(detail.getParent().getParent().getParent(), selectAllControls));
			Control[] sections = detail.getChildren();
			for (Control minovaSection : sections) {
				((MinovaSection) minovaSection).setTabList(
						TabUtil.getTabListForSection((MinovaSection) minovaSection, (MSection) minovaSection.getData(Constants.MSECTION), selectAllControls));
			}
		}
		
		
	}

}
