package kr.iamghost.kurum.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import kr.iamghost.kurum.AppConfig;
import kr.iamghost.kurum.AppConfigParser;
import kr.iamghost.kurum.Environment;
import kr.iamghost.kurum.FileUtil;
import kr.iamghost.kurum.Global;
import kr.iamghost.kurum.Language;
import kr.iamghost.kurum.PropertyUtil;
import kr.iamghost.kurum.Suggestion;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class SuggestionWindow extends Window {
	private String suggestedProcess;
	private List suggestionList;
	private Label descriptionLabel;
	private Button ignoreButton;
	private Button downloadButton;
	private ArrayList<Suggestion> suggestions;
	private Suggestion currentSuggestion;
	public SuggestionWindow(Display display) {
		super(display);
		// TODO Auto-generated constructor stub
	}

	
	public void init() {
		suggestedProcess = Global.getString("LastSuggestedProcess");
		
		suggestions = new ArrayList<Suggestion>();
		
		Shell shell = getShell();
		shell.setText(Language.getString("AppConfigSuggestion"));
		shell.setSize(380, 280);
		centre();
		shell.forceActive();
		
		shell.addShellListener(new ShellListener() {
			
			@Override
			public void shellIconified(ShellEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void shellDeiconified(ShellEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void shellDeactivated(ShellEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void shellClosed(ShellEvent e) {
				onClosed();
			}
			
			@Override
			public void shellActivated(ShellEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		
		shell.setLayout(layout);
		
		Label newLabel = new Label(shell, SWT.LEFT);
		newLabel.setText(Language.getString("SuitableAppConfig"));

		suggestionList = new List(shell, SWT.SINGLE | SWT.BORDER);
		suggestionList.setLayoutData(
				new GridDataBuilder()
				.fillHorizontal()
				.setHeight(100)
				.create());
		suggestionList.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				onClickSuggestionList();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		newLabel = new Label(shell, SWT.LEFT);
		newLabel.setText(Language.getString("Description") + ":");
		
		descriptionLabel = new Label(shell, SWT.LEFT);
		descriptionLabel.setLayoutData(
				new GridDataBuilder()
				.fillHorizontal()
				.create());
		
		downloadButton = new Button(shell, SWT.PUSH);
		downloadButton.setText(Language.getString("Download"));
		downloadButton.setLayoutData(
				new GridDataBuilder()
				.fillHorizontal()
				.create());
		downloadButton.setEnabled(false);
		downloadButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				onClickDownloadButton();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		ignoreButton = new Button(shell, SWT.CHECK);
		ignoreButton.setText(Language.getString("IgnoreNextTime"));
		refreshSuggestionList();
	}
	
	protected void onClickDownloadButton() {
		// TODO Auto-generated method stub
		try {
			URL url = new URL(currentSuggestion.getUrl());
			File file = new File(
					Environment.KURUM + "/AppConfigs/"+ currentSuggestion.getInternalName() + ".xml");
			InputStream is = url.openStream();
			FileOutputStream fos = new FileOutputStream(file);
			
			FileUtil.copy(is, fos);
			is.close();
			fos.close();
			
			AppConfigParser parser = new AppConfigParser();
			parser.parse(file.getAbsolutePath());
			AppConfig tempConfig = parser.getAppConfig();
			
			if (tempConfig != null && tempConfig.isValid()) {
				Global.set("ShowToolTip",
						Language.getFormattedString("AppConfigImported", tempConfig.getAppTitle()));
				Global.set("RefreshAppConfigs", true);
				askToSync(tempConfig);
				this.close();
			} else {
				file.delete();
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	protected void onClosed() {
		// TODO Auto-generated method stub
		if (ignoreButton.getSelection()) {
			PropertyUtil.getDefaultProperty().setString("ignore_" + suggestedProcess, "true");
		}
		
	}


	protected void onClickSuggestionList() {
		int index = suggestionList.getSelectionIndex();
		if (index >= 0) {
			downloadButton.setEnabled(true);
			currentSuggestion = suggestions.get(index);
			descriptionLabel.setText(currentSuggestion.getDescription());
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public void refreshSuggestionList() {
		ArrayList<Suggestion> globalSuggestions = (ArrayList<Suggestion>)Global.getObject("Suggestions");
	
		Iterator<Suggestion> it = globalSuggestions.iterator();
		while (it.hasNext()) {
			Suggestion suggestion = it.next();
			if (suggestion.getProcessName().equalsIgnoreCase(suggestedProcess)) {
				suggestionList.add(suggestion.getTitle());
				suggestions.add(suggestion);
			}
		}
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
