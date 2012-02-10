package kr.iamghost.kurum;

import kr.iamghost.kurum.ui.Window;
import kr.iamghost.kurum.ui.WindowFactory;

import org.eclipse.swt.widgets.Display;


public class Main {

	public static void main (String [] args) {
	    Display display = new Display();

	    Window mainWindow = WindowFactory.create("Main");
	    
	    while (!mainWindow.isDisposed()) {
	        if (!display.readAndDispatch()) display.sleep();
	    }
	    
	    display.dispose();
	    System.exit(0);
	}
}
