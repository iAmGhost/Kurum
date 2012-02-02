package kr.iamghost.kurum;

import java.util.ArrayList;
import java.util.Iterator;

public class AppConfig {
	private String appTitle;
	private String appName;
	private String processName;
	private ArrayList<AppConfigFileEntry> files = new ArrayList<AppConfigFileEntry>();
	
	public String getAppName() {
		return appName;
	}
	
	public void setAppName(String name) {
		appName = name;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcess(String processName) {
		this.processName = processName;
	}
	
	public void addFile(AppConfigFileEntry file) {
		boolean found = false;
		
		if (!found)
			files.add(file);
	}
	
	public Iterator<AppConfigFileEntry> getFilesIterator() {
		return files.iterator();
	}

	public String getAppTitle() {
		return appTitle;
	}

	public void setAppTitle(String appTitle) {
		this.appTitle = appTitle;
	}

	public String getDropboxZipPath() {
		return "/Data/" + appName + ".zip";
	}
}
