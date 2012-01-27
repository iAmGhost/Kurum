package kr.iamghost.kurum.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Window {
	final private Display display;
	final private Shell shell;
	
	public Window(Display display) {
		shell = new Shell(display);
		this.display = display;
		init();
	}
	
	public Window(Display display, int style) {
		shell = new Shell(display, style);
		this.display = display;
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

	public Display getDisplay() {
		return display;
	}
}
