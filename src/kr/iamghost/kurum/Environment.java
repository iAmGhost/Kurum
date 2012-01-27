package kr.iamghost.kurum;

import org.apache.commons.lang3.SystemUtils;

public class Environment {
	public enum Platform {
		WINDOWS, MAC, LINUX;
	}
	
	public static String APPDATA;
	public static String APPDATA_LOCAL;
	public static String VERSION;
	public static String KURUMTITLE;
	
	static {
		String appDataDir = SystemUtils.getUserHome().toString().replaceAll("\\\\", "/");
		
		if (SystemUtils.IS_OS_WINDOWS) {
			APPDATA_LOCAL = appDataDir + "/AppData/Local";
			APPDATA = appDataDir + "/AppData/Roaming";
		}
		else if (SystemUtils.IS_OS_MAC_OSX) {
			APPDATA = appDataDir +"/Application Support";
		}
		
		VERSION = "1.0";
		KURUMTITLE = "Kurum " + VERSION;
	}
	
	public static String parsePath(String path) {
		path = path.replaceAll("\\\\", "/");
		path = path.replaceAll("%AppDataLocal%", APPDATA_LOCAL);
		return path;
	}
}
