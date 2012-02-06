package kr.iamghost.kurum.ui;

import kr.iamghost.kurum.Global;
import kr.iamghost.kurum.GlobalEvent;
import kr.iamghost.kurum.GlobalEventListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class LogWindow extends Window implements GlobalEventListener {
	private Text logText;
	public LogWindow(Display display) {
		super(display);
		// TODO Auto-generated constructor stub
	}
	
	public void init() {
		Shell shell = getShell();
		
		shell.setText("Logs");
		shell.setSize(400, 300);
		
		shell.setLayout(new FillLayout());
		
		logText = new Text(shell, SWT.MULTI | SWT.V_SCROLL);
		
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
				// TODO Auto-generated method stub
				getShell().setVisible(false);
				e.doit = false;
			}
			
			@Override
			public void shellActivated(ShellEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Global.addEventlistener(this);
	}

	@Override
	public void onGlobalSet(final GlobalEvent e) {
		// TODO Auto-generated method stub
		if (e.getEventKey().equals("Log")) {
			getDisplay().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					logText.setText(e.getString() + "\n" + logText.getText());
				}
			});
		}
	}
}
