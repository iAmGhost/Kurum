package kr.iamghost.kurum.ui;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.eclipse.swt.widgets.Display;

public class WindowFactory {
	private static Display mDisplay;
	private static HashMap<String, String> mWindows;
	
	static {
		mWindows = new HashMap<String, String>();
		mWindows.put("DropboxLogin", "DropboxLoginWindow");
		mWindows.put("Main", "MainWindow");
	}
	
	public static void setDisplay(Display display) {
		mDisplay = display;
	}
	
	public static Display getDisplay(Display display) {
		return mDisplay;
	}
	
	public static Window create(String windowName) {
		try {
			String packageName = WindowFactory.class.getPackage().getName();
			String windowClassName = packageName + "." + mWindows.get(windowName);
			Constructor<?> windowClass = Class.forName(windowClassName).getConstructor(Display.class);
			return (Window) windowClass.newInstance(mDisplay);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
