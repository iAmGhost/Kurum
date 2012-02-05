package kr.iamghost.kurum.ui;

import java.awt.SystemTray;

import kr.iamghost.kurum.AppConfigVariable;
import kr.iamghost.kurum.AppConfigVariable.VarType;
import kr.iamghost.kurum.AppSyncr;
import kr.iamghost.kurum.Environment;
import kr.iamghost.kurum.Global;
import kr.iamghost.kurum.GlobalEvent;
import kr.iamghost.kurum.GlobalEventListener;
import kr.iamghost.kurum.Language;
import kr.iamghost.kurum.PropertyUtil;

import org.apache.commons.lang3.SystemUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
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
	private PropertyUtil kurumConfig;
	private AppSyncr appSyncr;
	private TrayItem trayItem;
	//private ToolTip toolTip;
	
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
		shell.setSize(400, 400);
		
		initForm();
		initTrayAndMenu();
		
		shell.addShellListener(new ShellListener() {
			@Override
			public void shellActivated(ShellEvent e) {}
			@Override
			public void shellClosed(ShellEvent e) {
				getShell().setVisible(false);
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
		
		kurumConfig = PropertyUtil.getDefaultProperty();
		checkDefaultLaunch();
		
		appSyncr = new AppSyncr();
		appSyncr.init();
	}
	
	private void initForm() {
		Shell shell = getShell();
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		
		shell.setLayout(gridLayout);
		
		GridData gridData;
		Button button;

		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		
		button = new Button(shell, SWT.PUSH);
		button.setText(Language.getString("AppConfigManager"));
		button.setLayoutData(gridData);
		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onClickAppManagerButton();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		
		button = new Button(shell, SWT.PUSH);
		button.setText(Language.getString("ManageDropboxAccount"));
		button.setLayoutData(gridData);
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
	}
	
	private void onClickAppManagerButton() {
		openWindow("AppConfigManager");
	}
	
	private void onClickLoginButton() {
		openWindow("DropboxLogin");
	}
	
	private void openWindow(String windowName) {
		Window newWindow = WindowFactory.create(windowName);
		newWindow.open();
		newWindow.show(true);
	}
	
	private void initTrayAndMenu() {
		final Tray tray = getDisplay().getSystemTray();
		
		if (tray != null)
		{
			
			Image image = getDisplay().getSystemImage(SWT.ICON_INFORMATION);
			
			trayItem = new TrayItem(tray, SWT.NONE);
			trayItem.setImage(image);
			
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
	
	public void checkDefaultLaunch() {
		if (!kurumConfig.getString("isFirstLaunch").equals("false")) {
			kurumConfig.setString("isFirstLaunch", "false");
			
			ToolTip tip = new ToolTip(getShell(), SWT.BALLOON | SWT.ICON_INFORMATION);
			tip.setMessage(Language.getString("TrayNotice"));
			tip.setText(Environment.KURUMTITLE);
			showTooltip(tip);
		}
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
		}
		else if (e.getEventKey().equals("WantSyncManually")) {
			appSyncr.syncAllApps();
		}
		else if(e.getEventKey().equals("VariableNotFoundError")) {
			AppConfigVariable var = (AppConfigVariable)e.getObject();
			
			handleVariableNotFoundError(var);
		}
		else if (e.getEventKey().equals("MessageBox")) {
			Shell shell = (Shell)Global.getObject("LastShell");
			if (shell == null) shell = getShell();
			
			MessageBox msg = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
			msg.setText(Environment.KURUMTITLE);
			msg.setMessage(e.getString());
			msg.open();
		}
	}

	private void handleVariableNotFoundError(AppConfigVariable var) {
		while (true) {
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
				String parsedDir = Environment.parsePath(dir);
				Environment.addVariable(var.getName(), parsedDir);
				kurumConfig.setString("var_" + var.getName(), parsedDir);
				break;
			}
		}
	}
}
