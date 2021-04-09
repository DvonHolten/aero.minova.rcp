package aero.minova.rcp.rcp.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;

public class ShowErrorDialogHandler {
	@Execute
	public static void execute(final Shell shell, String title, String message, Throwable t) { // create exception on purpose to demonstrate ErrorDialog
		MultiStatus status;
		status = createMultiStatus(t.getLocalizedMessage(), t);
		// show error dialog
		ErrorDialog.openError(shell, title, message, status);

	}

	private static MultiStatus createMultiStatus(String msg, Throwable t) {
		MultiStatus ms;
		List<Status> childStatuses = new ArrayList<>();
		StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
		for (StackTraceElement stackTrace : stackTraces) {
			Status status = new Status(IStatus.ERROR, "aero.minova.rcp.rcp", stackTrace.toString());
			childStatuses.add(status);
		}
		ms = new MultiStatus("aero.minova.rcp.rcp", IStatus.ERROR, childStatuses.toArray(new Status[] {}), t.toString(), t);
		return ms;
	}
}
