package com.nabisoft.kurum;

import org.apache.commons.lang3.SystemUtils;

public class Environment {
	public static String APPDATA = getAppData();
	
	private static String getAppData() {
		String appDataDir = SystemUtils.getUserHome().toString();
		
		if (SystemUtils.IS_OS_WINDOWS) {
			return appDataDir += "/AppData/Roaming/Kurum";
		}
		else if (SystemUtils.IS_OS_MAC_OSX) {
			return appDataDir += "/Application Support/Kurum";
		}
		
		return appDataDir;
	}
}
