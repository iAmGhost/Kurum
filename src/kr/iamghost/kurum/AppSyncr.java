package kr.iamghost.kurum;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;

import com.dropbox.client2.DropboxAPI.DropboxFileInfo;

public class AppSyncr implements ProcessWatcherListener {
	private final static int PROCESS_WATCH_PERIOD = 1000 * 5;
	private final static int AUTOMATIC_SYNC_PERIOD = 1000 * 60 * 5;
	private HashMap<String, AppConfig> appConfigs;
	private PropertyUtil kurumConfig;
	private ProcessWatcher processWatcher;
	private DropboxUtil dropbox;
	private Timer autoSyncTimer;
	
	public AppSyncr() {
		dropbox = new DropboxUtil();
		if (!dropbox.isLinked()) Global.set("DropboxLoginError", true);
	}
	
	public void syncAllApps() {
		if (dropbox.isLinked() == true)
		{
			for (AppConfig app : appConfigs.values()) {
				syncApp(app, false);
			}
		}
	}
	
	public void init() {
		refreshAppConfigs();
		refreshProcessWatcher();
		kurumConfig = new PropertyUtil().loadDefaultFile();
		startTimedSync();
	}
	
	public void startTimedSync() {
		autoSyncTimer = new Timer();
		autoSyncTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				syncAllApps();
			}
		}, 0, AUTOMATIC_SYNC_PERIOD);
	}
	
	public void refreshProcessWatcher() {
		processWatcher = new ProcessWatcher();
		
		for (AppConfig app : appConfigs.values()) {
			processWatcher.addProcess(app.getProcessName(), this);
		}
		processWatcher.start(PROCESS_WATCH_PERIOD);
	}
	
	public void refreshAppConfigs() {
		appConfigs = new HashMap<String, AppConfig>();
		AppConfigParser configParser = new AppConfigParser();
		
		File file = new File(Environment.parsePath("%AppData%/Kurum/AppConfigs"));
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			
			for (File singleFile : files) {
				AppConfig tempConfig;
				configParser.parse(singleFile.getAbsolutePath());
				tempConfig = configParser.getAppConfig();
				if (tempConfig != null)
				{
					appConfigs.put(tempConfig.getProcessName(), tempConfig);
				}
					
			}
		}
	}
	
	@Override
	public void onProcessDisappeared(ProcessWatcherEvent e) {
		syncApp(appConfigs.get(e.getProcessName()), true);
	}
	
	public void uploadToDropbox(AppConfig config) {
		Iterator<AppConfigFileEntry> it = config.getFilesIterator();
		
		String appName = config.getAppName();
		
		File zipFile = null;
		ZipArchiveOutputStream zos = null;
		
		try {
			zipFile = File.createTempFile(appName, ".zip");
			zipFile.deleteOnExit();
			zos = new ZipArchiveOutputStream(zipFile);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		while (it.hasNext()) {
			AppConfigFileEntry fileInfo = it.next();
			

			File file = new File(fileInfo.getOriginalPath());
			
			if (file.isFile()) {
				addFileToZip(zos, file, fileInfo.getDropboxPath());
			}
			else {
				addDirectoryToZip(zos, file, fileInfo.getDropboxPath());
			}
		}
		
		try {
			zos.closeArchiveEntry();
			zos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DropboxEntry upload = dropbox.upload(zipFile.getAbsolutePath(),
				config.getDropboxZipPath(), true);
		
		saveSyncInfo(upload);
	}
	
	public void downloadToLocal(AppConfig config) {
		Iterator<AppConfigFileEntry> it = config.getFilesIterator();
		
		String appName = config.getAppName();
		
		File tempFile = null;
		ZipFile zipFile = null;
		String tempZipPath = null;
		DropboxFileInfo download = null;
		
		try {
			tempFile = File.createTempFile(appName, ".zip");
			tempZipPath = tempFile.getAbsolutePath();
			tempFile.delete();
			
			download = dropbox.download(config.getDropboxZipPath(), tempZipPath);
			
			zipFile = new ZipFile(tempZipPath);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		while (it.hasNext()) {
			AppConfigFileEntry fileInfo = it.next();
			String tempFolder = Environment.parsePath("%Temp%/Kurum/" + appName + "/");
			Util.deleteDirectory(new File(tempFolder));
			
			extractZip(zipFile, tempFolder);
			
			if (fileInfo.isFile()) {
				File file = new File(tempFolder + fileInfo.getDropboxPath());
				File destFile = new File(fileInfo.getOriginalPath());
				destFile.mkdirs();
				file.renameTo(destFile);
			}
			else {
				copyDirectoryRecursively(tempFolder + fileInfo.getDropboxPath(),
						fileInfo.getOriginalPath());
			}
		}
		
		try {
			zipFile.close();
			new File(tempZipPath).delete();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		DropboxEntry meta = dropbox.getMetadata(config.getDropboxZipPath());
		saveSyncInfo(meta);
	}
	
	private void copyDirectoryRecursively(String dir, String dest) {
		File destDirectory = new File(dir);
		if (destDirectory.isDirectory()) Util.deleteDirectory(destDirectory);
		
		File directory = new File(dir);
		
		File[] files = directory.listFiles();
		
		for (File file : files) {
			String newDir = dir + "/" + file.getName();
			String filePath = dest + "/" + file.getName();

			if (file.isDirectory()) {
				copyDirectoryRecursively(newDir, filePath);
			}
			else
			{
				File destFile = new File(filePath);
				destFile.getParentFile().mkdirs();
				if (destFile.isFile()) destFile.delete();

				try {
				    FileReader in = new FileReader(file);
				    FileWriter out = new FileWriter(destFile);
					int c;
	
				    while ((c = in.read()) != -1)
				      out.write(c);
	
				    in.close();
				    out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void extractFileFromZip(ZipFile zip, ZipArchiveEntry entry, String destPath) {
		
		try {
			File file = new File(destPath);
			File folder = file.getParentFile();
			
			if (!folder.isDirectory()) {
				folder.mkdirs();
			}
			
			InputStream is = zip.getInputStream(entry);
			FileOutputStream os = new FileOutputStream(file);
			
			byte[] buffer = new byte[1024];
			int bytes_read = 0;
			
			os.write(is.read());
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void extractZip(ZipFile zip, String path) {
		Enumeration<ZipArchiveEntry> entries = zip.getEntries();
		
		while (entries.hasMoreElements()) {
			ZipArchiveEntry entry = entries.nextElement();
			Log.write(entry.getName());
			
			String newPath = path + "/" + entry.getName();
			
			if (entry.isDirectory()) {
				extractZip(zip, newPath);
			}
			else {
				extractFileFromZip(zip, entry, newPath);
			}
		}
	}
	
	public void addFileToZip(ZipArchiveOutputStream zos, File file, String name) {
		ZipArchiveEntry entry = new ZipArchiveEntry(file, name);
		
		try {
			zos.putArchiveEntry(entry);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addDirectoryToZip(ZipArchiveOutputStream zos, File dir, String name) {
		File[] files = dir.listFiles();
		
		for (File file : files) {
			String fileName = name + "/" + file.getName();
			
			if (file.isDirectory()) {
				addDirectoryToZip(zos, file, fileName);
			}
			else
			{
				addFileToZip(zos, file, fileName);
			}
		}
	}
	
	public void saveSyncInfo(DropboxEntry entry) {
		kurumConfig.setString(entry.fileName, entry.modifydate.toString());
		kurumConfig.save();
	}

	public void syncApp(AppConfig config, boolean force) {
		if (config != null) {
			String appName = config.getAppName();
			String archivePath = config.getDropboxZipPath();
			Date localDate = Util.stringToDate(kurumConfig.getString(appName + ".zip"));
			
			DropboxEntry lastSync = dropbox.getMetadata(archivePath);
			
			if (!lastSync.isValid || lastSync.isDeleted) {
				//First upload->Upload
				Log.write("First upload");
				uploadToDropbox(config);
			}
			
			else if(lastSync.isValid) {
				if (!lastSync.isDeleted && localDate == null) {
					//First sync->Download
					Log.write("First sync");
					downloadToLocal(config);
				}
				else if(lastSync.modifydate.after(localDate)) {
					//Dropbox is newer->Download
					Log.write("Dropbox is newer");
					downloadToLocal(config);
				}
				else if(lastSync.modifydate.before(localDate) && !force)
				{
					Log.write("Local is newer");
					//Local is (maybe) newer
					uploadToDropbox(config);
				}
				else if (force)
				{
					Log.write("Forcing upload");
					uploadToDropbox(config);
				}
				else
				{
					//Same, but will never reach here except timed sync
					Log.write("Same, POMF =3");
				}
			}
		}
		kurumConfig.save();
	}
}
