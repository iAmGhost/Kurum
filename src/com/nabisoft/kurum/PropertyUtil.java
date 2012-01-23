package com.nabisoft.kurum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertyUtil {
	private Properties property = null;
	private String propertyFilePath = null;
	
	protected void init(InputStream is) throws IOException {
		property = new Properties();
		property.load(is);
	}
	
	public PropertyUtil loadDefaultFile() {
		loadFile(propertyFilePath = Environment.APPDATA + "/Kurum.properties");
		
		return this;
	}
	public PropertyUtil loadLocalFile(String path) {
		try {
			InputStream is = getClass().getResourceAsStream(path);
			init(is);
		}
		catch (Exception e) {
			System.out.println("Cannot find local properties file: " + path);
			System.exit(1);
		}
		
		return this;
	}
	
	public PropertyUtil loadFile(String filePath) {
		propertyFilePath = filePath;
		
		try {
			File file = new File(filePath);
			File folder = file.getParentFile();
			
			if (!folder.isDirectory()) {
				folder.mkdirs();
			}
			if (!file.isFile()) {
				file.createNewFile();
			}
			
			FileInputStream is;
			is = new FileInputStream(filePath);
			init(is);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return this;
	}
	
	public String getString(String key) {
		return property.getProperty(key);
	}
	
	public void setString(String key, String value) {
		property.setProperty(key, value);
	}
	
	public void save() {
		try {
			FileOutputStream os = new FileOutputStream(propertyFilePath);
			property.store(os, "");
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}
}
