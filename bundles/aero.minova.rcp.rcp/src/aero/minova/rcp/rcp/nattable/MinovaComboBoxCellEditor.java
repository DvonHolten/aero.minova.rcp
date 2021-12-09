package aero.minova.rcp.rcp.nattable;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;
import org.eclipse.nebula.widgets.nattable.ui.matcher.LetterOrDigitKeyEventMatcher;
import org.eclipse.nebula.widgets.nattable.widget.EditModeEnum;
import org.eclipse.nebula.widgets.nattable.widget.NatCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class MinovaComboBoxCellEditor extends ComboBoxCellEditor {

	private GridLookupContentProvider contentProvider;
	private NatCombo combo;
	private int selectionIndex;

	/**
	 * Create a new single selection {@link MinovaComboBoxCellEditor} based on the given list of items, showing the default number of items in the dropdown of
	 * the combo.
	 */
	public MinovaComboBoxCellEditor(GridLookupContentProvider contentProvider) {
		super(contentProvider.getValues(), NatCombo.DEFAULT_NUM_OF_VISIBLE_ITEMS);
		this.contentProvider = contentProvider;
	}

	private EHandlerService getHandlerService(Control control) {
		return (EHandlerService) control.getParent().getData("EHandlerService");
	}

	private ECommandService getCommandService(Control control) {
		return (ECommandService) control.getParent().getData("ECommandService");
	}

	@Override
	public boolean commit(MoveDirectionEnum direction) {
		selectionIndex = combo.getSelectionIndex();
		setCanonicalValue(contentProvider.getValues());
		boolean commited = super.commit(direction);
		parent.forceFocus();
		return commited;
	}

	@Override
	public Object getCanonicalValue() {
		// Item selected from list
		if (selectionIndex > 0) {
			return contentProvider.getValues().get(selectionIndex);
		} else {
			return contentProvider.getValues().get(0);
		}
	}

	/**
	 * Registers special listeners to the {@link NatCombo} regarding the {@link EditModeEnum}, that are needed to commit/close or change the visibility state of
	 * the {@link NatCombo} dependent on UI interactions.
	 *
	 * @param combo
	 *            The {@link NatCombo} to add the listeners to.
	 */
	@Override
	protected void addNatComboListener(final NatCombo combo) {
		this.combo = combo;
		combo.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent event) {
				if ((event.keyCode == SWT.CR) || (event.keyCode == SWT.KEYPAD_CR)) {
					combo.getParent().forceFocus();
					commit(MoveDirectionEnum.NONE);
					Map<String, String> parameter = new HashMap<>();
					ParameterizedCommand command = getCommandService(combo).createCommand("aero.minova.rcp.rcp.command.traverseenter", parameter);
					EHandlerService handlerService = getHandlerService(combo);
					handlerService.executeHandler(command);
					close();
				} else if (event.keyCode == SWT.ESC) {
					if (editMode == EditModeEnum.INLINE) {
						close();
					} else {
						combo.hideDropdownControl();
					}
				}
			}

		});

		combo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				commit(MoveDirectionEnum.NONE, (!MinovaComboBoxCellEditor.this.multiselect && MinovaComboBoxCellEditor.this.editMode == EditModeEnum.INLINE));
				if (!MinovaComboBoxCellEditor.this.multiselect && MinovaComboBoxCellEditor.this.editMode == EditModeEnum.DIALOG) {
					// hide the dropdown after a value was selected in the combo
					// in a dialog
					combo.hideDropdownControl();
				}
			}
		});

		Text text = (Text) combo.getChildren()[0];
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.keyCode == SWT.ARROW_DOWN || event.keyCode == SWT.ARROW_UP) {
					combo.showDropdownControl();

					// ensure the arrow key events do not have any further
					// effect
					event.doit = false;
				} else if (LetterOrDigitKeyEventMatcher.isLetterOrDigit(event.character) || (event.keyCode == SWT.DEL || event.keyCode == SWT.BS)) {

					String entry = text.getText() + event.character;
					if (event.keyCode == SWT.BS && !text.getText().isEmpty()) {
						String cleanedEntry = entry.substring(0, entry.indexOf(SWT.BS) - 1) + entry.substring(entry.indexOf(SWT.BS) + 1);
						combo.setItems(contentProvider.filterContent(cleanedEntry));
					} else if (event.keyCode == SWT.DEL && !text.getText().isEmpty()) {
						String cleanedEntry = entry.substring(0, entry.indexOf(SWT.DEL)) + entry.substring(entry.indexOf(SWT.DEL) + 1);
						combo.setItems(contentProvider.filterContent(cleanedEntry));
					} else if (text.getText().isEmpty()) {
						combo.setItems(contentProvider.getOriginalValueArray());
					} else {
						combo.setItems(contentProvider.filterContent(entry));
					}

				}
			}
		});

		// Bei Klick auf den Pfeil Lookup Content aktualisieren (Zelle muss deaktiviert werden damit neue Werte angezeigt werden)
		Control arrow = combo.getChildren()[1];
		arrow.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				contentProvider.update();
			}
		});

		if (editMode == EditModeEnum.INLINE) {
			combo.addShellListener(new ShellAdapter() {
				@Override
				public void shellClosed(ShellEvent e) {
					close();
				}
			});
		}

		if (editMode == EditModeEnum.DIALOG) {
			combo.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					combo.hideDropdownControl();
				}
			});
		}
	}

}
