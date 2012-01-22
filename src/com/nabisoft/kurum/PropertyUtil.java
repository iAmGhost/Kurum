package com.nabisoft.kurum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertyUtil {
	private Properties mProperty = null;
	private String mFilePath = null;
	
	protected void init(InputStream is) throws IOException {
		mProperty = new Properties();
		mProperty.load(is);
	}
	
	public PropertyUtil loadDefaultFile() {
		loadFile(mFilePath = Environment.APPDATA + "/Kurum.properties");
		
		return this;
	}
	public PropertyUtil loadLocalFile(String path) {
		try {
			InputStream is = getClass().getResourceAsStream(path);
			init(is);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return this;
	}
	
	public PropertyUtil loadFile(String filePath) {
		mFilePath = filePath;
		
		try {
			File file = new File(filePath);
			File folder = file.getParentFile();
			
			if (!file.isFile()) {
				file.createNewFile();
			}
			if (!folder.isDirectory()) {
				folder.mkdirs();
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
		return mProperty.getProperty(key);
	}
	
	public void setString(String key, String value) {
		mProperty.setProperty(key, value);
	}
	
	public void save() {
		try {
			FileOutputStream os = new FileOutputStream(mFilePath);
			mProperty.store(os, "");
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}
}
