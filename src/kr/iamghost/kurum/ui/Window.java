package kr.iamghost.kurum.ui;

import kr.iamghost.kurum.Global;

import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class Window {
	final private Display display;
	final private Shell shell;
	private String windowName;
	private String lastWindowName;
	private boolean isJumped = false;
	
	public Window(Display display) {
		shell = new Shell(display);
		this.display = display;
		initWindow();
	}
	
	public Window(Display display, int style) {
		shell = new Shell(display, style);
		this.display = display;
		initWindow();
	}
	
	private void initWindow() {
		shell.setText("BaseWindow");
		
		shell.addShellListener(new ShellListener() {
			
			@Override
			public void shellIconified(ShellEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void shellDeiconified(ShellEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void shellDeactivated(ShellEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void shellClosed(ShellEvent e) {
				if (!isJumped()) {
					Global.set("LastWindowClosed", true);
				}
			}
			
			@Override
			public void shellActivated(ShellEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		centre();
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
		
	}
	
	public Shell getShell() {
		return shell;
	}

	public boolean isDisposed() {
		return getShell().isDisposed();
	}
	
	public void jumpToNewWindow(String windowName) {
		Window newWindow = WindowFactory.create(windowName);
		
		newWindow.setLastWindowName(this.windowName);
		newWindow.open();
		isJumped = true;
		close();
	}
	
	public void hide() {
		getShell().setVisible(false);
	}
	
	public void close() {
		getShell().close();
	}
	
	public void open() {
		centre();
		getShell().open();
	}

	public void show(boolean raise) {
		shell.setVisible(true);
		
		if (raise) {
			shell.forceActive();
		}
	}

	public Display getDisplay() {
		return display;
	}

	public String getWindowName() {
		return windowName;
	}

	public void setWindowName(String windowName) {
		this.windowName = windowName;
	}

	public String getLastWindowName() {
		return lastWindowName;
	}

	public void setLastWindowName(String lastWindowName) {
		this.lastWindowName = lastWindowName;
	}

	public boolean isJumped() {
		return isJumped;
	}

	public void setJumped(boolean isJumped) {
		this.isJumped = isJumped;
	}
}
