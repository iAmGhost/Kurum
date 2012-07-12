package kr.iamghost.kurum;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import kr.iamghost.kurum.LuaEngine.LuaMode;

import org.eclipse.swt.widgets.Display;

public class AppSyncr implements ProcessWatcherListener, GlobalEventListener {
	private final static int PROCESS_WATCH_PERIOD = 1000 * 5;
	private final static int AUTOMATIC_SYNC_PERIOD = 1000 * 60 * 5;
	private HashMap<String, AppConfig> appConfigs;
	private PropertyUtil kurumConfig;
	private ProcessWatcher processWatcher;
	private DropboxUtil dropbox;
	private Timer autoSyncTimer;
	private long lastSyncTime;
	
	public AppSyncr() {
		dropbox = DropboxUtil.getDefaultDropbox();
		if (!dropbox.isLinked()) Global.set("DropboxLoginError", true);
		
		processWatcher = new ProcessWatcher();
		processWatcher.addEventListener(this);
		lastSyncTime = new Date().getTime() - AUTOMATIC_SYNC_PERIOD;
		Global.addEventlistener(this);
	}
	
	public boolean isWatchingProcess(String processName)
	{
		for (AppConfig config : appConfigs.values())
		{
			if (config.getProcessName().equalsIgnoreCase(processName))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public void syncAllApps() {
		new Thread() {
			public void run() {
				syncAllAppsNotThreaded();
			}
		}.start();
	}

	public void syncAllAppsNotThreaded() {
		if (dropbox.isLinked() == true)
		{
			for (final AppConfig app : appConfigs.values()) {
				syncApp(app, false);
			}
		}
	}
	
	public void timedSync() {
		long currentTime = new Date().getTime();
		
		long diff = currentTime - lastSyncTime;
		
		if (diff >= AUTOMATIC_SYNC_PERIOD) {
			if (processWatcher.foundAtLeastOneProcess()) {
				syncAllApps();
			}
		}
		
		lastSyncTime = currentTime;
	}
	
	public void init() {
		reload();
		kurumConfig = PropertyUtil.getDefaultProperty();
		startTimedSync();
		syncAllApps();
	}
	
	public void reload() {
		refreshAppConfigs();
		refreshProcessWatcher();
	}
	
	public void startTimedSync() {
		autoSyncTimer = new Timer();
		autoSyncTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				timedSync();
			}
		}, AUTOMATIC_SYNC_PERIOD, AUTOMATIC_SYNC_PERIOD);
	}
	
	public void refreshProcessWatcher() {
		processWatcher.clear();
		
		for (AppConfig app : appConfigs.values()) {
			processWatcher.addProcess(app.getProcessName());
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
		Log.write(e.getProcessName());
		syncApp(appConfigs.get(e.getProcessName()), true);
	}
	
	public void uploadToDropbox(AppConfig config) {
		config.setSyncing(true);
		showTooltip(Language.getFormattedString("StartSyncing", config.getAppTitle()));
		
		if (!config.isUsesLuaScript()) {
			AppSyncrEngine engine = new AppSyncrEngine(config);
			engine.doDefaultUpload();
		}
		else {
			LuaEngine.getDefaultLuaEngine().run(config, LuaMode.UPLOAD);
		}
			
		showTooltip(Language.getFormattedString("UploadFinished", config.getAppTitle()));
		config.setSyncing(false);
	}

	public void downloadToLocal(AppConfig config) {
		config.setSyncing(true);
		showTooltip(Language.getFormattedString("StartSyncing", config.getAppTitle()));
		
		if (!config.isUsesLuaScript()) {
			AppSyncrEngine engine = new AppSyncrEngine(config);
			engine.doDefaultDownload();
		}
		else {
			LuaEngine.getDefaultLuaEngine().run(config, LuaMode.DOWNLOAD);
		}
			
		
		showTooltip(Language.getFormattedString("DownloadFinished", config.getAppTitle()));
		config.setSyncing(false);
	}

	public void showTooltip(final String text) {
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				Global.set("ShowToolTip", text);
			}
		});
		
	}
	
	public void saveSyncInfo(DropboxEntry entry) {
		kurumConfig.setString(entry.fileName + "_date", entry.modifydate.toString());
		kurumConfig.setString(entry.fileName + "_rev", entry.rev);
	}

	public void syncApp(AppConfig config, boolean force) {
		String supportedVersion = config.getSupportedVersion();
		
		if (supportedVersion != null) {
			supportedVersion = supportedVersion.substring(1, supportedVersion.length());
			int version = Integer.valueOf(supportedVersion);
			int kurumVersion = Integer.valueOf(Environment.VERSION.substring(1, Environment.VERSION.length()));
			
			if (version > kurumVersion) {
				Log.write(config.getAppTitle() + " AppConfig requires newer Kurum version: #" + version);
				return ;
			}
		}
		
		if (config != null && !config.isSyncing() && config.checkAllVars()) {
			String appName = config.getAppName();
			String archivePath = config.getDropboxZipPath();
			Date localDate = Util.stringToDate(kurumConfig.getString(appName + ".zip" + "_date"));
			
			DropboxEntry lastSync = dropbox.getMetadata(archivePath);
			
			
			if (!lastSync.isValid || lastSync.isDeleted) {
				//First upload->Upload
				Log.write("First upload :" + config.getAppTitle());
				uploadToDropbox(config);
			}
			else if(lastSync.isValid) {
				if (!lastSync.isDeleted && localDate == null) {
					//First sync->Download
					Log.write("First sync :" + config.getAppTitle());
					downloadToLocal(config);
				}
				else if(lastSync.modifydate.after(localDate)) {
					//Dropbox is newer->Download
					Log.write("Dropbox is newer :" + config.getAppTitle());
					downloadToLocal(config);
				}
				else if(lastSync.modifydate.before(localDate) && !force)
				{
					Log.write("Local is newer :" + config.getAppTitle());
					//Local is (maybe) newer
					uploadToDropbox(config);
				}
				else if (force)
				{
					Log.write("Forcing upload :" + config.getAppTitle());
					uploadToDropbox(config);
				}
				else
				{
					//Same, but will never reach here except timed sync
					Log.write("Same, POMF =3 :" + config.getAppTitle());
				}	
			}
		}
	}


	@Override
	public void onGlobalSet(GlobalEvent e) {
		String eventKey = e.getEventKey();
		
		if (eventKey.equals("UploadApp")) {
			AppConfig config = (AppConfig)e.getObject();
			String processName = config.getProcessName();
			
			if (appConfigs.containsKey(processName)) {
				config = appConfigs.get(processName);
			}
			
			uploadToDropbox(config);
		}
		else if (eventKey.equals("DownloadApp")) {
			AppConfig config = (AppConfig)e.getObject();
			String processName = config.getProcessName();
			
			if (appConfigs.containsKey(processName)) {
				config = appConfigs.get(processName);
			}
			
			downloadToLocal(config);
		}
		else if(eventKey.equals("SyncNow")) {
			if (e.getObject() == null) {
				syncAllApps();
			}
			else {
				syncApp((AppConfig)e.getObject(), false);
			}
		}
	}
}
