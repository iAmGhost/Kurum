package kr.iamghost.kurum.ui;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import kr.iamghost.kurum.AppConfig;
import kr.iamghost.kurum.AppConfigParser;
import kr.iamghost.kurum.Environment;
import kr.iamghost.kurum.FileUtil;
import kr.iamghost.kurum.Global;
import kr.iamghost.kurum.GlobalEvent;
import kr.iamghost.kurum.GlobalEventListener;
import kr.iamghost.kurum.Language;
import kr.iamghost.kurum.Util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class SyncManagerWindow extends Window implements GlobalEventListener {
	private Combo appConfigCombo;
	private ArrayList<AppConfig> appConfigs;
	private AppConfig currentAppConfig;
	private Button deleteButton;
	
	public SyncManagerWindow(Display display) {
		super(display);
		// TODO Auto-generated constructor stub
	}
	
	public void init() {
		Global.addEventlistener(this);
		
		Shell shell = getShell();
		shell.setText(Language.getString("SyncManager"));
		shell.setSize(300, 300);
		
		Button button;
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		shell.setLayout(gridLayout);
		
		Label newLabel = new Label(shell, SWT.LEFT);
		newLabel.setLayoutData(
				new GridDataBuilder()
					.spanHorizontal(2)
					.create());
		
		newLabel.setText(Language.getString("CurrentAppConfigs") + ":");
		
		appConfigCombo = new Combo(shell, SWT.READ_ONLY);
		appConfigCombo.setLayoutData(
				new GridDataBuilder()
					.spanHorizontal(2)
					.fillHorizontal()
					.create());
		
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
		
		button = new Button(shell, SWT.PUSH);
		button.setText(Language.getString("ImportAppConfigFile"));
		button.setLayoutData(
				new GridDataBuilder()
					.fillHorizontal()
					.create());
		
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
		
		deleteButton = new Button(shell, SWT.PUSH);
		deleteButton.setText(Language.getString("DeleteCurrentAppConfig"));
		deleteButton.setLayoutData(
				new GridDataBuilder()
					.fillHorizontal()
					.create());
		deleteButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				onClickDeleteCurrentAppConfigButton();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Button syncThisAppButton = new Button(shell, SWT.PUSH);
		syncThisAppButton.setText(Language.getString("SyncThisAppOnly"));
		syncThisAppButton.setLayoutData(
				new GridDataBuilder()
					.fillHorizontal()
					.spanHorizontal(2)
					.create());
		syncThisAppButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				onClickSyncThisAppButton();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Button forceUploadButton = new Button(shell, SWT.PUSH);
		forceUploadButton.setText(Language.getString("ForceUpload"));
		forceUploadButton.setLayoutData(
				new GridDataBuilder()
					.fillHorizontal()
					.create());
		forceUploadButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				onClickForceUploadButton();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Button forceDownloadButton = new Button(shell, SWT.PUSH);
		forceDownloadButton.setText(Language.getString("ForceDownload"));
		forceDownloadButton.setLayoutData(
				new GridDataBuilder()
					.fillHorizontal()
					.create());
		forceDownloadButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				onClickForceDownloadButton();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(
				new GridDataBuilder()
					.spanHorizontal(2)
					.fillHorizontal()
					.create());
		
		button = new Button(shell, SWT.PUSH);
		button.setText(Language.getString("RefreshAppConfigs"));
		button.setLayoutData(
				new GridDataBuilder()
					.spanHorizontal(2)
					.fillHorizontal()
					.create());
		
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
		button.setLayoutData(
				new GridDataBuilder()
				.spanHorizontal(2)
				.fillHorizontal()
				.create());
		
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
	
	protected void onClickSyncThisAppButton() {
		int select = appConfigCombo.getSelectionIndex();
		
		if (select >= 0) {
			MessageBox msgBox = new MessageBox(getShell(), SWT.YES | SWT.NO | SWT.ICON_INFORMATION);
			msgBox.setText(Environment.KURUMTITLE);
			msgBox.setMessage(Language.getFormattedString("ConfirmSyncSpecificApp",
					currentAppConfig.getAppTitle()));
			
			if (msgBox.open() == SWT.YES) {
				Global.setObject("SyncNow", currentAppConfig);
			}
		}
	}

	protected void onClickForceUploadButton() {
		
		int select = appConfigCombo.getSelectionIndex();
			
		if (select >= 0) {
			MessageBox msgBox = new MessageBox(getShell(), SWT.YES | SWT.NO | SWT.ICON_INFORMATION);
			msgBox.setText(Environment.KURUMTITLE);
			msgBox.setMessage(Language.getFormattedString("ConfirmForceUpload",
					currentAppConfig.getAppTitle()));
			
			if (msgBox.open() == SWT.YES) {
				Global.setObject("UploadApp", currentAppConfig);
			}
		}
	}

	protected void onClickForceDownloadButton() {
		int select = appConfigCombo.getSelectionIndex();
		
		if (select >= 0) {
			MessageBox msgBox = new MessageBox(getShell(), SWT.YES | SWT.NO | SWT.ICON_INFORMATION);
			msgBox.setText(Environment.KURUMTITLE);
			msgBox.setMessage(Language.getFormattedString("ConfirmForceDownload",
					currentAppConfig.getAppTitle()));
			
			if (msgBox.open() == SWT.YES) {
				Global.setObject("DownloadApp", currentAppConfig);
			}
		}
	}

	protected void onClickDeleteCurrentAppConfigButton() {
		
		MessageBox msgBox = new MessageBox(getShell(), SWT.YES | SWT.NO | SWT.ICON_INFORMATION);
		msgBox.setText(Environment.KURUMTITLE);
		msgBox.setMessage(Language.getString("ConfirmDeleteAppConfig"));
		
		if (msgBox.open() == SWT.YES) {
			int select = appConfigCombo.getSelectionIndex();
			
			if (select >= 0) {
				Global.set("ShowToolTip",
						Language.getFormattedString("AppConfigDeleted",
								currentAppConfig.getAppTitle())
								);
				
				currentAppConfig.resetAllVars();
				FileUtil.delete(currentAppConfig.getOriginalFile());
				onClickRefreshAppConfigsButton();
			}
		}
	}

	protected void onClickImportAppConfigButton() {
		WindowFactory.create("AppConfigImport").open();
	}

	private void onClickRefreshAppConfigsButton() {
		appConfigCombo.removeAll();
		loadAppConfigList();
		Global.set("RefreshAppConfigs", true);
	}

	private void onAppConfigListSelected(int index) {
		currentAppConfig = appConfigs.get(index);
		
		if (index >= 0)
		{
			deleteButton.setEnabled(true);
		}
		else {
			deleteButton.setEnabled(false);
		}
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

	@Override
	public void onGlobalSet(GlobalEvent e) {
		// TODO Auto-generated method stub
		if (e.getEventKey().equals("AppConfigImportFinished")) {
			onClickRefreshAppConfigsButton();
		}
	}
}
