package kr.iamghost.kurum.ui;

import java.io.File;
import java.io.IOException;

import kr.iamghost.kurum.AppConfig;
import kr.iamghost.kurum.AppConfigParser;
import kr.iamghost.kurum.DropboxEntry;
import kr.iamghost.kurum.DropboxUtil;
import kr.iamghost.kurum.Environment;
import kr.iamghost.kurum.FileUtil;
import kr.iamghost.kurum.Global;
import kr.iamghost.kurum.Language;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.dropbox.client2.DropboxAPI.Entry;

public class AppConfigImportWindow extends Window {
	private Button deleteButton;
	private Button downloadButton;
	private Button importFromFileButton;
	private List recentConfigList;
	private String selectedFileName;
	
	public AppConfigImportWindow(Display display) {
		super(display);
		// TODO Auto-generated constructor stub
	}
	
	public void init() {
		Shell shell = getShell();
		shell.setSize(200, 400);
		shell.setText(Language.getString("AddNewConfig"));
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		
		shell.setLayout(layout);
		
		Label newLabel = new Label(shell, SWT.LEFT);
		newLabel.setText(Language.getString("RecentAppConfigs") + ":");
		newLabel.setLayoutData(
				new GridDataBuilder()
					.spanHorizontal(2)
					.fillHorizontal()
					.create());
		
		recentConfigList = new List(shell, SWT.SINGLE | SWT.BORDER);
		recentConfigList.setLayoutData(
				new GridDataBuilder()
					.spanHorizontal(2)
					.fillHorizontal()
					.setMinimumWidth(200)
					.setHeight(100)
					.create());
		
		recentConfigList.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				onSelectConfigList();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		downloadButton = new Button(shell, SWT.PUSH);
		downloadButton.setText(Language.getString("Download"));
		downloadButton.setLayoutData(
				new GridDataBuilder()
					.fillHorizontal()
					.create());
		downloadButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				onClickDownloadButton();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		deleteButton = new Button(shell, SWT.PUSH);
		deleteButton.setText(Language.getString("Delete"));
		deleteButton.setLayoutData(
				new GridDataBuilder()
					.fillHorizontal()
					.create());
		deleteButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				onClickDeleteButton();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

		importFromFileButton = new Button(shell, SWT.PUSH);
		importFromFileButton.setText(Language.getString("ImportFromFile"));
		importFromFileButton.setLayoutData(
				new GridDataBuilder()
					.spanHorizontal(2)
					.fillHorizontal()
					.create());
		
		importFromFileButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				importAppConfigFromFile();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		shell.pack(true);

		setJumped(true);
		
		refreshRecentConfigs();
	}
	
	protected void onClickDeleteButton() {
		DropboxUtil dropbox = DropboxUtil.getDefaultDropbox();
		MessageBox messageBox = new MessageBox(getShell(), SWT.YES | SWT.NO | SWT.ICON_QUESTION);
		messageBox.setMessage(Language.getString("ConfirmDeleteAppConfig"));
		
		if (messageBox.open() == SWT.YES) {
			dropbox.Delete("/AppConfigs/" + selectedFileName);
			refreshRecentConfigs();
		}
	}

	protected void onClickDownloadButton() {
		DropboxUtil dropbox = DropboxUtil.getDefaultDropbox();
		
		File file = new File(Environment.KURUM + "/AppConfigs/" + selectedFileName);
		File folder = file.getParentFile();
		
		if (!folder.isDirectory()) {
			folder.mkdirs();
		}
		
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		dropbox.download("AppConfigs/" + selectedFileName, file);
		
		AppConfigParser parser = new AppConfigParser();
		parser.parse(file.getAbsolutePath());
		
		AppConfig tempConfig = parser.getAppConfig();
		
		if (tempConfig != null && tempConfig.getAppName() != null
				&& tempConfig.getAppTitle() != null) {
			
			Global.set("AppConfigImportFinished", true);
			askToSync(tempConfig);
			showImportFinishedMessage(tempConfig.getAppTitle());
			close();
		}

	}

	protected void onSelectConfigList() {
		int index = recentConfigList.getSelectionIndex();
		
		if (index >= 0)
		{
			selectedFileName = recentConfigList.getItem(index).toString();
			downloadButton.setEnabled(true);
			deleteButton.setEnabled(true);
		}
	}

	private void refreshRecentConfigs() {
		downloadButton.setEnabled(false);
		deleteButton.setEnabled(false);
		recentConfigList.removeAll();
		
		DropboxUtil dropbox = DropboxUtil.getDefaultDropbox();
		
		if (dropbox.isLinked()) {
			DropboxEntry entry = dropbox.getMetadata("/AppConfigs", 100, null, true, null);
			
			if (entry.isValid) {
				for (Entry file : entry.getContents()) {
					recentConfigList.add(file.fileName());
				}	
			}
		}
	}
	
	private void importAppConfigFromFile() {
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
				
				showImportFinishedMessage(tempConfig.getAppTitle());
				
				askToSync(tempConfig);
				
				DropboxUtil dropbox = DropboxUtil.getDefaultDropbox();
				if (dropbox.isLinked()) {
					File file = tempConfig.getOriginalFile();
					dropbox.upload(file, "AppConfigs/" + file.getName(), true);
				}
				
				Global.set("AppConfigImportFinished", true);
				close();
			}
			else
			{
				Global.setObject("LastShell", getShell());
				Global.set("MessageBox", Language.getString("ConfigImportFailed"));
			}
		}
	}
	
	private void showImportFinishedMessage(String appTitle) {
		Global.set("ShowToolTip", Language.getFormattedString("AppConfigImported", appTitle));
	}
	
	private void askToSync(AppConfig config) {
		MessageBox msgBox = new MessageBox(getShell(), SWT.YES | SWT.NO | SWT.ICON_QUESTION);
		msgBox.setText(Environment.KURUMTITLE);
		msgBox.setMessage(Language.getFormattedString("ConfirmFirstSync", config.getAppTitle()));
		
		if (msgBox.open() == SWT.YES) {
			Global.setObject("SyncNow", config);
		}
	}
}
