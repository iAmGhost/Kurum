package com.nabisoft.kurum;

import java.util.ResourceBundle;

public class Language {
	private final static String LANGFILE = "com.nabisoft.kurum.Messages";
	private final static ResourceBundle resource;
	
	static {
		 resource = ResourceBundle.getBundle(LANGFILE);
	}
	
	public static String getString(String key) {
		String value = key;
		try {
			value = resource.getString(key);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return value;
	}
	
	public static String getFormattedString(String key, String format, Object ... args) {
		String value = key;
		try {
			value = resource.getString(key);
			value = String.format(format, args);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		 return value;
	}
}
