package kr.iamghost.kurum;

import kr.nabisoft.kurum.ui.Window;
import kr.nabisoft.kurum.ui.WindowFactory;

import org.eclipse.swt.widgets.Display;


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