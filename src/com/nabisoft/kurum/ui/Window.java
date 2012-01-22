package com.nabisoft.kurum.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Window {
	final private Shell shell;
	
	public Window(Display display) {
		shell = new Shell(display);
		init();
	}
	
	public Window(Display display, int style) {
		shell = new Shell(display, style);
		init();
	}
	
	public void init() {
		shell.setText("BaseWindow");
	}
	
	public Shell getShell() {
		return shell;
	}

	public boolean isDisposed() {
		return getShell().isDisposed();
	}
	
	public void open() {
		getShell().open();
	}
}
