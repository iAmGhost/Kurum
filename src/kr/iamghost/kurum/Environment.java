package kr.iamghost.kurum;

import java.util.ArrayList;

import org.apache.commons.lang3.SystemUtils;

public class Environment {
	public static ArrayList<EnvironmentVariable> envVars;
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
			APPDATA = appDataDir +"/Library/Application Support";
			APPDATA_LOCAL = APPDATA;
		}
		
		VERSION = "1.0";
		KURUMTITLE = "Kurum " + VERSION;
		
		envVars = new ArrayList<EnvironmentVariable>();
		addVariable("LocalAppData", APPDATA_LOCAL);
		addVariable("AppData", APPDATA);
		addVariable("Temp", parsePath(System.getProperty("java.io.tmpdir").toString()));
	}
	
	public static void addVariable(String name, String value) {
		envVars.add(new EnvironmentVariable("%" + name + "%", value));
	}
	
	public static String parsePath(String path) {
		path = path.replaceAll("\\\\", "/");
		
		for (EnvironmentVariable ev : envVars) {
			path = path.replaceAll(ev.getName(), ev.getValue());
		}

		return path;
	}
}
