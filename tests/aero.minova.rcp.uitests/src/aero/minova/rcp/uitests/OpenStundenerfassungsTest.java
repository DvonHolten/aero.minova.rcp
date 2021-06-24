package aero.minova.rcp.uitests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.osgi.framework.FrameworkUtil;

import aero.minova.rcp.dataservice.XmlProcessor;
import aero.minova.rcp.form.menu.mdi.Main;

@RunWith(SWTBotJunit4ClassRunner.class)
public class OpenStundenerfassungsTest {

	private static SWTWorkbenchBot bot;

	@Before
	public void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot(getEclipseContext());
		SWTBotPreferences.TIMEOUT = 30000;
	}

	@Test
	public void openStundenerfassung() {

		SWTBotMenu adminMenu = bot.menu("Administration");

		assertNotNull(adminMenu);
		SWTBotMenu stundenErfassung = adminMenu.menu("Stundenerfassung");
		assertNotNull(stundenErfassung);
		stundenErfassung.click();
		SWTBotView partByTitle = bot.partByTitle("@Form.Search");
		assertNotNull(partByTitle);

		List<SWTBotToolbarButton> toolbarButtons = partByTitle.getToolbarButtons();
		assertTrue(!toolbarButtons.isEmpty());
		toolbarButtons.get(0).click();

//		SWTNatTableBot swtNatTableBot = new SWTNatTableBot();
//		SWTBotNatTable nattable = swtNatTableBot.nattable();
//
//		System.out.println(nattable);
//
//		int row = 5, col = 1;
//		int rowCount = nattable.rowCount();
//		nattable.setCellDataValueByPosition(1, 3, "xxyy");
//		nattable.pressShortcut(Keystrokes.LF);
//		System.out.println(rowCount);
////		int totalRowCount = nattable.preferredRowCount();
//
//		IEclipseContext eclipseContext = getEclipseContext();
//		MApplication application = eclipseContext.get(IWorkbench.class).getApplication();
//		EModelService modelService = eclipseContext.get(EModelService.class);
//
//		List<MPart> findElements = modelService.findElements(application, null, MPart.class, null);
//		System.out.println(findElements);

	}

	@Test
	public void openPreferencesAndTestMenu() {

		// Einstellungen über Command öffnen
		Display.getDefault().asyncExec(() -> {
			ECommandService commandService = getEclipseContext().get(ECommandService.class);
			EHandlerService handlerService = getEclipseContext().get(EHandlerService.class);
			ParameterizedCommand cmd = commandService.createCommand("org.eclipse.ui.window.preferences", null);
			handlerService.executeHandler(cmd);
		});

		// Workspace-Ordner auslesen
		SWTBotShell shell = bot.shell("Preferences");
		assertNotNull(shell);
		SWTBot childBot = new SWTBot(shell.widget);
		SWTBotText currentWorkspaceText = childBot.text(2);
		assertNotNull(currentWorkspaceText);
		assertNotNull(currentWorkspaceText.getText());
		String pathWorkspace = currentWorkspaceText.getText();
		shell.close();

		// application.mdi einlesen und Menü überprüfen
		try {
			Path path = Path.of(pathWorkspace, "application.mdi");
			String applicationString = Files.readString(path);
			assertNotNull(applicationString);
			Main mainMDI = XmlProcessor.get(applicationString, Main.class);
			assertNotNull(mainMDI);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	protected static IEclipseContext getEclipseContext() {
		final IEclipseContext serviceContext = EclipseContextFactory
				.getServiceContext(FrameworkUtil.getBundle(OpenStundenerfassungsTest.class).getBundleContext());
		return serviceContext.get(IWorkbench.class).getApplication().getContext();
	}

	@AfterEach
	public void sleep() {
		bot.sleep(10000);
	}
}