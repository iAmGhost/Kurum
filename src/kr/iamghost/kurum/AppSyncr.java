package kr.iamghost.kurum;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;

import com.dropbox.client2.DropboxAPI.Entry;

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
		for (AppConfig app : appConfigs.values()) {
			syncApp(app, false);
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
				appConfigs.put(tempConfig.getProcessName(), tempConfig);
			}
		}
	}
	
	@Override
	public void onProcessDisappeared(ProcessWatcherEvent e) {
		syncApp(appConfigs.get(e.getProcessName()), true);
	}
	
	public void uploadToDropbox(AppConfig config) {
		Iterator<AppConfigFileEntry> it = config.getFilesIterator();
		
		while (it.hasNext()) {
			AppConfigFileEntry fileEntry = it.next();
			String originalPath = Environment.parsePath(fileEntry.getOriginalPath());
			String dropboxPath = "/Apps/" + config.getAppName();
			dropboxPath = dropboxPath + fileEntry.getDropboxPath();
			dropboxPath = Environment.parsePath(dropboxPath);
			
			File dir = new File(originalPath);
			
			if (!fileEntry.isFile()) {
				try {
					List<File> files = FileListing.getFileListing(dir);
					for (File file : files) {
						String filePath = file.getAbsolutePath();
						String fileName = filePath.replaceFirst(
								Matcher.quoteReplacement(dir.getAbsolutePath()), "");
						//Log.write(dropboxPath + fileName);
						if (file.isFile())
							dropbox.upload(filePath,
									dropboxPath + Environment.parsePath(fileName), true);
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else
			{
				if (dir.isFile())
					dropbox.upload(originalPath, dropboxPath, true);
			}
		}
		String appName = config.getAppName();
		String lastSyncPath = config.getLastSyncPath();
		DropboxEntry lastSync = dropbox.uploadText(new Date().toString(), lastSyncPath);
		kurumConfig.setString("date_" + appName, lastSync.modifydate.toString());
	}
	
	public void downloadToLocal(AppConfig config) {
		Iterator<AppConfigFileEntry> it = config.getFilesIterator();
		
		while (it.hasNext()) {
			AppConfigFileEntry fileEntry = it.next();
			String originalPath = Environment.parsePath(fileEntry.getOriginalPath());
			String dropboxPath = "/Apps/" + config.getAppName();
			dropboxPath = dropboxPath + fileEntry.getDropboxPath();
			dropboxPath = Environment.parsePath(dropboxPath);
			
			if (!fileEntry.isFile())
			{
				downloadFolderRecursive(dropboxPath, originalPath);
			}
			else
			{
				dropbox.download(dropboxPath, originalPath);
			}
		}
		
		String appName = config.getAppName();
		String lastSyncPath = config.getLastSyncPath();
		DropboxEntry lastSync = dropbox.getMetadata(lastSyncPath);
		kurumConfig.setString("date_" + appName, lastSync.modifydate.toString());
	}
	
	public void downloadFolderRecursive(String path, String savePath) {
		DropboxEntry meta = dropbox.getMetadata(path);
		List<Entry> entries = meta.getEntry().contents;
		
		for (Entry entry : entries) {
			String destPath = "";
			
			if (entry.isDir)
			{
				destPath = savePath + entry.path.replace(path, "");
				downloadFolderRecursive(entry.path, destPath);
			}
			else
			{
				destPath = savePath + "/" + entry.path.replace(path, "");
				dropbox.download(entry.path, destPath);
			}
		}
	}
	
	public void syncApp(AppConfig config, boolean force) {
		if (config != null) {
			String appName = config.getAppName();
			String lastSyncPath = config.getLastSyncPath();
			Date localDate = Util.stringToDate(kurumConfig.getString("date_" + appName));
			DropboxEntry lastSync = dropbox.getMetadata(lastSyncPath);
			
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
