package aero.minova.rcp.model.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.helper.IHelper;

/**
 * Das Modell für den Detailbereich
 *
 * @author saak
 */
public class MDetail {

	private HashMap<String, MField> fields = new HashMap<>();
	private List<MField> primaryFields = new ArrayList<>();
	private HashMap<String, MGrid> grids = new HashMap<>();
	private HashMap<String, MButton> buttons = new HashMap<>();
	private List<MSection> mSectionList = new ArrayList<>();

	private IHelper helper;

	private Map<String, Form> optionPages = new HashMap<>();
	private Map<String, Map<String, String>> optionPageKeys = new HashMap<>();

	private IDetailAccessor detailAccessor;

	/**
	 * Ein neues Feld dem Detail hinzufügen. Dabei muss selbst auf die Eindeutigkeit geachtet werden. Z.B.
	 * <ul>
	 * <li>"KeyLong" = Das Feld KeyLong der Detail-Maske</li>
	 * <li>"CustomerUserCode.UserCode" = Das Feld UserCode in der Maske CustomerUserCode.op.xml</li>
	 * </ul>
	 *
	 * @param field
	 *            das eigentliche Feld
	 */
	public void putField(MField field) {
		if (field == null) {
			return;
		}
		fields.put(field.getName(), field);
		field.setDetail(this);

		if (field.isPrimary()) {
			primaryFields.add(field);
		}
	}

	/**
	 * Ein neues MGrid dem Detail hinzufügen. Dabei muss selbst auf die Eindeutigkeit geachtet werden. Z.B. Um diese Einigkeit zu erreichen wird der
	 * Procedure-Suffix des Grid-Knoten verwendet. Dies ist ein Pflichtfeld!
	 *
	 * @param g
	 *            das MGrid
	 */
	public void putGrid(MGrid g) {
		if (g == null) {
			return;
		}
		grids.put(g.getId(), g);
	}

	public Collection<MGrid> getGrids() {
		return grids.values();
	}

	/**
	 * Liefert das MGrid mit der ID
	 *
	 * @param name
	 *            Name des Grids
	 * @return Das MGrid
	 */
	public MGrid getGrid(String name) {
		return grids.get(name);
	}

	public void putButton(MButton b) {
		if (b == null) {
			return;
		}
		buttons.put(b.getId(), b);
	}

	public Collection<MButton> getButtons() {
		return buttons.values();
	}

	public MButton getButton(String id) {
		return buttons.get(id);
	}

	/**
	 * Liefert das Feld mit dem Namen. Felder im Detail haben kein Präfix. Felder in einer OptionPage haben das Präfix aus der XBS. z.B.
	 * <ul>
	 * <li>"KeyLong" = Das Feld KeyLong der Detail-Maske</li>
	 * <li>"CustomerUserCode.UserCode" = Das Feld UserCode in der Maske CustomerUserCode.op.xml</li>
	 * </ul>
	 *
	 * @param name
	 *            Name des Feldes
	 * @return Das Feld
	 */
	public MField getField(String name) {
		return fields.get(name);
	}

	public Collection<MField> getFields() {
		return fields.values();
	}

	public MSection getPage(String id) {
		for (MSection m : mSectionList) {
			if (m.getId().equals(id)) {
				return m;
			}
		}
		return null;
	}

	public List<MSection> getMSectionList() {
		return mSectionList;
	}

	public void setMSectionList(List<MSection> mSectionList) {
		this.mSectionList = mSectionList;
	}

	public void addMSection(MSection mSection) {
		this.mSectionList.add(mSection);
	}

	public void addOptionPage(Form op) {
		this.optionPages.put(op.getDetail().getProcedureSuffix(), op);
	}

	public Form getOptionPage(String name) {
		return optionPages.get(name);
	}

	public void addOptionPageKeys(String name, Map<String, String> keysToValue) {
		this.optionPageKeys.put(name, keysToValue);
	}

	public Map<String, String> getOptionPageKeys(String name) {
		return optionPageKeys.get(name);
	}

	public Collection<Form> getOptionPages() {
		return optionPages.values();
	}

	public IHelper getHelper() {
		return helper;
	}

	public void setHelper(IHelper helper) {
		this.helper = helper;
	}

	public boolean allFieldsAndGridsValid() {
		for (MField field : fields.values()) {
			if (!field.isValid()) {
				return false;
			}
		}
		for (MGrid grid : grids.values()) {
			if (!grid.isValid()) {
				return false;
			}
		}
		return true;
	}

	public List<MField> getPrimaryFields() {
		return primaryFields;
	}

	public IDetailAccessor getDetailAccessor() {
		return detailAccessor;
	}

	public void setDetailAccessor(IDetailAccessor detailAccessor) {
		this.detailAccessor = detailAccessor;
	}
}
