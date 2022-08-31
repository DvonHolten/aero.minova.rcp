
package aero.minova.rcp.rcp.parts;

import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBException;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.PersistState;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.prefs.BackingStoreException;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.css.widgets.DetailLayout;
import aero.minova.rcp.css.widgets.MinovaSection;
import aero.minova.rcp.css.widgets.MinovaSectionData;
import aero.minova.rcp.dataservice.ImageUtil;
import aero.minova.rcp.dataservice.XmlProcessor;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Grid;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Onclick;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.form.model.xsd.Procedure;
import aero.minova.rcp.form.model.xsd.Wizard;
import aero.minova.rcp.form.setup.util.XBSUtil;
import aero.minova.rcp.form.setup.xbs.Node;
import aero.minova.rcp.form.setup.xbs.Preferences;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.form.MBooleanField;
import aero.minova.rcp.model.form.MButton;
import aero.minova.rcp.model.form.MDateTimeField;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MGrid;
import aero.minova.rcp.model.form.MLabelText;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.model.form.MNumberField;
import aero.minova.rcp.model.form.MParamStringField;
import aero.minova.rcp.model.form.MPeriodField;
import aero.minova.rcp.model.form.MRadioField;
import aero.minova.rcp.model.form.MSection;
import aero.minova.rcp.model.form.MShortDateField;
import aero.minova.rcp.model.form.MShortTimeField;
import aero.minova.rcp.model.form.MTextField;
import aero.minova.rcp.model.form.ModelToViewModel;
import aero.minova.rcp.model.helper.IHelper;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.accessor.ButtonAccessor;
import aero.minova.rcp.rcp.accessor.DetailAccessor;
import aero.minova.rcp.rcp.accessor.GridAccessor;
import aero.minova.rcp.rcp.accessor.SectionAccessor;
import aero.minova.rcp.rcp.fields.BooleanField;
import aero.minova.rcp.rcp.fields.DateTimeField;
import aero.minova.rcp.rcp.fields.FieldUtil;
import aero.minova.rcp.rcp.fields.LabelTextField;
import aero.minova.rcp.rcp.fields.LookupField;
import aero.minova.rcp.rcp.fields.NumberField;
import aero.minova.rcp.rcp.fields.PeriodField;
import aero.minova.rcp.rcp.fields.RadioField;
import aero.minova.rcp.rcp.fields.ShortDateField;
import aero.minova.rcp.rcp.fields.ShortTimeField;
import aero.minova.rcp.rcp.fields.TextField;
import aero.minova.rcp.rcp.util.DirtyFlagUtil;
import aero.minova.rcp.rcp.util.TabUtil;
import aero.minova.rcp.rcp.util.TranslateUtil;
import aero.minova.rcp.rcp.util.WFCDetailCASRequestsUtil;
import aero.minova.rcp.rcp.widgets.SectionGrid;

@SuppressWarnings("restriction")
public class WFCDetailPart extends WFCFormPart {

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.TIMEZONE)
	String timezone;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.SELECT_ALL_CONTROLS)
	boolean selectAllControls;

	@Inject
	@Preference
	private IEclipsePreferences prefs;

	IEclipsePreferences prefsDetailSections = InstanceScope.INSTANCE.getNode(Constants.PREFERENCES_DETAILSECTIONS);

	private FormToolkit formToolkit;

	private Composite composite;

	private MDetail mDetail = new MDetail();

	@Inject
	private MPart mPart;

	@Inject
	private TranslationService translationService;
	private Locale locale;

	@Inject
	private ECommandService commandService;

	@Inject
	private EHandlerService handlerService;
	private LocalResourceManager resManager;
	private WFCDetailCASRequestsUtil casRequestsUtil;
	private DirtyFlagUtil dirtyFlagUtil;

	private IEclipseContext appContext;

	private int detailWidth;

	@Inject
	MWindow mwindow;

	@Inject
	EModelService eModelService;

	@Inject
	Logger logger;

	MApplication mApplication;

	private List<SectionGrid> sectionGrids = new ArrayList<>();
	private ScrolledComposite scrolled;

	private MinovaSection headSection;

	private int sectionCount = -1;

	@PostConstruct
	public void postConstruct(Composite parent, MWindow window, MApplication mApp) {
		resManager = new LocalResourceManager(JFaceResources.getResources(), parent);
		composite = parent;
		formToolkit = new FormToolkit(parent.getDisplay());
		appContext = mApp.getContext();
		mApplication = mApp;
		getForm();

		if (form == null) {
			return;
		}

		if (form.getDetail() == null) {
			mPart.setVisible(false);
			return;
		}

		// DiryFlagUtil erstellen und in Kontext setzten
		dirtyFlagUtil = ContextInjectionFactory.make(DirtyFlagUtil.class, mPart.getContext());
		mPerspective.getContext().set(DirtyFlagUtil.class, dirtyFlagUtil);

		layoutForm(parent);

		mDetail.setDetailAccessor(new DetailAccessor(mDetail));
		ContextInjectionFactory.inject(mDetail.getDetailAccessor(), mPerspective.getContext()); // In Context, damit Injection verfügbar ist
		mPerspective.getContext().set(MDetail.class, mDetail); // MDetail per Injection ermöglichen

		mDetail.setClearAfterSave(form.getDetail().isClearAfterSave());

		// Label und Icon aus Maske setzten
		mPart.setIconURI(ImageUtil.retrieveIcon(form.getIcon(), false));
		mPart.setLabel(form.getTitle());
		mPart.updateLocalization();

		// Erstellen der Util-Klasse, welche sämtliche funktionen der Detailansicht steuert
		casRequestsUtil = ContextInjectionFactory.make(WFCDetailCASRequestsUtil.class, mPerspective.getContext());
		casRequestsUtil.initializeCasRequestUtil(getDetail(), mPerspective, this);
		mPerspective.getContext().set(WFCDetailCASRequestsUtil.class, casRequestsUtil);
		mPerspective.getContext().set(Constants.DETAIL_WIDTH, detailWidth);
		TranslateUtil.translate(composite, translationService, locale);

		// Helpers erst initialisieren, wenn casRequestsUtil erstellt wurde
		for (IHelper helper : mDetail.getHelpers()) {
			helper.setControls(mDetail);
		}

		openRestoringUIDialog();

		// In XBS gegebene Felder füllen
		casRequestsUtil.setValuesAccordingToXBS();
	}

	/**
	 * Öffnet den "UI wird wiederhergestellt" Dialog, wenn er diese Session noch nicht geöffnet wurde und die Checkbox "NEVER_SHOW_RESTORING_UI_MESSAGE" nie
	 * gewählt wurde
	 */
	private void openRestoringUIDialog() {
		String prefName = form.getIndexView().getSource() + "." + Constants.LAST_STATE + ".index.size";
		boolean stateToLoad = prefs.get(prefName, null) != null; // Gibt es überhaupt etwaszu laden?
		boolean neverShow = prefs.getBoolean(Constants.NEVER_SHOW_RESTORING_UI_MESSAGE, false);
		boolean shownThisSession = prefs.getBoolean(Constants.RESTORING_UI_MESSAGE_SHOWN_THIS_SESSION, false);
		// Benötigt für UI-Tests damit sich in ihnen Dialog nicht öffnet, wird in LifeCycle gesetzt
		boolean neverShowContext = appContext.get(Constants.NEVER_SHOW_RESTORING_UI_MESSAGE) != null
				&& (boolean) appContext.get(Constants.NEVER_SHOW_RESTORING_UI_MESSAGE);

		if (stateToLoad && !neverShow && !shownThisSession && !neverShowContext) {
			MessageDialogWithToggle mdwt = MessageDialogWithToggle.openInformation(Display.getCurrent().getActiveShell(), //
					translationService.translate("@RestoringUIDialog.Title", null), //
					translationService.translate("@RestoringUIDialog.InfoText", null), //
					translationService.translate("@RestoringUIDialog.NeverShowAgain", null), //
					false, null, null);
			if (mdwt.getToggleState()) {
				prefs.put(Constants.NEVER_SHOW_RESTORING_UI_MESSAGE, "true");
			}
			prefs.put(Constants.RESTORING_UI_MESSAGE_SHOWN_THIS_SESSION, "true");
		}
	}

	private static class HeadOrPageOrGridWrapper {
		private Object headOrPageOrGrid;
		public boolean isHead = false;
		public boolean isOP = false;
		private String formTitle;
		public String formSuffix;
		public String id;
		public String icon;
		public boolean isVisible;

		public HeadOrPageOrGridWrapper(Object headOrPageOrGrid) {
			this.headOrPageOrGrid = headOrPageOrGrid;
			if (headOrPageOrGrid instanceof Head) {
				isHead = true;
				id = "Head";
				isVisible = true;
			} else if (headOrPageOrGrid instanceof Page) {
				id = ((Page) headOrPageOrGrid).getId();
				icon = ((Page) headOrPageOrGrid).getIcon();
				isVisible = ((Page) headOrPageOrGrid).isVisible();
			} else {
				id = ((Grid) headOrPageOrGrid).getId();
				icon = ((Grid) headOrPageOrGrid).getIcon();
				isVisible = true;
			}
		}

		public HeadOrPageOrGridWrapper(Object headOrPageOrGrid, boolean isOP, String formSuffix) {
			this(headOrPageOrGrid);
			this.formSuffix = formSuffix;
			this.isOP = isOP;
			if (headOrPageOrGrid instanceof Head && !isOP) {
				isHead = true;
			} else {
				isHead = false;
			}

			if (headOrPageOrGrid instanceof Head && isOP) {
				id = formSuffix + ".Head";
			}
		}

		public HeadOrPageOrGridWrapper(Object headOrPageOrGrid, boolean isOP, String formSuffix, String formTitle, String icon) {
			this(headOrPageOrGrid, isOP, formSuffix);
			this.formTitle = formTitle;
			this.icon = icon;
		}

		public String getTranslationText() {
			if (isHead) {
				return "@Head";
			} else if (headOrPageOrGrid instanceof Head && isOP) {
				return formTitle;
			} else if (headOrPageOrGrid instanceof Grid) {
				return ((Grid) headOrPageOrGrid).getTitle();
			} else if (headOrPageOrGrid instanceof Page) {
				return ((Page) headOrPageOrGrid).getText();
			}
			return "";
		}

		public List<Object> getFieldOrGrid() {
			if (headOrPageOrGrid instanceof Head) {
				return ((Head) headOrPageOrGrid).getFieldOrGrid();
			} else if (headOrPageOrGrid instanceof Grid) {
				// es existieren keine Felder, nur eine Table
				List<Object> mylistList = new ArrayList<>();
				mylistList.add(headOrPageOrGrid);
				return mylistList;
			}
			return ((Page) headOrPageOrGrid).getFieldOrGrid();
		}

	}

	private void layoutForm(Composite parent) {

		// Wir wollen eine horizontale Scrollbar, damit auch bei breiten Details alles erreichbar ist
		scrolled = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolled.setShowFocusedControl(true);
		Composite wrap = new Composite(scrolled, SWT.NO_SCROLL);
		DetailLayout detailLayout = new DetailLayout();
		wrap.setLayout(detailLayout);
		parent.setData(Constants.DETAIL_COMPOSITE, wrap);
		mPerspective.getContext().set(Constants.DETAIL_LAYOUT, detailLayout);

		// Abschnitte der Hauptmaske und OPs erstellen
		for (Object headOrPage : form.getDetail().getHeadAndPageAndGrid()) {
			HeadOrPageOrGridWrapper wrapper = new HeadOrPageOrGridWrapper(headOrPage);
			layoutSection(wrap, wrapper);
		}
		loadOptionPages(wrap);

		scrolled.setContent(wrap);
		scrolled.setExpandHorizontal(true);
		scrolled.setExpandVertical(true);

		scrolled.addListener(SWT.Resize, event -> adjustScrollbar(scrolled, wrap));

		// Setzen der TabListe der Sections.
		parent.setTabList(parent.getChildren());
		// Holen des Parts
		Composite part = parent.getParent();
		// Setzen der TabListe des Parts. Dabei bestimmt SelectAllControls, ob die Toolbar mit selektiert wird.
		part.setTabList(TabUtil.getTabListForPart(part, selectAllControls));
		// Wir setzen eine leere TabListe für die Perspektive, damit nicht durch die Anwendung mit Tab navigiert werden kann.
		part.getParent().setTabList(new Control[0]);

		// Helper-Klasse initialisieren
		initializeHelper(form.getHelperClass());
	}

	private void adjustScrollbar(ScrolledComposite scrolled, Composite wrap) {
		int height = scrolled.getClientArea().height;
		int width = scrolled.getClientArea().width;

		scrolled.setMinSize(wrap.computeSize(SWT.DEFAULT, height).x, wrap.computeSize(width, SWT.DEFAULT).y);
	}

	private void initializeHelper(String helperName) {
		if (helperName == null) {
			return;
		}

		IHelper iHelper = null;

		pluginService.activatePlugin(helperName);
		BundleContext bundleContext = FrameworkUtil.getBundle(WFCDetailPart.class).getBundleContext();
		try {
			ServiceReference<?>[] allServiceReferences = bundleContext.getAllServiceReferences(IHelper.class.getName(), null);
			for (ServiceReference<?> serviceReference : allServiceReferences) {
				String property = (String) serviceReference.getProperty("component.name");
				if (property.equals(helperName)) {
					iHelper = (IHelper) bundleContext.getService(serviceReference);
				}
			}
		} catch (Exception e1) {
			logger.error(e1);
		}

		if (iHelper == null) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", translationService.translate("@msg.HelperNotFound", null));
		} else {
			getDetail().addHelper(iHelper);
			ContextInjectionFactory.inject(iHelper, mPerspective.getContext()); // In Context, damit Injection verfügbar ist
		}
	}

	private void loadOptionPages(Composite parent) {
		Preferences preferences = (Preferences) mApplication.getTransientData().get(Constants.XBS_FILE_NAME);

		Node maskNode = XBSUtil.getNodeWithName(preferences, mPerspective.getPersistedState().get(Constants.FORM_NAME));
		if (maskNode == null) {
			return;
		}

		for (Node settingsForMask : maskNode.getNode()) {
			if (settingsForMask.getName().equals(Constants.OPTION_PAGES)) {
				for (Node op : settingsForMask.getNode()) {
					try {
						try {
							Form opForm = dataFormService.getForm(op.getName());
							addOPFromForm(opForm, parent, op);
						} catch (IllegalArgumentException e) {
							try {
								String opContent = dataService.getHashedFile(op.getName()).get();
								Grid opGrid = XmlProcessor.get(opContent, Grid.class);
								addOPFromGrid(opGrid, parent, op);
							} catch (JAXBException e1) {
								logger.error(e1);
							}
						}
					} catch (ExecutionException e) {
						logger.error(e);
					} catch (InterruptedException e) {
						logger.error(e);
						Thread.currentThread().interrupt();
					} catch (NoSuchFieldException e) {
						MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getMessage());
					}
				}
			}
		}
	}

	private void addOPFromForm(Form opForm, Composite parent, Node opNode) throws NoSuchFieldException {
		mDetail.addOptionPage(opForm);
		Map<String, String> keynamesToValues = XBSUtil.getKeynamesToValues(opNode);
		mDetail.addOptionPageKeys(opForm.getDetail().getProcedureSuffix(), keynamesToValues);

		for (Object headOrPage : opForm.getDetail().getHeadAndPageAndGrid()) {
			HeadOrPageOrGridWrapper wrapper;
			if (headOrPage instanceof Head) {
				// Head in der OP braucht titel und icon der Form
				wrapper = new HeadOrPageOrGridWrapper(headOrPage, true, opForm.getDetail().getProcedureSuffix(), opForm.getTitle(), opForm.getIcon());
			} else {
				wrapper = new HeadOrPageOrGridWrapper(headOrPage, true, opForm.getDetail().getProcedureSuffix());
			}
			layoutSection(parent, wrapper);
		}

		// Keyzuordnung aus .xbs prüfen, gibt es alle Felder?
		for (Entry<String, String> e : keynamesToValues.entrySet()) {
			String opFieldName = opForm.getDetail().getProcedureSuffix() + "." + e.getKey();
			String mainFieldName = e.getValue();

			if (mDetail.getField(opFieldName) == null) {
				NoSuchFieldException error = new NoSuchFieldException(
						"Option Page \"" + opForm.getDetail().getProcedureSuffix() + "\" does not contain Field \"" + e.getKey() + "\"! (As defined in .xbs)");
				logger.error(error);
				throw error;
			}

			if (mainFieldName.startsWith(Constants.OPTION_PAGE_QUOTE_ENTRY_SYMBOL)) {
				continue;
			}

			if (mDetail.getField(mainFieldName) == null) {
				NoSuchFieldException error = new NoSuchFieldException("Main Mask does not contain Field \"" + mainFieldName + "\", needed for OP \""
						+ opForm.getDetail().getProcedureSuffix() + "\"! (As defined in .xbs)");
				logger.error(error);
				throw error;
			}
		}

		initializeHelper(opForm.getHelperClass());
	}

	private void addOPFromGrid(Grid opGrid, Composite parent, Node opNode) throws NoSuchFieldException {
		HeadOrPageOrGridWrapper wrapper = new HeadOrPageOrGridWrapper(opGrid);
		layoutSection(parent, wrapper);
		addKeysFromXBSToGrid(opGrid, opNode);
	}

	/**
	 * Diese Methode extrahiert die Keyzuordnung für ein Grid aus der XBS, setzt diese ins Grid und überprüft, ob es alle Felder gibt
	 *
	 * @param grid
	 * @param Node
	 * @throws NoSuchFieldException
	 */
	private void addKeysFromXBSToGrid(Grid grid, Node node) throws NoSuchFieldException {
		// OP-Feldnamen zu Values Map aus .xbs setzten
		MGrid opMGrid = mDetail.getGrid(grid.getId());
		SectionGrid sg = ((GridAccessor) opMGrid.getGridAccessor()).getSectionGrid();
		Map<String, String> keynamesToValues = XBSUtil.getKeynamesToValues(node);
		sg.setFieldnameToValue(keynamesToValues);

		// Keyzuordnung aus .xbs prüfen, gibt es alle Felder?
		List<String> sgColumnNames = new ArrayList<>();
		for (Column c : sg.getDataTable().getColumns()) {
			sgColumnNames.add(c.getName());
		}
		for (Entry<String, String> e : keynamesToValues.entrySet()) {
			if (!sgColumnNames.contains(e.getKey())) {
				NoSuchFieldException error = new NoSuchFieldException(
						"Grid \"" + sg.getDataTable().getName() + "\" does not contain Field \"" + e.getKey() + "\"! (As defined in .xbs)");
				logger.error(error);
				throw error;
			}
			if (e.getValue().startsWith(Constants.OPTION_PAGE_QUOTE_ENTRY_SYMBOL)) {
				continue;
			}
			if (mDetail.getField(e.getValue()) == null) {
				NoSuchFieldException error = new NoSuchFieldException("Main Mask does not contain Field \"" + e.getValue() + "\", needed for Grid \""
						+ sg.getDataTable().getName() + "\"! (As defined in .xbs)");
				logger.error(error);
				throw error;
			}
		}
	}

	/**
	 * Diese Methode bekommt einen Composite übergeben, und erstellt aus dem übergenen Objekt ein Section. Diese Sektion ist entweder der Head (Kopfdaten) oder
	 * eine OptionPage die sich unterhalb der Kopfdaten eingliedert. Zusätzlich wird ein TraverseListener übergeben, der das Verhalten für TAB und Enter
	 * festlegt.
	 *
	 * @param parent
	 * @param headOrPageOrGrid
	 */

	private void layoutSection(Composite parent, HeadOrPageOrGridWrapper headOrPageOrGrid) {
		MinovaSectionData sectionData = new MinovaSectionData();
		MinovaSection section;
		if (headOrPageOrGrid.isHead) {
			section = new MinovaSection(parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
			headSection = section;
		} else {
			section = new MinovaSection(parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE);
			section.getImageLink().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDoubleClick(MouseEvent e) {
					minimizeSection(section);
				}
			});
		}
		section.setLayoutData(sectionData);
		section.setData(TRANSLATE_PROPERTY, headOrPageOrGrid.getTranslationText());

		ImageDescriptor imageDescriptor = ImageUtil.getImageDescriptor(headOrPageOrGrid.icon, false);
		if (!imageDescriptor.equals(ImageDescriptor.getMissingImageDescriptor())) {
			section.setImage(resManager.createImage(imageDescriptor));
		}

		section.addControlListener(new ControlAdapter() {
			@Override
			public void controlMoved(ControlEvent e) {
				parent.setTabList(TabUtil.getSortedSectionTabList(parent));
			}
		});

		// Wir erstellen die Section des Details.
		MSection mSection = new MSection(headOrPageOrGrid.isHead, "open", mDetail, headOrPageOrGrid.id, section.getText());
		mSection.setSectionAccessor(new SectionAccessor(mSection, section));
		// Button erstellen, falls vorhanden
		createButton(headOrPageOrGrid, section);

		layoutSectionClient(headOrPageOrGrid, section, mSection);

		section.addListener(SWT.Resize, event -> adjustScrollbar(scrolled, parent));

		// Order setzen und sectionCount erhöhen
		sectionCount++;
		sectionData.setOrder(sectionCount);

		// Alten Zustand wiederherstellen
		// HorizontalFill
		String prefsHorizontalFillKey = form.getTitle() + "." + headOrPageOrGrid.getTranslationText() + ".horizontalFill";
		String horizontalFillString = prefsDetailSections.get(prefsHorizontalFillKey, "false");
		sectionData.setHorizontalFill(Boolean.parseBoolean(horizontalFillString));

		// Ein-/Ausgeklappt
		String prefsExpandedString = form.getTitle() + "." + headOrPageOrGrid.getTranslationText() + ".expanded";
		String expandedString = prefsDetailSections.get(prefsExpandedString, "true");
		section.setExpanded(Boolean.parseBoolean(expandedString));

		// Minimiert
		String prefsMinimizedString = form.getTitle() + "." + headOrPageOrGrid.getTranslationText() + ".minimized";
		String minimizedString = prefsDetailSections.get(prefsMinimizedString, "false");
		if (Boolean.parseBoolean(minimizedString)) {
			minimizeSection(section);
		}

		// Sichtbarkeit entsprechend der Maske setzen
		mSection.setVisible(headOrPageOrGrid.isVisible);

		detailWidth = section.getCssStyler().getSectionWidth();
		section.requestLayout();
		section.style();
	}

	private void minimizeSection(MinovaSection section) {
		Image image = section.getImageLink().getImage();
		if (image == null) {
			return;
		}

		section.setVisible(false);
		section.setMinimized(true);
		Control textClient = headSection.getTextClient();
		ToolBar bar = (ToolBar) textClient;
		ToolItem tItem = new ToolItem(bar, SWT.PUSH);
		tItem.setImage(image);
		tItem.setData(FieldUtil.TRANSLATE_PROPERTY, section.getData(FieldUtil.TRANSLATE_PROPERTY));
		tItem.setToolTipText(translationService.translate((String) section.getData(FieldUtil.TRANSLATE_PROPERTY), null));

		tItem.addSelectionListener(SelectionListener.widgetSelectedAdapter(selectionEvent -> {
			section.setVisible(true);
			section.setMinimized(false);
			tItem.dispose();
			bar.requestLayout();
			headSection.requestLayout();
		}));
		headSection.requestLayout();
		bar.requestLayout();
	}

	private void layoutSectionClient(HeadOrPageOrGridWrapper headOrPageOrGrid, MinovaSection section, MSection mSection) {
		// Client Area
		Composite clientComposite = getFormToolkit().createComposite(section);
		clientComposite.setLayout(new FormLayout());
		getFormToolkit().paintBordersFor(clientComposite);
		section.setClient(clientComposite);

		// Erstellen der Field des Section.
		createFields(clientComposite, headOrPageOrGrid, mSection, section);
		// Setzen der TabListe für die einzelnen Sections.
		TabUtil.updateTabListOfSectionComposite(clientComposite);
		// Setzen der TabListe der Sections im Part.
		clientComposite.getParent().setTabList(TabUtil.getTabListForSection(section, mSection, selectAllControls));

		// MSection wird zum MDetail hinzugefügt.
		mDetail.addMSection(mSection);
	}

	/**
	 * Erstellt einen oder mehrere Button auf der übergebenen Section. Die Button werden in der ausgelesenen Reihelfolge erstellt und in eine Reihe gesetzt.
	 *
	 * @param composite2
	 * @param headOPOGWrapper
	 * @param mSection
	 * @param section
	 */
	private void createButton(HeadOrPageOrGridWrapper headOPOGWrapper, Section section) {
		if (headOPOGWrapper.headOrPageOrGrid instanceof Grid) {
			return;
		}

		boolean isHead = headOPOGWrapper.isHead && !headOPOGWrapper.isOP;

		final ToolBar bar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL | SWT.RIGHT | SWT.NO_FOCUS);

		List<aero.minova.rcp.form.model.xsd.Button> buttons = null;
		if (headOPOGWrapper.headOrPageOrGrid instanceof Head) {
			buttons = ((Head) headOPOGWrapper.headOrPageOrGrid).getButton();
		} else {
			buttons = ((Page) headOPOGWrapper.headOrPageOrGrid).getButton();
		}

		for (aero.minova.rcp.form.model.xsd.Button btn : buttons) {
			MButton mButton = new MButton(btn.getId());
			mButton.setText(btn.getText());
			mButton.setIcon(btn.getIcon());

			ButtonAccessor ba;
			if (isHead) {
				ba = createToolItemInPartToolbar(btn);
			} else {
				ba = createToolItemInSection(bar, btn);
			}

			mButton.setButtonAccessor(ba);
			ba.setmButton(mButton);
			mDetail.putButton(mButton);
		}

		section.setTextClient(bar);
	}

	private ButtonAccessor createToolItemInSection(final ToolBar bar, aero.minova.rcp.form.model.xsd.Button btn) {

		// Kein Gruppenname: Element nur in Toolbar, kein Menü
		if (btn.getGroup() == null) {
			ToolItem item = new ToolItem(bar, SWT.PUSH);
			fillItemWithValues(item, btn);
			return new ButtonAccessor(item);
		}

		// Menü für Gruppennamen finden
		Menu groupMenu = null;
		if (btn.getGroup() != null) {
			for (ToolItem c : bar.getItems()) {
				if (btn.getGroup().equalsIgnoreCase((String) c.getData(Constants.GROUP_NAME))) {
					groupMenu = (Menu) c.getData(Constants.GROUP_MENU);
					break;
				}
			}
		}

		ToolItem toolItem = null;

		// Erstes Vorkommen des Gruppennamens: Element in Toolbar, Menü muss noch erstellt werden
		if (groupMenu == null) {
			final ToolItem item = new ToolItem(bar, SWT.DROP_DOWN);
			fillItemWithValues(item, btn);

			Menu menu = new Menu(new Shell(Display.getCurrent()), SWT.POP_UP);
			item.setData(Constants.GROUP_MENU, menu);

			item.addListener(SWT.Selection, event -> {
				if (event.detail == SWT.ARROW) {
					Rectangle rect = item.getBounds();
					Point pt = new Point(rect.x, rect.y + rect.height);
					pt = bar.toDisplay(pt);
					menu.setLocation(pt.x, pt.y);
					menu.setVisible(true);
				}
			});

			toolItem = item;
			groupMenu = menu;
		}

		// Wenn Gruppenname gegeben ist soll der Button immer auch in das Dropdown-Menü
		MenuItem menuEntry = new MenuItem(groupMenu, SWT.PUSH);
		fillItemWithValues(menuEntry, btn);

		return new ButtonAccessor(toolItem, menuEntry);
	}

	/**
	 * Füllt das Item mit den Werten aus dem Knopf (Text, Tooptip, Icon) und fügt den Onclick Listener hinzu, wenn in der Maske definiert
	 *
	 * @param item
	 * @param btn
	 */
	private void fillItemWithValues(Item item, aero.minova.rcp.form.model.xsd.Button btn) {
		item.setData(btn);
		item.setData(Constants.GROUP_NAME, btn.getGroup());

		if (item instanceof MenuItem) {
			((MenuItem) item).setEnabled(btn.isEnabled());
		} else if (item instanceof ToolItem) {
			((ToolItem) item).setEnabled(btn.isEnabled());
		}

		if (btn.getText() != null) {
			if (item instanceof MenuItem) {
				((MenuItem) item).setText(translationService.translate(btn.getText(), null));
				((MenuItem) item).setToolTipText(translationService.translate(btn.getText(), null));
			} else if (item instanceof ToolItem) {
				((ToolItem) item).setToolTipText(translationService.translate(btn.getText(), null));
			}
			item.setData(FieldUtil.TRANSLATE_PROPERTY, btn.getText());
		}
		if (btn.getIcon() != null && btn.getIcon().trim().length() > 0) {
			final ImageDescriptor buttonImageDescriptor = ImageUtil.getImageDescriptor(btn.getIcon().replace(".ico", ""), false);
			item.setImage(resManager.createImage(buttonImageDescriptor));
		}

		Object event = findEventForID(btn.getId());
		if (event instanceof Onclick) {
			Onclick onclick = (Onclick) event;
			if (item instanceof MenuItem) {
				((MenuItem) item).addSelectionListener(getSelectionAdapterForItem(onclick, item));
			} else if (item instanceof ToolItem) {
				((ToolItem) item).addSelectionListener(getSelectionAdapterForItem(onclick, item));
			}
		}
	}

	private SelectionAdapter getSelectionAdapterForItem(Onclick onclick, Item item) {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isEnabled = false;
				if (item instanceof MenuItem) {
					isEnabled = ((MenuItem) item).isEnabled();
				} else if (item instanceof ToolItem) {
					isEnabled = ((ToolItem) item).isEnabled();
				}
				if (e.detail != SWT.ARROW && isEnabled) {
					// TODO: Andere procedures/bindings/instances auswerten
					List<Object> binderOrProcedureOrInstances = onclick.getBinderOrProcedureOrInstance();

					for (Object o : binderOrProcedureOrInstances) {
						if (o instanceof Wizard) {
							Map<String, String> parameter = Map.of(Constants.CLAZZ, Constants.WIZARD, Constants.PARAMETER, ((Wizard) o).getWizardname());
							ParameterizedCommand command = commandService.createCommand(Constants.AERO_MINOVA_RCP_RCP_COMMAND_DYNAMIC_BUTTON, parameter);
							handlerService.executeHandler(command);
						} else if (o instanceof Procedure) {
							casRequestsUtil.callProcedure((Procedure) o);
						} else {
							// Auch in Methode createParameters() anpassen!!
							System.err.println("Event vom Typ " + o.getClass() + " für Buttons noch nicht implementiert!");
						}
					}
				}
			}
		};
	}

	private ButtonAccessor createToolItemInPartToolbar(aero.minova.rcp.form.model.xsd.Button btn) {

		// Kein Gruppenname: Element nur in Toolbar, kein Menü
		if (btn.getGroup() == null) {
			MHandledToolItem handledToolItem = eModelService.createModelElement(MHandledToolItem.class);
			fillMHandledItemWithValues(handledToolItem, btn);
			mPart.getToolbar().getChildren().add(handledToolItem);
			return new ButtonAccessor(handledToolItem);
		}

		// Menü für Gruppennamen finden
		MMenu groupMenu = null;
		if (btn.getGroup() != null) {
			for (MToolBarElement element : mPart.getToolbar().getChildren()) {
				if (btn.getGroup().equalsIgnoreCase(element.getPersistedState().get(Constants.GROUP_NAME))) {
					groupMenu = ((MHandledToolItem) element).getMenu();
					break;
				}
			}
		}

		MHandledToolItem handledToolItem = null;

		// Erstes Vorkommen des Gruppennamens: Element in Toolbar, Menü muss noch erstellt werden
		if (groupMenu == null) {
			handledToolItem = eModelService.createModelElement(MHandledToolItem.class);
			fillMHandledItemWithValues(handledToolItem, btn);
			mPart.getToolbar().getChildren().add(handledToolItem);

			groupMenu = eModelService.createModelElement(MMenu.class);
			handledToolItem.getPersistedState().put(Constants.GROUP_NAME, btn.getGroup());
			handledToolItem.setMenu(groupMenu);
		}

		// Wenn Gruppenname gegeben ist soll der Button immer auch in das Dropdown-Menü
		MHandledMenuItem menuEntry = eModelService.createModelElement(MHandledMenuItem.class);
		fillMHandledItemWithValues(menuEntry, btn);
		groupMenu.getChildren().add(menuEntry);

		return new ButtonAccessor(handledToolItem, menuEntry);
	}

	/**
	 * Füllt das handledItem mit den Werten aus dem Knopf (Text, Tooptip, Icon), fügt den Command (und damit den Handler) sowie die benötigten Parameter hinzu
	 *
	 * @param handledItem
	 * @param btn
	 */
	private void fillMHandledItemWithValues(MHandledItem handledItem, aero.minova.rcp.form.model.xsd.Button btn) {
		handledItem.getPersistedState().put(IWorkbench.PERSIST_STATE, String.valueOf(false));
		handledItem.getPersistedState().put(Constants.CONTROL_ID, btn.getId());
		handledItem.setLabel(btn.getText());
		handledItem.setTooltip(btn.getText());
		if (btn.getIcon() != null && btn.getIcon().trim().length() > 0) {
			handledItem.setIconURI(ImageUtil.retrieveIcon(btn.getIcon(), false));
		}

		MCommand command = mApplication.getCommand(Constants.AERO_MINOVA_RCP_RCP_COMMAND_DYNAMIC_BUTTON);
		handledItem.setCommand(command);

		Object event = findEventForID(btn.getId());
		if (event instanceof Onclick) {
			Onclick onclick = (Onclick) event;
			List<Object> binderOrProcedureOrInstances = onclick.getBinderOrProcedureOrInstance();
			handledItem.getParameters().addAll(createParameters(binderOrProcedureOrInstances));
		} else {
			MParameter mParameterForm = eModelService.createModelElement(MParameter.class);
			mParameterForm.setName(Constants.PARAMETER);
			mParameterForm.setValue(btn.getId());
			handledItem.getParameters().add(mParameterForm);
		}
	}

	private List<MParameter> createParameters(List<Object> binderOrProcedureOrInstances) {
		List<MParameter> parameter = new ArrayList<>();
		MParameter mParameterForm = null;
		for (Object o : binderOrProcedureOrInstances) {
			if (o instanceof Wizard) {
				mParameterForm = eModelService.createModelElement(MParameter.class);
				mParameterForm.setName(Constants.CLAZZ);
				mParameterForm.setValue(Constants.WIZARD);
				parameter.add(mParameterForm);

				mParameterForm = eModelService.createModelElement(MParameter.class);
				mParameterForm.setName(Constants.PARAMETER);
				mParameterForm.setValue(((Wizard) o).getWizardname());
				parameter.add(mParameterForm);
			} else if (o instanceof Procedure) {
				Procedure p = (Procedure) o;
				String procedureID = p.getName() + p.getParam().hashCode();
				mPart.getContext().set(procedureID, p);

				mParameterForm = eModelService.createModelElement(MParameter.class);

				mParameterForm.setName(Constants.CLAZZ);
				mParameterForm.setValue(Constants.PROCEDURE);
				parameter.add(mParameterForm);

				mParameterForm = eModelService.createModelElement(MParameter.class);
				mParameterForm.setName(Constants.PARAMETER);
				mParameterForm.setValue(procedureID);
				parameter.add(mParameterForm);
			} else {
				// Auch in Methode getSelectionAdapterForItem() anpassen!!
				System.err.println("Event vom Typ " + o.getClass() + " für Buttons noch nicht implementiert!");
			}
		}
		return parameter;
	}

	private Object findEventForID(String id) {
		if (form.getEvents() != null) {
			for (Onclick onclick : form.getEvents().getOnclick()) {
				if (onclick.getRefid().equals(id)) {
					return onclick;
				}
			}
		}
		// TODO: Onbinder und ValueChange implementieren
		return null;
	}

	private MGrid createMGrid(Grid grid, MSection section) {

		if (grid.getId() == null) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Grid " + grid.getProcedureSuffix() + " has no ID!");
		}

		MGrid mgrid = new MGrid(grid.getId());
		mgrid.setTitle(grid.getTitle());
		mgrid.setFill(grid.getFill());
		mgrid.setProcedureSuffix(grid.getProcedureSuffix());
		mgrid.setProcedurePrefix(grid.getProcedurePrefix());
		mgrid.setmSection(section);
		final ImageDescriptor gridImageDescriptor = ImageUtil.getImageDescriptor(grid.getIcon(), false);
		Image gridImage = resManager.createImage(gridImageDescriptor);
		mgrid.setIcon(gridImage);
		mgrid.setHelperClass(grid.getHelperClass());
		List<MField> mFields = new ArrayList<>();
		for (Field f : grid.getField()) {
			try {
				MField mF = ModelToViewModel.convert(f, locale);
				mFields.add(mF);
			} catch (NullPointerException e) {
				showErrorMissingSQLIndex(f, grid.getId() + "." + f.getName(), e);
			}
		}
		mgrid.setGrid(grid);
		mgrid.setFields(mFields);
		return mgrid;
	}

	/**
	 * Erstellt die Field einer Section.
	 *
	 * @param composite
	 *            der parent des Fields
	 * @param headOrPage
	 *            bestimmt ob die Fields nach den Regeln des Heads erstellt werden oder der einer Page.
	 * @param mSection
	 *            die Section deren Fields erstellt werden.
	 */
	private void createFields(Composite composite, HeadOrPageOrGridWrapper headOrPage, MSection mSection, MinovaSection section) {
		IEclipseContext context = mPerspective.getContext();
		List<MField> visibleMFields = new ArrayList<>();
		for (Object fieldOrGrid : headOrPage.getFieldOrGrid()) {
			if (fieldOrGrid instanceof Grid) {

				createGrid(composite, mSection, section, context, fieldOrGrid);

			} else {

				Field field = (Field) fieldOrGrid;

				String suffix = headOrPage.isOP ? headOrPage.formSuffix + "." : "";
				MField mField = createMField(field, mSection, suffix);
				if (mField.isVisible()) {
					visibleMFields.add(mField);
				}

				if (mField instanceof MParamStringField) {
					for (Field f : ((MParamStringField) mField).getSubFields()) {
						MField subfield = createMField(f, mSection, suffix);
						((MParamStringField) mField).addSubMField(subfield);
						if (subfield.isVisible()) {
							visibleMFields.add(subfield);
						}
					}
				}
			}
		}
		createUIFields(visibleMFields, composite);
	}

	public void createUIFields(List<MField> mFields, Composite clientComposite) {
		int row = 0;
		int column = 0;
		for (MField mField : mFields) {
			int width = mField.getNumberColumnsSpanned();

			if (column + width > 4) {
				column = 0;
				row++;
			}

			createField(clientComposite, mField, row, column);

			row += mField.getNumberRowsSpanned() - 1;
			column += width;
			if (mField.isFillHorizontal()) {
				column = 4;
			}
		}
	}

	public MField createMField(Field field, MSection mSection, String suffix) {
		String fieldName = suffix + field.getName();
		try {
			MField f = ModelToViewModel.convert(field, locale);
			f.addValueChangeListener(dirtyFlagUtil);
			f.setName(fieldName);

			getDetail().putField(f);
			f.setMSection(mSection);

			if (field.isVisible()) {
				mSection.addMField(f);
			}

			return f;
		} catch (NullPointerException e) {
			showErrorMissingSQLIndex(field, fieldName, e);
		}
		return null;
	}

	private void createGrid(Composite composite, MSection mSection, MinovaSection section, IEclipseContext context, Object fieldOrGrid) {
		SectionGrid sg = new SectionGrid(composite, section, (Grid) fieldOrGrid, mDetail);
		MGrid mGrid = createMGrid((Grid) fieldOrGrid, mSection);
		mGrid.addGridChangeListener(dirtyFlagUtil);
		GridAccessor gA = new GridAccessor(mGrid);
		gA.setSectionGrid(sg);
		mGrid.setGridAccessor(gA);
		mSection.getmDetail().putGrid(mGrid);
		sectionGrids.add(sg);
		initializeHelper(((Grid) fieldOrGrid).getHelperClass());

		ContextInjectionFactory.inject(sg, context); // In Context injected, damit Injection in der Klasse verfügbar ist
		sg.createGrid();
		mGrid.setDataTable(sg.getDataTable());

		// XBS nach Keyzuordnung überprüfen, gilt nur fürs erste Grid
		try {
			if (mDetail.getGrids().size() == 1) {
				checkXBSForGridKeys((Grid) fieldOrGrid);
			}
		} catch (NoSuchFieldException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getMessage());
		}
	}

	public void showErrorMissingSQLIndex(Field field, String fieldname, NullPointerException e) {
		if (field.getSqlIndex() == null) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Field " + fieldname + " has no SQL-Index!");
		} else {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getMessage());
		}
	}

	/**
	 * XBS überprüfen, ob es eine Keyzuordnung für dieses Grid gibt
	 */
	private void checkXBSForGridKeys(Grid grid) throws NoSuchFieldException {
		Preferences preferences = (Preferences) mApplication.getTransientData().get(Constants.XBS_FILE_NAME);
		Node maskNode = XBSUtil.getNodeWithName(preferences, mPerspective.getPersistedState().get(Constants.FORM_NAME));
		if (maskNode == null) {
			return;
		}

		for (Node settingsForMask : maskNode.getNode()) {
			if (settingsForMask.getName().equals(Constants.OPTION_PAGE_GRID)) {
				addKeysFromXBSToGrid(grid, settingsForMask);
			}
		}
	}

	private void createField(Composite composite, MField field, int row, int column) {
		if (field instanceof MBooleanField) {
			BooleanField.create(composite, field, row, column, locale, mPerspective);
		} else if (field instanceof MNumberField) {
			NumberField.create(composite, (MNumberField) field, row, column, locale, mPerspective);
		} else if (field instanceof MDateTimeField) {
			DateTimeField.create(composite, field, row, column, locale, timezone, mPerspective, translationService);
		} else if (field instanceof MShortDateField) {
			ShortDateField.create(composite, field, row, column, locale, timezone, mPerspective, translationService);
		} else if (field instanceof MShortTimeField) {
			ShortTimeField.create(composite, field, row, column, locale, timezone, mPerspective, translationService);
		} else if (field instanceof MLookupField) {
			LookupField.create(composite, field, row, column, locale, mPerspective);
		} else if (field instanceof MTextField) {
			TextField.create(composite, field, row, column, mPerspective);
		} else if (field instanceof MRadioField) {
			RadioField.create(composite, field, row, column, locale, mPerspective);
		} else if (field instanceof MLabelText) {
			LabelTextField.createBold(composite, field, row, column, mPerspective);
		} else if (field instanceof MPeriodField) {
			PeriodField.create(composite, field, row, locale, mPerspective);
		}
	}

	@Inject
	@Optional
	private void getNotified(@Named(TranslationService.LOCALE) Locale s) {
		this.locale = s;
		if (translationService != null && composite != null) {
			TranslateUtil.translate(composite, translationService, locale);
		}
	}

	public MDetail getDetail() {
		return mDetail;
	}

	public WFCDetailCASRequestsUtil getRequestUtil() {
		return casRequestsUtil;
	}

	@PersistState
	public void persistState() {
		// Grids
		for (SectionGrid sg : sectionGrids) {
			sg.saveState();
		}

		// Sections, ein-/ausgeklappt
		for (MSection s : mDetail.getMSectionList()) {
			MinovaSection section = ((SectionAccessor) s.getSectionAccessor()).getSection();
			String prefsExpandedString = form.getTitle() + "." + section.getData(TRANSLATE_PROPERTY) + ".expanded";
			prefsDetailSections.put(prefsExpandedString, section.isExpanded() + "");

			String prefsMinimizedString = form.getTitle() + "." + section.getData(TRANSLATE_PROPERTY) + ".minimized";
			prefsDetailSections.put(prefsMinimizedString, section.isMinimized() + "");
		}

		try {
			prefsDetailSections.flush();
		} catch (BackingStoreException e1) {
			logger.error(e1);
		}
	}

	public FormToolkit getFormToolkit() {
		return formToolkit;
	}

	public Composite getComposite() {
		return composite;
	}

	public Locale getLocale() {
		return locale;
	}

	public boolean isSelectAllControls() {
		return selectAllControls;
	}

}