package aero.minova.rcp.rcp.parts;

import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.IMinovaPluginService;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.perspectiveswitcher.commands.E4WorkbenchParameterConstants;

public abstract class WFCFormPart {

	@Inject
	protected MPerspective mPerspective;
	@Inject
	protected IDataFormService dataFormService;
	@Inject
	protected IDataService dataService;

	@Inject
	protected IMinovaPluginService pluginService;
	// protected Form form;

	public CompletableFuture<Form> getForm() {
		IEclipseContext ctx = mPerspective.getContext();
//		form = ctx.get(Form.class);
		String formName = mPerspective.getPersistedState().get(E4WorkbenchParameterConstants.FORM_NAME);

		CompletableFuture<Form> form2 = dataFormService.getForm(formName);
		form2.thenAccept(form -> {
			ctx.set(Form.class, form);
		});
		// Form in den Context injected, damit überall darauf zugegriffen werden kann

		return form2;
	}

	public abstract void createUserInterface(Composite parent, Form form);

	@PostConstruct
	public void init(Composite parent, EModelService modelService) {
		getForm().thenAccept(form -> Display.getDefault().asyncExec(() -> createUserInterface(parent, form)));
	}
}