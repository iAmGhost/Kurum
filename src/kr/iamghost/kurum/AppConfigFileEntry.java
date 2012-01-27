package kr.iamghost.kurum;

public class AppConfigFileEntry {
	private String originalPath;
	private String dropboxPath;
	
	public AppConfigFileEntry() {
		
	}
	
	public AppConfigFileEntry(String originalPath, String dropboxPath) {
		setOriginalPath(originalPath);
		setDropboxPath(dropboxPath);
	}
	
	public String getOriginalPath() {
		return originalPath;
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

}
