package kr.iamghost.kurum;

import java.util.HashMap;

import javax.swing.JFileChooser;

import org.apache.commons.lang3.SystemUtils;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;

public class Environment {
	public static HashMap<String, EnvironmentVariable> envVars = new HashMap<String, EnvironmentVariable>();
	public static String APPDATA;
	public static String APPDATA_LOCAL;
	public static String VERSION;
	public static String KURUM;
	public static String KURUMTITLE;
	public static String STEAM;
	
	static {
		String homeDir = SystemUtils.getUserHome().toString().replaceAll("\\\\", "/");
		if (SystemUtils.IS_OS_WINDOWS) {
			
			try {
				APPDATA = parsePath(System.getenv("APPDATA"));
			}
			catch (NullPointerException e) {
				APPDATA = parsePath(System.getProperty("user.home") + "/AppData/Roaming");
			}
			
			try {
				APPDATA_LOCAL = parsePath(System.getenv("LOCALAPPDATA"));
			}
			catch (NullPointerException e) {
				APPDATA_LOCAL = APPDATA;
			}
			
			try {
				STEAM = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER,
						"Software\\Valve\\Steam", "SteamPath");
			}
			catch (Win32Exception e) {
				STEAM = "";
			}
		} else if (SystemUtils.IS_OS_MAC_OSX) {
			APPDATA = homeDir + "/Library/Application Support";
			APPDATA_LOCAL = APPDATA;
			STEAM = APPDATA + "/Steam";
		} else if (SystemUtils.IS_OS_LINUX) {
			APPDATA = homeDir + "/.local/share";
			APPDATA_LOCAL = APPDATA;
			STEAM = APPDATA + "/Steam";
		}
		
		KURUM = APPDATA + "/Kurum";
		VERSION = "#20140203";
		KURUMTITLE = "Kurum " + VERSION;
		
		addVariable("LocalAppData", APPDATA_LOCAL);
		addVariable("AppData", APPDATA);
		addVariable("Temp", parsePath(System.getProperty("java.io.tmpdir").toString()));
		addVariable("Documents", parsePath(new JFileChooser().getFileSystemView().
				getDefaultDirectory().getAbsolutePath()));
		addVariable("Steam", STEAM);
		addVariable("Home", parsePath(System.getProperty("user.home")));
	}
	
	public static EnvironmentVariable getVariableData(String name) {
		return envVars.get(name);
	}
	
	public static void addVariable(String name, String value) {
		envVars.put(name, new EnvironmentVariable("%" + name + "%", value));
	}
	
	public static void removeVariable(String name) {
		envVars.remove(name);
	}
	
	public static String parsePath(String path) {
		path = path.replaceAll("\\\\", "/");
		
		for (EnvironmentVariable ev : envVars.values()) {
			if (ev.getValue() != null) {
				path = path.replaceAll(ev.getName(), ev.getValue());
			}
		}

		return path;
	}
}
