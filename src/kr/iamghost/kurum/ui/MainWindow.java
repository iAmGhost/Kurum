package kr.iamghost.kurum.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public class MainWindow extends Window{

	protected boolean quit = false;
	
	public MainWindow(Display display) {
		super(display);
	}
	
	public void init() {
		Shell shell = getShell();
		shell.addShellListener(new ShellListener() {
			@Override
			public void shellActivated(ShellEvent e) {}
			@Override
			public void shellClosed(ShellEvent e) {
				getShell().setVisible(false);
				e.doit = false;
			}
			@Override
			public void shellDeactivated(ShellEvent e) {}
			@Override
			public void shellDeiconified(ShellEvent e) {}
			@Override
			public void shellIconified(ShellEvent e) {}
		});
		
		final Tray tray = getDisplay().getSystemTray();
		if (tray != null)
		{
			TrayItem item = new TrayItem(tray, SWT.NONE);
			
		}
	}
}
