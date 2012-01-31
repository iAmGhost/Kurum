package kr.iamghost.kurum.ui;

import kr.iamghost.kurum.AppSyncr;
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
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
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
	
	protected boolean quit;
	protected boolean isFirstLaunch;
	
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
		
		final ToolTip tip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_INFORMATION);
		tip.setMessage(Language.getString("TrayNotice"));
		tip.setText(Environment.KURUMTITLE);

		final Tray tray = getDisplay().getSystemTray();
		
		if (tray != null)
		{
			
			Image image = getDisplay().getSystemImage(SWT.ICON_INFORMATION);
			TrayItem item = new TrayItem(tray, SWT.NONE);
			
			item.setToolTip(tip);
			item.setImage(image);
			
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
				item.addListener(SWT.DefaultSelection, new Listener() {
					@Override
					public void handleEvent(Event event) {
						showAndActive();
					}
				});
			}

			
			item.addMenuDetectListener(new MenuDetectListener() {
				@Override
				public void menuDetected(MenuDetectEvent e) {
					menu.setVisible(true);
				}
			});
		}
		else
		{
			tip.setLocation(100, 100);
		}
		
		shell.addShellListener(new ShellListener() {
			@Override
			public void shellActivated(ShellEvent e) {}
			@Override
			public void shellClosed(ShellEvent e) {
				if (isFirstLaunch) tip.setVisible(true);
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
		
		kurumConfig = new PropertyUtil().loadDefaultFile();
		appSyncr = new AppSyncr();
		appSyncr.init();
		
		checkDefaultLaunch();
	}
	
	public void loop() {
		if (Global.getString("LastError") != null) {
			Log.write("HolyShit");
		}
	}
	
	public void checkDefaultLaunch() {
		if (!kurumConfig.getString("isFirstLaunch").equals("false")) {
			kurumConfig.setString("isFirstLaunch", "false");
			kurumConfig.save();
			isFirstLaunch = true;
		}
	}

	@Override
	public void onGlobalSet(GlobalEvent e) {
		if (e.getEventKey().equals("DropboxLoginError") && e.getBool()) {
			MessageBox msg = new MessageBox(getShell(), SWT.ICON_INFORMATION | SWT.OK);
			msg.setMessage(Language.getString("NeedDropboxLogin"));
			msg.open();
			new DropboxLoginWindow(getDisplay()).open();
		}
	}
}
