package kr.iamghost.kurum.ui;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import kr.iamghost.kurum.AppConfig;
import kr.iamghost.kurum.AppConfigParser;
import kr.iamghost.kurum.Environment;
import kr.iamghost.kurum.FileUtil;
import kr.iamghost.kurum.Global;
import kr.iamghost.kurum.Language;
import kr.iamghost.kurum.Util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class AppConfigManagerWindow extends Window {
	private Combo appConfigCombo;
	private ArrayList<AppConfig> appConfigs;
	private AppConfig currentAppConfig;
	private Label appTitleLabel;
	private Label appNameLabel;
	private Label authorLabel;
	
	public AppConfigManagerWindow(Display display) {
		super(display);
		// TODO Auto-generated constructor stub
	}
	
	public void init() {
		Shell shell = getShell();
		shell.setText("AppConfigManager");
		shell.setSize(300, 300);
		
		Button button;
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		shell.setLayout(gridLayout);
		
		GridData defaultGridData;
		defaultGridData = new GridData();
		defaultGridData.horizontalSpan = 2;
		defaultGridData.horizontalAlignment = SWT.FILL;
		defaultGridData.grabExcessHorizontalSpace = true;
		
		GridData fillerGridData;
		fillerGridData = new GridData();
		fillerGridData.horizontalAlignment = SWT.FILL;
		fillerGridData.grabExcessHorizontalSpace = true;
		
		GridData horizSpanGridData;
		horizSpanGridData = new GridData();
		horizSpanGridData.horizontalSpan = 2;
		horizSpanGridData.horizontalAlignment = SWT.FILL;
		horizSpanGridData.grabExcessHorizontalSpace = true;
		
		appConfigCombo = new Combo(shell, SWT.READ_ONLY);
		appConfigCombo.setLayoutData(defaultGridData);
		appConfigCombo.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				onAppConfigListSelected(appConfigCombo.getSelectionIndex());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});
		
		
		new Label(shell, SWT.LEFT).setText(Language.getString("AppTitle") + ":");
		
		appTitleLabel = new Label(shell, SWT.LEFT);
		appTitleLabel.setLayoutData(fillerGridData);
		
		new Label(shell, SWT.LEFT).setText(Language.getString("AppName") + ":");
		
		appNameLabel = new Label(shell, SWT.LEFT);
		appNameLabel.setLayoutData(fillerGridData);
		
		new Label(shell, SWT.LEFT).setText(Language.getString("Author") + ":");
		
		authorLabel = new Label(shell, SWT.LEFT);
		authorLabel.setLayoutData(fillerGridData);
		
		button = new Button(shell, SWT.PUSH);
		button.setText(Language.getString("ImportAppConfigFile"));
		button.setLayoutData(horizSpanGridData);
		button.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				onClickImportAppConfigButton();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		button = new Button(shell, SWT.PUSH);
		button.setText(Language.getString("RefreshAppConfigs"));
		button.setLayoutData(horizSpanGridData);
		button.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				onClickRefreshAppConfigsButton();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		button = new Button(shell, SWT.PUSH);
		button.setText(Language.getString("BrowseAppConfigDirectory"));
		button.setLayoutData(horizSpanGridData);
		button.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Util.browseDirectory(Environment.KURUM + "/AppConfigs");
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		loadAppConfigList();
	}
	
	protected void onClickImportAppConfigButton() {
		boolean success = false;
		FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
		String[] filter = {"*.xml"};
		dlg.setFilterExtensions(filter);
		
		String path = dlg.open();

		if (path != null) {
			File configFile = new File(path);
			AppConfigParser parser = new AppConfigParser();
			parser.parse(path);
			
			AppConfig tempConfig = parser.getAppConfig();
			
			if (tempConfig != null && tempConfig.getAppName() != null
					&& tempConfig.getAppTitle() != null) {
				File targetFile = new File(Environment.KURUM + "/AppConfigs/" + configFile.getName());
				FileUtil.copy(configFile, targetFile);
				success = true;
				Global.setObject("LastShell", getShell());
				Global.set("MessageBox",
						Language.getFormattedString("AppConfigImported", tempConfig.getAppTitle()));
				onClickRefreshAppConfigsButton();
			}
		}
		
		if (!success)
			Global.setObject("LastShell", getShell());
			Global.set("MessageBox", Language.getString("ConfigImportFailed"));
			
	}

	private void onClickRefreshAppConfigsButton() {
		appConfigCombo.removeAll();
		loadAppConfigList();
		Global.set("WantSyncManually", true);
		Global.set("RefreshAppConfigs", true);
	}

	private void onAppConfigListSelected(int index) {
		currentAppConfig = appConfigs.get(index);
		
		appTitleLabel.setText(currentAppConfig.getAppTitle());
		appNameLabel.setText(currentAppConfig.getAppName());
		authorLabel.setText(currentAppConfig.getAuthor());
	}
	
	public void loadAppConfigList() {
		appConfigs = new ArrayList<AppConfig>();
		
		File appConfigDir = new File(Environment.KURUM + "/AppConfigs");
		
		if (appConfigDir.isDirectory()) {
			File[] files = appConfigDir.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".xml");
				}
			});
			
			for (File file : files) {
				AppConfigParser parser = new AppConfigParser();
				parser.parse(file.getAbsolutePath());
				
				AppConfig newConfig = parser.getAppConfig();
				if (newConfig != null) {
					appConfigs.add(newConfig);
					appConfigCombo.add(newConfig.getAppTitle());
				}
			}
			if (appConfigCombo.getItemCount() > 0) {
				appConfigCombo.select(0);
				onAppConfigListSelected(0);	
			}
		}
	}
}
