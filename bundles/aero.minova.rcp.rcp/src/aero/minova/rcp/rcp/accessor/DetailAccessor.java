package aero.minova.rcp.rcp.accessor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.Section;

import aero.minova.rcp.model.form.IDetailAccessor;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MSection;

public class DetailAccessor implements IDetailAccessor {

	private MDetail mDetail;
	private Control selectedControl;

	public DetailAccessor(MDetail mDetail) {
		this.mDetail = mDetail;
	}

	@Override
	public Control getSelectedControl() {
		return selectedControl;
	}

	@Override
	public void setSelectedControl(Control selectedControl) {
		this.selectedControl = selectedControl;
	}

	@Override
	public List<Section> getSectionList() {
		List<Section> sectionList = new ArrayList<>();
		for (MSection mSection : mDetail.getMSectionList()) {
			sectionList.add(mSection.getSectionAccessor().getSection());
		}
		return sectionList;
	}

}
