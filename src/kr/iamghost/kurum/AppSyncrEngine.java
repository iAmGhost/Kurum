package kr.iamghost.kurum;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang3.SystemUtils;

public class AppSyncrEngine {
	private AppConfig appConfig;
	private ZipUtil zipFile;
	private PropertyUtil kurumConfig;
	private String tempDirectoryPath;
	
	public AppSyncrEngine(AppConfig appConfig) {
		this.appConfig = appConfig;
		kurumConfig = PropertyUtil.getDefaultProperty();
	}
	
	public void doDefaultUpload() {
		beginUpload();
		
		Iterator<AppConfigFileEntry> it = appConfig.getFilesIterator();
		
		while (it.hasNext()) {
			AppConfigFileEntry fileInfo = it.next();

			addUpload(fileInfo);
		}
		
		endUpload();
	}
	
	public void doDefaultDownload() {
		beginDownload();
		
		Iterator<AppConfigFileEntry> it = appConfig.getFilesIterator();
		
		while (it.hasNext()) {
			AppConfigFileEntry fileInfo = it.next();
			
			if (fileInfo.isNeedCleanup()) {
				FileUtil.delete(fileInfo.getOriginalFile());
			}
			
			addDownload(fileInfo.getDropboxPath(), fileInfo.getOriginalPath());
		}
		
		endDownload();
	}
	
	public void beginUpload() {
		String appName = appConfig.getAppName();
		
		try {
			File tempZipFile = File.createTempFile(appName, ".zip");
			zipFile = new ZipUtil().createZip(tempZipFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addUpload(AppConfigFileEntry fileEntry) {
		zipFile.add(fileEntry);
	}
	
	public void addUpload(String filePath, String pathInZipFile) {
		addUpload(parsePath(filePath), pathInZipFile, null);
	}
	
	public void addUpload(String filePath, String pathInZipFile, ArrayList<String> excludes) {
		File file = new File(parsePath(filePath));
		
		if (file.exists()) {
			zipFile.add(file, pathInZipFile, excludes);
		}
	}
	
	public boolean isWindows() {
		return SystemUtils.IS_OS_WINDOWS;
	}
	
	public boolean isMac() {
		return SystemUtils.IS_OS_MAC;
	}
	
	public boolean isLinux() {
		return SystemUtils.IS_OS_LINUX;
	}
	
	public void endUpload() {
		DropboxUtil dropbox = DropboxUtil.getDefaultDropbox();
		
		zipFile.save();
		zipFile.close();
		
		DropboxEntry upload = dropbox.upload(zipFile.getFile(),
				appConfig.getDropboxZipPath(),
				kurumConfig.getString(appConfig.getAppName() + ".zip_rev"), true);
		
		saveSyncInfo(upload);
		
		FileUtil.delete(zipFile.getFile());
	}
	
	public void beginDownload() {
		String appName = appConfig.getAppName();
		
		File tempFile = null;
		DropboxUtil dropbox = DropboxUtil.getDefaultDropbox();
		
		try {
			tempFile = File.createTempFile(appName, ".zip");
			dropbox.download(appConfig.getDropboxZipPath(), tempFile);
			zipFile = new ZipUtil().loadZip(tempFile);
			
			File tempDirectory;
			
			tempDirectory = File.createTempFile("Kurum", "");
			tempDirectoryPath = tempDirectory.getAbsolutePath();
			
			tempDirectory.delete();
			zipFile.extract(tempDirectory);
			zipFile.close();
			
			zipFile.getFile().delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addDownload(String dropboxPath, String targetPath) {
		String path = tempDirectoryPath + "/" + dropboxPath;
		
		FileUtil.copy(new File(path), new File(parsePath(targetPath)));
	}
	
	public void endDownload() {
		DropboxUtil dropbox = DropboxUtil.getDefaultDropbox();
		FileUtil.delete(tempDirectoryPath);
		
		DropboxEntry meta = dropbox.getMetadata(appConfig.getDropboxZipPath());
		saveSyncInfo(meta);
		
	}
	
	public void log(String line) {
		Log.write(line);
	}
	
	public String parsePath(String path) {
		return Environment.parsePath(path);
	}
	
	public void test() {
		Log.write("test");
	}
	
	public void saveSyncInfo(DropboxEntry entry) {
		kurumConfig.setString(entry.fileName + "_date", entry.modifydate.toString());
		kurumConfig.setString(entry.fileName + "_rev", entry.rev);
	}
	
	public AppConfig getAppConfig() {
		return appConfig;
	}
	
	public void setAppConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
	}
}
