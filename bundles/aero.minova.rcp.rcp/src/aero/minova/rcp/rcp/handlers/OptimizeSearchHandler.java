package aero.minova.rcp.rcp.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.dialogs.PasswordDialog;

public class OptimizeSearchHandler {
	
	@Execute
	public void execute(Shell shell) {
		System.out.println("Optimize NatTable Search Part");
		
	}

}
