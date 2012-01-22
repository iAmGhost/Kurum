package com.nabisoft.kurum;

import org.eclipse.swt.widgets.Display;

import com.nabisoft.kurum.ui.Window;
import com.nabisoft.kurum.ui.WindowFactory;

public class Main {

	public static void main (String [] args) {
	    Display display = new Display();
	    WindowFactory.setDisplay(display);
	    Window mainWindow = WindowFactory.create("DropboxLogin");

	    mainWindow.open();
	    while (!mainWindow.isDisposed()) {
	        if (!display.readAndDispatch()) display.sleep();
	    }
	    display.dispose();
	}
}
