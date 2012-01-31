package kr.iamghost.kurum.ui;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
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
	
	public void centre() {
		Monitor primary = display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		
		int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    
	    shell.setLocation(x, y);
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
