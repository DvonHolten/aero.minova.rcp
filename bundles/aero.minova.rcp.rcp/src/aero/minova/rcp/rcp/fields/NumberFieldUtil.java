package aero.minova.rcp.rcp.fields;

import java.text.NumberFormat;
import java.util.Locale;

import org.eclipse.swt.widgets.Text;

public class NumberFieldUtil {

	private NumberFieldUtil() {}

	/**
	 * generiert aus den Properties
	 * <ul>
	 * <li>{@link FieldUtil#FIELD_MIN_VALUE}</li>
	 * <li>{@link FieldUtil#FIELD_MAX_VALUE}</li>
	 * <li>{@link FieldUtil#FIELD_DECIMALS}</li>
	 * <li>{@link FieldUtil#TRANSLATE_LOCALE}</li>
	 * </ul>
	 * des übergebenen Text-Widgets den Tooltip-Text für den Wert 0.0
	 * und trägt ihn in das Text-Widget ein.
	 * <br>
	 * Das Dezimal-Trennzeichen ist z.B. in Deutschland ein Komma,
	 * in vielen anderen Regionen ein Punkt.<br>
	 * Tausender-Trennzeichen werden hier nicht behandelt.
	 *
	 * <br>ToDo - ich würde setMessage() wegfallen lassen.
	 * Der Tooltip '0.0' ist wenig hilfreich, und es wird dadurch erschwert,
	 * dass der Aufrufer selbst eine bessere Message an das Text-Widget hängt.
	 *
	 * <br>ToDo - Vorzeichen?
	 *
	 * @param text das einzustellende Text-Widget
	 */
	public static void setMessage(Text text) {

		// Ziffern rechts vom Dezimal-Trennzeichen, Nachkomma-Stellen
		int decimals = (int) text.getData(FieldUtil.FIELD_DECIMALS);
		double maximum = (double) text.getData(FieldUtil.FIELD_MAX_VALUE);

		// eingestellte Locale definiert das Dezimal-Trennzeichen
		Locale locale = (Locale) text.getData(FieldUtil.TRANSLATE_LOCALE);

		int integer = 0; 	// Ziffern links vom Dezimal-Trennzeichen
		if (maximum >= Float.MAX_VALUE) { // bisschen unklar, was hier abgeht
			integer = 9 - decimals;
		} else {
			while (maximum > 1) {
				maximum /= 10;
				integer++;
			}
		}

		// DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
		NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
		numberFormat.setMaximumFractionDigits(decimals);
		numberFormat.setMinimumFractionDigits(decimals);
		numberFormat.setMaximumIntegerDigits(integer);
		numberFormat.setMinimumIntegerDigits(integer);
		text.setMessage(numberFormat.format(0.0d));
	}
}
