package kr.iamghost.kurum.ui;

import kr.iamghost.kurum.Environment;
import kr.iamghost.kurum.Language;
import kr.iamghost.kurum.Log;
import kr.iamghost.kurum.PropertyUtil;

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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public class MainWindow extends Window{
	private PropertyUtil appConfig;
	protected boolean quit;
	protected boolean isFirstLaunch;
	
	public MainWindow(Display display) {
		super(display);
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
			menuItem.setText(Language.getString("Exit"));
			menuItem.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					quit = true;
					getShell().close();
				}
			});
			
			item.addListener(SWT.DefaultSelection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					getShell().setVisible(true);
					getShell().forceActive();
				}
			});
			
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
		
		appConfig = new PropertyUtil().loadDefaultFile();
		
		if (!appConfig.getString("isFirstLaunch").equals("false")) {
			appConfig.setString("isFirstLaunch", "false");
			appConfig.save();
			isFirstLaunch = true;
		}
	}
}
