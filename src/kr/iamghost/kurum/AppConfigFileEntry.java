package kr.iamghost.kurum;

import java.io.File;

public class AppConfigFileEntry {
	private String originalPath;
	private String dropboxPath;
	private boolean isFile = true;
	
	public AppConfigFileEntry() {
		
	}
	
	public AppConfigFileEntry(String originalPath, String dropboxPath) {
		setOriginalPath(originalPath);
		setDropboxPath(dropboxPath);
	}
	
	public String getOriginalPath() {
		return originalPath;
	}
	
	public File getOriginalFile() {
		return new File(originalPath);
	}
	
	public void setOriginalPath(String originalPath) {
		this.originalPath = originalPath;
	}
	
	public String getDropboxPath() {
		return dropboxPath;
	}
	
	public void setDropboxPath(String dropboxPath) {
		this.dropboxPath = dropboxPath;
	}

	public boolean isFile() {
		return isFile;
	}

	public void setIsFile(boolean isFile) {
		this.isFile = isFile;
	}

}
