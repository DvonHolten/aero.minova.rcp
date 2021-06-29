package aero.minova.rcp.model.form;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.Section;

public class MSection {

	// nur für die 1. Page möglich. Befindet sich immer an oberster Stelle
	private boolean isHead;
	// Zustand der Page (invisible, open, closed, minimized)
	private String status;
	// Das übergeordnete Element, welches alle Sections enthält
	private MDetail mDetail;
	// Liste an allen mit Tab erreichbaren Feldern der Section. Festlegen dieser
	// anhand der Preferences
	private List<MField> tabList = new ArrayList<>();
	// Text für diese Page
	private String label;
	// Symbol für diese Page
	private Image icon;
	private Section section;

	private Control control;

	/**
	 * Erstellt eine neue MSection.
	 *
	 * @param isHead
	 *            makiert die Section als HEAD. Es gibt nur einen HEAD in einem Part.
	 * @param status
	 *            der Zustand der Section (invisible, open, closed, minimized)
	 * @param mDetail
	 *            das Detail in dem die Section erstellt werden soll.
	 * @param label
	 *            das Label der Section.
	 * @param control
	 *            das Twistie Element einer Section. Der HEAD hat keinen Twistie.
	 * @param section
	 *            das org.eclipse.ui.forms.widgets.Section Element
	 */
	public MSection(boolean isHead, String status, MDetail mDetail, String label, Control control, Section section) {
		this.isHead = isHead;
		this.status = status;
		this.mDetail = mDetail;
		this.label = label;
		this.control = control;
		this.section = section;
	}

	public boolean isHead() {
		return isHead;
	}

	public void setHead(boolean isHead) {
		this.isHead = isHead;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public MDetail getmDetail() {
		return mDetail;
	}

	public void setmDetail(MDetail mDetail) {
		this.mDetail = mDetail;
	}

	/**
	 * @return Twistie wenn es vorhanden ist.
	 */
	public Control getSectionControl() {
		return control;
	}

	public void setSection(Control section) {
		this.control = section;
	}

	public List<MField> getTabList() {
		return tabList;
	}

	public void setTabList(List<MField> tabList) {
		this.tabList = tabList;
	}

	public void addTabField(MField mField) {
		this.tabList.add(mField);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Image getIcon() {
		return icon;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
	}

	/**
	 * Liefert das org.eclipse.ui.forms.widgets.Section Element zurück.
	 * 
	 * @return section
	 */
	public Section getSection() {
		return section;
	}
}
