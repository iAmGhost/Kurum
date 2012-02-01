package kr.iamghost.kurum.ui;

import kr.iamghost.kurum.DropboxUtil;

import org.eclipse.swt.widgets.Display;

public class ImportAppConfigWindow extends Window {

	private DropboxUtil dropbox;
	
	public ImportAppConfigWindow(Display display) {
		super(display);
		// TODO Auto-generated constructor stub
	}
	
	public void init() {
		dropbox = new DropboxUtil();
	}
	
}
