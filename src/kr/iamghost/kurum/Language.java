package kr.iamghost.kurum;

import java.util.ResourceBundle;

public class Language {
	private final static String LANGFILE = "kr.iamghost.kurum.Messages";
	private final static ResourceBundle resource;
	
	static {
		 resource = ResourceBundle.getBundle(LANGFILE);
	}
	
	public static String getString(String key) {
		String value = key;
		
		try {
			value = resource.getString(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return value;
	}
	
	public static String getFormattedString(String key, Object ... args) {
		String value = key;
		
		try {
			value = resource.getString(key);
			value = String.format(value, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		 return value;
	}
}
