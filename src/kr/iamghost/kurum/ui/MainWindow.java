package kr.iamghost.kurum.ui;

import java.awt.SystemTray;

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
	private TrayItem trayItem;
	private ToolTip toolTip;
	
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
		
		kurumConfig = new PropertyUtil().loadDefaultFile();
		checkDefaultLaunch();
		
		appSyncr = new AppSyncr();
		appSyncr.init();
		

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
	}
}
