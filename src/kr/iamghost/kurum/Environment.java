package kr.iamghost.kurum;

import java.util.HashMap;

import javax.swing.JFileChooser;

import org.apache.commons.lang3.SystemUtils;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

public class Environment {
	public static HashMap<String, EnvironmentVariable> envVars;
	public static String APPDATA;
	public static String APPDATA_LOCAL;
	public static String VERSION;
	public static String KURUM;
	public static String KURUMTITLE;
	public static String STEAM;
	
	static {
		String appDataDir = SystemUtils.getUserHome().toString().replaceAll("\\\\", "/");
		if (SystemUtils.IS_OS_WINDOWS) {
			APPDATA_LOCAL = appDataDir + "/AppData/Local";
			APPDATA = appDataDir + "/AppData/Roaming";
			STEAM = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER,
					"Software\\Valve\\Steam", "SteamPath");
		}
		else if (SystemUtils.IS_OS_MAC_OSX) {
			APPDATA = appDataDir +"/Library/Application Support";
			APPDATA_LOCAL = APPDATA;
			STEAM = APPDATA + "/Steam";
		}
		KURUM = APPDATA + "/Kurum";
		VERSION = "1.0";
		KURUMTITLE = "Kurum " + VERSION;
		
		envVars = new HashMap<String, EnvironmentVariable>();
		addVariable("LocalAppData", APPDATA_LOCAL);
		addVariable("AppData", APPDATA);
		addVariable("Temp", parsePath(System.getProperty("java.io.tmpdir").toString()));
		addVariable("Documents", parsePath(new JFileChooser().getFileSystemView().
				getDefaultDirectory().getAbsolutePath()));
		addVariable("Steam", STEAM);
	}
	
	public static EnvironmentVariable getVariableData(String name) {
		return envVars.get(name);
	}
	
	public static void addVariable(String name, String value) {
		envVars.put(name, new EnvironmentVariable("%" + name + "%", value));
	}
	
	public static String parsePath(String path) {
		path = path.replaceAll("\\\\", "/");
		
		for (EnvironmentVariable ev : envVars.values()) {
			path = path.replaceAll(ev.getName(), ev.getValue());
		}

		return path;
	}
}
