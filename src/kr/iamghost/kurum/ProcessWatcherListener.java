package kr.iamghost.kurum;

import java.util.EventListener;

public interface ProcessWatcherListener extends EventListener {
	public void onProcessDisappeared(ProcessWatcherEvent e);
}
