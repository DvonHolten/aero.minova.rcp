package aero.minova.rcp.rcp.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainerElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Composite;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.css.widgets.DetailLayout;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.parts.WFCStatisticDetailPart;

public class OptimizeDetailHandler {

	@Execute
	public void execute(EModelService emservice, MWindow mwindow, MPerspective mPerspective) {
		MPart detail = emservice.findElements(emservice.getActivePerspective(mwindow), "aero.minova.rcp.rcp.part.details", MPart.class).get(0);

		// Höhe mindestens 900 Pixel
		if (mwindow.getHeight() < 900) {
			mwindow.setHeight(900);

			try {
				// Höhe von Suche und Index im Verhältnis 35/65
				MPartStack searchPartStack = emservice
						.findElements(emservice.getActivePerspective(mwindow), "aero.minova.rcp.rcp.partstack.search", MPartStack.class).get(0);
				searchPartStack.setContainerData("35");
				MPartStack indexPartStack = emservice
						.findElements(emservice.getActivePerspective(mwindow), "aero.minova.rcp.rcp.partstack.index", MPartStack.class).get(0);
				indexPartStack.setContainerData("65");
			} catch (IndexOutOfBoundsException e) {
				// In "Statistic" Ansicht haben wir links nur ein Part -> nichts tun
			}
		}

		Integer defaultSectionWidth = (Integer) mPerspective.getContext().get(Constants.DETAIL_WIDTH);

		// Detail-Composite finden (Kann auch Statistik-Part sein)
		Composite detailComposite;
		if (detail.getObject() instanceof WFCDetailPart) {
			WFCDetailPart wfcDetailPart = (WFCDetailPart) detail.getObject();
			detailComposite = (Composite) wfcDetailPart.getComposite().getData(Constants.DETAIL_COMPOSITE);
		} else {
			WFCStatisticDetailPart wfcStatisticDetailPart = (WFCStatisticDetailPart) detail.getObject();
			detailComposite = wfcStatisticDetailPart.getComposite();
		}

		// Optimale Breite des Details. Dabei werden die horizontalFill-Abschnitte ignoriert
		DetailLayout detailLayout = (DetailLayout) mPerspective.getContext().get(Constants.DETAIL_LAYOUT);
		int prefDetailWidth = detailLayout.layout(detailComposite, false, detailComposite.getSize().x, false, false).x;

		// Standardbreite defaultSectionWidth (für Index/Suche) + Benötigte Breite für Detail (können mehrere Sections nebeneinander sein) + 50 Pixel
		if (mwindow.getWidth() < defaultSectionWidth + prefDetailWidth + 50) {
			mwindow.setWidth(defaultSectionWidth + prefDetailWidth + 50);
		}

		int size = 100000;
		float detailSize = (float) (prefDetailWidth + 35.0) / mwindow.getWidth();
		float leftSize = 1.0f - detailSize;

		MPartSashContainer element = emservice
				.findElements(emservice.getActivePerspective(mwindow), "aero.minova.rcp.rcp.partsashcontainer.main", MPartSashContainer.class).get(0);
		for (MPartSashContainerElement e : element.getChildren()) {
			if (e.getElementId() != null && e.getElementId().equalsIgnoreCase("aero.minova.rcp.rcp.partstack.details")) {
				e.setContainerData(Integer.toString((int) (detailSize * size)));
			} else {
				e.setContainerData(Integer.toString((int) (leftSize * size)));
			}
		}
	}
}