package com.nabisoft.kurum;

import org.apache.commons.lang3.SystemUtils;

public class Environment {
	public static String APPDATA;
	public static String APPDATA_LOCAL;
	
	static {
		String appDataDir = SystemUtils.getUserHome().toString();
		
		if (SystemUtils.IS_OS_WINDOWS) {
			APPDATA_LOCAL = appDataDir + "/AppData/Local";
			APPDATA = appDataDir + "/AppData/Roaming/Kurum";
		}
		else if (SystemUtils.IS_OS_MAC_OSX) {
			APPDATA = appDataDir +"/Application Support/Kurum";
		}
	}
}
