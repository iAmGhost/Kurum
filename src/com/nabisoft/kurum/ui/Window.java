package com.nabisoft.kurum.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Window {
	final protected Shell mShell;
	
	public Window(Display display) {
		mShell = new Shell(display);
		mShell.setText("BaseWindow");
		mShell.open();
	}
	
	public boolean isDisposed() {
		return mShell.isDisposed();
	}
}
