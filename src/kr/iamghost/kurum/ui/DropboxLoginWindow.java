package kr.iamghost.kurum.ui;

import kr.iamghost.kurum.DropboxUtil;
import kr.iamghost.kurum.Language;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.dropbox.client2.DropboxAPI.Account;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
public class DropboxLoginWindow extends Window implements SelectionListener{
	Label accountInfoLabel;
	Button loginButton;
	Button createAccountButton;
	DropboxUtil dropbox;
	
	public DropboxLoginWindow(Display display) {
		super(display, SWT.CLOSE);
	}
	
	@Override
	public void init() {
		dropbox = new DropboxUtil();
		
		Shell shell = getShell();
		shell.setText(Language.getString("DropboxLogin"));
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		
		shell.setLayout(gridLayout);
		
		GridData gridData = null;
		Label label = null;
		Button button = null;
		
		label = new Label(shell, SWT.NONE);
		label.setText(Language.getString("Account") + ":");
		
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.minimumWidth = 200;
		gridData.grabExcessHorizontalSpace = true;
		
		label = new Label(shell, SWT.CENTER);
		label.setText("");
		label.setLayoutData(gridData);
		accountInfoLabel = label;
		
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		
		button = new Button(shell, SWT.PUSH);
		button.setText(Language.getString("LoginWithDropbox"));
		button.setLayoutData(gridData);
		button.addSelectionListener(this);
		loginButton = button;

		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		
		button = new Button(shell, SWT.PUSH);
		button.setText(Language.getString("CreateDropboxAccount"));
		button.setLayoutData(gridData);
		button.addSelectionListener(this);
		createAccountButton = button;
		
		shell.pack();
		
		refreshAccountInfo();
		
		centre();
	}
	
	public void refreshAccountInfo() {
		if (dropbox.isLinked()) {
			Account account = dropbox.getAccountInfo();
			accountInfoLabel.setText(account.displayName);
			loginButton.setText(Language.getString("Logout"));
			createAccountButton.setVisible(false);
			getShell().pack(true);
		}
		else {
			loginButton.setText(Language.getString("LoginWithDropbox"));
			accountInfoLabel.setText("");
		}
	}

	@Override
	public void widgetSelected(SelectionEvent event) {
		if (event.widget.equals(loginButton)) {
			onClickLoginButton();
		}
		else if (event.widget.equals(createAccountButton)) {
			onClickCreateAccountButton();
		}
	}
	
	public void onClickCreateAccountButton() {
		String url = "https://www.dropbox.com/register";
		
		MessageBox question = new MessageBox(getShell(),
				SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		question.setMessage(Language.getString("DropboxReferralQuestion"));
		
		if (question.open() == SWT.YES) url = "http://db.tt/mbTseJZf";
		
		try {
			BrowserLauncher launcher = new BrowserLauncher();
			launcher.openURLinBrowser(url);
		} catch (BrowserLaunchingInitializingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedOperatingSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onClickLoginButton() {
		if (dropbox.isLinked())
		{
			dropbox.deleteToken();
		}
		else
		{
			MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_INFORMATION | SWT.OK);
			messageBox.setMessage(Language.getString("LoginNotify"));
			
			String url = dropbox.requestNewToken();
			
			if (url == null) {
				messageBox.setMessage(Language.getString("CantConnectToDropbox"));
				messageBox.open();
			}
			
			
			BrowserLauncher launcher;
			
			try {
				launcher = new BrowserLauncher();
				launcher.openURLinBrowser(url);
			}
			catch (Exception exception) {
				exception.printStackTrace();
			}

			int respond = messageBox.open();
			if (respond == SWT.OK) {
				if (dropbox.retrieveNewToken() != null) {
					messageBox.setMessage(Language.getString("LoginSaved"));
					messageBox.open();
					dropbox.saveToken();
				}
			}
		}
		
		refreshAccountInfo();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
