package kr.iamghost.kurum.ui;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import kr.iamghost.kurum.images.Images;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class WindowFactory {
	private static Image image;
	private static Display display;
	private static HashMap<String, String> windowList;
	
	static {
		windowList = new HashMap<String, String>();
		windowList.put("DropboxLogin", "DropboxLoginWindow");
		windowList.put("Main", "MainWindow");
		windowList.put("Log", "LogWindow");
		windowList.put("SyncManager", "SyncManagerWindow");
		windowList.put("AppConfigImport", "AppConfigImportWindow");
		
		display = Display.getCurrent();
		
		InputStream is = Images.class.getResourceAsStream("Kurum_512px.png");
		image = new Image(display, is);
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Window create(String windowName) {
		try {
			String packageName = WindowFactory.class.getPackage().getName();
			String windowClassName = packageName + "." + windowList.get(windowName);
			Constructor<?> windowClass = Class.forName(windowClassName).getConstructor(Display.class);
			Window instance = (Window) windowClass.newInstance(display);
			instance.setWindowName(windowName);
			instance.getShell().setImage(image);
			instance.init();
			return instance;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
