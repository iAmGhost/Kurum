package kr.iamghost.kurum.ui;

import java.awt.SystemTray;

import kr.iamghost.kurum.AppConfigVariable;
import kr.iamghost.kurum.AppConfigVariable.VarType;
import kr.iamghost.kurum.AppSyncr;
import kr.iamghost.kurum.DropboxUtil;
import kr.iamghost.kurum.Environment;
import kr.iamghost.kurum.Global;
import kr.iamghost.kurum.GlobalEvent;
import kr.iamghost.kurum.GlobalEventListener;
import kr.iamghost.kurum.Language;
import kr.iamghost.kurum.Log;
import kr.iamghost.kurum.PropertyUtil;

import org.apache.commons.lang3.SystemUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public class MainWindow extends Window implements GlobalEventListener{
	private AppSyncr appSyncr;
	private TrayItem trayItem;
	private Window logWindow;

	protected boolean quit;
	
	public MainWindow(Display display) {
		super(display);
	}
	
	public void showAndActive() {
		getShell().setVisible(true);
		getShell().forceActive();
	}
	
	public void quit() {
		quit = true;
		getShell().close();
	}
	
	public void init() {
		Shell shell = getShell();
		shell.setText(Environment.KURUMTITLE);
		shell.setSize(250, 250);
		centre();
		
		initForm();
		initTrayAndMenu();
		
		shell.addShellListener(new ShellListener() {
			@Override
			public void shellActivated(ShellEvent e) {}
			@Override
			public void shellClosed(ShellEvent e) {
				if (!quit) {
					getShell().setVisible(false);
					if (!isJumped())
						Global.set("LastWindowClosed", true);
				}
				e.doit = quit;
			}
			@Override
			public void shellDeactivated(ShellEvent e) {}
			@Override
			public void shellDeiconified(ShellEvent e) {}
			@Override
			public void shellIconified(ShellEvent e) {}
		});
		
		Global.addEventlistener(this);
		
		checkFirstLaunch();
		
		appSyncr = new AppSyncr();
		appSyncr.init();
		
		logWindow = WindowFactory.create("Log");
	}
	
	private void initForm() {
		Shell shell = getShell();
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		
		shell.setLayout(gridLayout);
		
		Button button;

		GridData horizSpangridData = new GridData();
		horizSpangridData.horizontalSpan = 2;
		horizSpangridData.horizontalAlignment = SWT.FILL;
		horizSpangridData.grabExcessHorizontalSpace = true;
		
		button = new Button(shell, SWT.PUSH);
		button.setText(Language.getString("SyncManually"));
		button.setLayoutData(horizSpangridData);
		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkDropboxLoginAndRaiseError();
				if (DropboxUtil.getDefaultDropbox().isLinked())
					appSyncr.syncAllApps();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		button = new Button(shell, SWT.PUSH);
		button.setText(Language.getString("SyncManager"));
		button.setLayoutData(horizSpangridData);
		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkDropboxLoginAndRaiseError();
				if (DropboxUtil.getDefaultDropbox().isLinked())
					onClickSyncManagerButton();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		button = new Button(shell, SWT.PUSH);
		button.setText(Language.getString("ManageDropboxAccount"));
		button.setLayoutData(horizSpangridData);
		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onClickLoginButton();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		button = new Button(shell, SWT.PUSH);
		button.setText("Showlogs");
		button.setLayoutData(horizSpangridData);
		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logWindow.open();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void onClickSyncManagerButton() {
		jumpToNewWindow("SyncManager");
	}
	
	private void onClickLoginButton() {
		jumpToNewWindow("DropboxLogin");
	}

	private void checkDropboxLoginAndRaiseError() {
		if (!DropboxUtil.getDefaultDropbox().isLinked()) Global.set("DropboxLoginError", true);
	}
	
	private void initTrayAndMenu() {
		final Tray tray = getDisplay().getSystemTray();
		
		if (tray != null)
		{
			trayItem = new TrayItem(tray, SWT.NONE);
			trayItem.setImage(getShell().getImage());
			
			final Menu menu = new Menu(getShell(), SWT.POP_UP);
			MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText(Language.getString("OpenKurum"));
			menuItem.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					showAndActive();
				}
			});
			
			new MenuItem(menu, SWT.SEPARATOR);
			
			menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText(Language.getString("Exit"));
			menuItem.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					quit();
				}
			});
			
			//Double-clicking tray icon only for Windows
			if (SystemUtils.IS_OS_WINDOWS) {
				trayItem.addListener(SWT.DefaultSelection, new Listener() {
					@Override
					public void handleEvent(Event event) {
						showAndActive();
					}
				});
			}

			
			trayItem.addMenuDetectListener(new MenuDetectListener() {
				@Override
				public void menuDetected(MenuDetectEvent e) {
					menu.setVisible(true);
				}
			});
		}
	}
	
	public void checkFirstLaunch() {

	}
	
	public void showTooltip(ToolTip tip) {
		if (SystemTray.isSupported()) {
			trayItem.setToolTip(tip);
		}
		else
		{
			tip.setLocation(0, 0);
		}
		
		tip.setVisible(true);
	}

	@Override
	public void onGlobalSet(GlobalEvent e) {
		if (e.getEventKey().equals("DropboxLoginError") && e.getBool()) {
			MessageBox msg = new MessageBox(getShell(), SWT.ICON_INFORMATION | SWT.OK);
			msg.setMessage(Language.getString("NeedDropboxLogin"));
			msg.open();
			new DropboxLoginWindow(getDisplay()).open();
		}
		else if (e.getEventKey().equals("RefreshAppConfigs") && e.getBool()) {
			appSyncr.reload();
			Log.write("Reload AppConfigs");
		}
		else if(e.getEventKey().equals("VariableNotFoundError")) {
			final AppConfigVariable var = (AppConfigVariable)e.getObject();
			getDisplay().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					handleVariableNotFoundError(var);
				}
			});
		}
		else if (e.getEventKey().equals("MessageBox")) {
			Shell shell = (Shell)Global.getObject("LastShell");
			if (shell == null) shell = getShell();
			
			MessageBox msg = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
			msg.setText(Environment.KURUMTITLE);
			msg.setMessage(e.getString());
			msg.open();
		}
		else if (e.getEventKey().equals("ShowToolTip")) {
			ToolTip tip = new ToolTip(getShell(), SWT.BALLOON | SWT.ICON_INFORMATION);
			tip.setMessage(e.getString());
			tip.setText(Environment.KURUMTITLE);
			showTooltip(tip);
		}
		else if (e.getEventKey().equals("OnDropboxLoggedIn")) {
			if (appSyncr != null)
				appSyncr.syncAllApps();
		}
		else if(e.getEventKey().equals("LastWindowClosed")) {
			Global.set("ShowToolTip", Language.getString("TrayNotice"));
			setJumped(false);
		}
	}

	private void handleVariableNotFoundError(AppConfigVariable var) {
		String dir = null;
		
		if (var.getType() == VarType.DIRECTORY) {
			DirectoryDialog dlg = new DirectoryDialog(getShell());
			dlg.setMessage(var.getMessage());
			dir = dlg.open();
		}
		else {
			FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
			dlg.setFilterExtensions(var.getFilters());
			dlg.setText(var.getMessage());
			dir = dlg.open();
		}
		
		if (dir != null) {
			PropertyUtil kurumConfig = PropertyUtil.getDefaultProperty();
			String parsedDir = Environment.parsePath(dir);
			Environment.addVariable(var.getName(), parsedDir);
			kurumConfig.setString("var_" + var.getName(), parsedDir);
		}
		
		Global.setObject("SyncNow", Global.getObject("VariableNotFoundAppConfig"));
	}
}
