package com.nabisoft.kurum;

import org.eclipse.swt.widgets.Display;

import com.nabisoft.kurum.ui.Window;
import com.nabisoft.kurum.ui.WindowFactory;

public class Main {

	public static void main (String [] args) {
		PropertyUtil a = new PropertyUtil().loadDefaultFile();
		a.setString("Hello", "hi");
		a.save();
		System.out.println(Environment.APPDATA);
		
	    Display display = new Display();
	    WindowFactory.setDisplay(display);
	    Window aa = WindowFactory.create("DropboxLogin");

	    while (!aa.isDisposed()) {
	        if (!display.readAndDispatch()) display.sleep();
	    }
	    display.dispose();
	}
}
