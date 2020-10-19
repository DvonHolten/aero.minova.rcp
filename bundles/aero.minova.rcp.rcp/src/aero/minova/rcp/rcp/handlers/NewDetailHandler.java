package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

public class NewDetailHandler {

	@Inject
	EModelService model;
	@Inject 
	private IEventBroker broker;

	@Execute
	public void execute(MPart mpart, MPerspective mPerspective) {
		broker.post("clearFields", "Delete");
	}
}
