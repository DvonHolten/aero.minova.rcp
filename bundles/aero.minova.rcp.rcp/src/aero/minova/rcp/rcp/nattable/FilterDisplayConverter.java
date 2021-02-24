package aero.minova.rcp.rcp.nattable;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;

import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.DateTimeType;
import aero.minova.rcp.model.FilterValue;
import aero.minova.rcp.rcp.util.DateTimeUtil;
import aero.minova.rcp.rcp.util.OperatorExtractionUtil;
import aero.minova.rcp.rcp.util.TimeUtil;

public class FilterDisplayConverter extends DisplayConverter {

	private Locale locale;
	private DataType datatype;
	private DateTimeType datetimetype;
	private ZoneId zoneId;

	public FilterDisplayConverter(DataType datatype, Locale locale, DateTimeType datetimetype, ZoneId zoneId) {
		this.locale = locale;
		this.datatype = datatype;
		this.datetimetype = datetimetype;
		this.zoneId = zoneId;
	}

	public FilterDisplayConverter(DataType datatype, Locale locale, DateTimeType datetimetype) {
		this.locale = locale;
		this.datatype = datatype;
		this.datetimetype = datetimetype;
	}

	public FilterDisplayConverter(DataType datatype) {
		this.datatype = datatype;
	}

	@Override
	public Object canonicalToDisplayValue(Object canonicalValue) {
		if (canonicalValue instanceof FilterValue) {
			FilterValue cv = (FilterValue) canonicalValue;
			String val = "";
			switch (datatype) {
			case INSTANT:
				switch (datetimetype) {
				case DATE:
					val = DateTimeUtil.getDateString((Instant) cv.getFilterValue().getValue(), locale);
					break;
				case TIME:
					val = TimeUtil.getTimeString((Instant) cv.getFilterValue().getValue(), locale);
					break;
				case DATETIME:
					val = DateTimeUtil.getDateTimeString((Instant) cv.getFilterValue().getValue(), locale, zoneId);
					break;
				}
				break;
			case ZONED:
				val = DateTimeUtil.getDateTimeString((Instant) cv.getFilterValue().getValue(), locale, zoneId);
				break;
			default:
				val = cv.getFilterValue().getValue().toString();
			}
			return cv.getValue().toString() + " " + val;
		}
		return null;
	}

	@Override
	public Object displayToCanonicalValue(Object displayValue) {
		if (displayValue instanceof String) {
			String valueString = (String) displayValue;

			int operatorPos = OperatorExtractionUtil.getOperatorEndIndex(valueString);
			String operator = valueString.substring(0, operatorPos);

			String filterValueString = valueString.substring(operatorPos).strip();
			Object filterValue = null;

			if (filterValueString.length() > 0) {
				switch (datatype) {
				case INSTANT:
					switch (datetimetype) {
					case DATE:
						filterValue = DateTimeUtil.getDate(filterValueString);
						break;
					case TIME:
						filterValue = TimeUtil.getTime(filterValueString);
						break;
					case DATETIME:
						// TODO: .getDateTime(String s) Methode hinzufügen?
						break;
					}
					break;
				case ZONED:
					// TODO: .getDateTime(String s) Methode hinzufügen?
					break;
				case INTEGER:
					filterValue = Integer.parseInt(filterValueString);
					break;
				case DOUBLE:
					filterValue = Double.parseDouble(filterValueString);
					break;
				case BOOLEAN:
					filterValue = Boolean.parseBoolean(filterValueString);
					break;
				default:
					filterValue = filterValueString;
				}

				if (filterValue != null) {
					if (operator.equals(""))
						operator = "=";
					return new FilterValue(operator, filterValue);
				}
			}
		}
		return null;
	}

}
