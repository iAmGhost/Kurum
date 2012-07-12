package kr.iamghost.kurum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertyUtil {
	private static PropertyUtil defaultProperty = null;
	private Properties property = null;
	private String propertyFilePath = null;

	static {
		defaultProperty = new PropertyUtil().loadDefaultFile();
	}
	
	protected void init(InputStream is) throws IOException {
		property = new Properties();
		property.load(is);
	}
	
	public static PropertyUtil getDefaultProperty() {
		return defaultProperty;
	}
	
	public PropertyUtil loadDefaultFile() {
		loadFile(propertyFilePath = Environment.APPDATA + "/Kurum/Kurum.properties");
		return this;
	}

	public PropertyUtil loadLocalFile(String path) {
		try {
			InputStream is = getClass().getResourceAsStream(path);
			init(is);
		} catch (Exception e) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return this;
	}
	
	public String getString(String key) {
		String data = property.getProperty(key);
		
		if (data == null) {
			return "";
		}
		
		return property.getProperty(key);
	}
	
	public void setString(String key, String value) {
		if (value.equals("")) {
			property.remove(key);
		} else {
			property.setProperty(key, value);
		}
		save();
	}
	
	public void save() {
		try {
			FileOutputStream os = new FileOutputStream(propertyFilePath);
			property.store(os, "");
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
