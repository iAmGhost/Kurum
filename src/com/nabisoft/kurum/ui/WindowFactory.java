package com.nabisoft.kurum.ui;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.eclipse.swt.widgets.Display;

public class WindowFactory {
	private static Display mDisplay;
	
	public static void setDisplay(Display display) {
		mDisplay = display;
	}
	
	public static Display getDisplay(Display display) {
		return mDisplay;
	}
	
	public static Window create(String windowName) {
		try {
			HashMap<String, String> map = WindowFactory.getHash();
			String windowClassName = map.get(windowName);
			Constructor<?> windowClass = Class.forName(windowClassName).getConstructor(Display.class);
			return (Window) windowClass.newInstance(mDisplay);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static HashMap<String, String> getHash() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("DropboxLogin", "com.nabisoft.kurum.ui.DropboxLoginWindow");
		return map;
	}
}
