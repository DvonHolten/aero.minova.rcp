package aero.minova.workingtime.helper;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.component.annotations.Component;

import aero.minova.rcp.dataservice.IHelper;
import aero.minova.rcp.model.Value;

@Component
public class WorkingTimeHelper implements IHelper{

	Map<String, Control> controls;
	public static final String CONTROL_CONSUMER = "consumer";
	public static final String CONTROL_FIELD = "field";
	private Control startDate;
	private Control endDate;
	private Control reQty;
	private Control chQty;

	public WorkingTimeHelper() {
		System.out.println("Ich bin da: WorkingTimeHelper");
	}

	@Override
	public void setControls(Map<String, Control> controls) {
		this.controls = controls;
		initAccessor();
	}

	public void initAccessor() {
		startDate = controls.get("StartDate");
		endDate = controls.get("EndDate");
		reQty = controls.get("RenderedQuantity");
		chQty = controls.get("ChargedQuantity");

		startDate.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				calculateTime();
			}
		});
	}

	protected void calculateTime() {
		Value start = (Value) startDate.getData("value");
		Value end = (Value) endDate.getData("value");
		Instant iStart = start.getInstantValue();
		Instant iEnd = end.getInstantValue();
		Duration d = Duration.between(iEnd, iStart);
		long min = ChronoUnit.MINUTES.between(iEnd, iStart);
		getFloatFromMinutes(min);

	}

	public Float getFloatFromMinutes(long min) {
		Float f = null;
		f = (float) Math.round(min * 100.0 / 60.0);
		f = (float) (f / 100.0);
		return f;
	}

}
