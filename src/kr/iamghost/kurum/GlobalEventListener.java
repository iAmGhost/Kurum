package kr.iamghost.kurum;

import java.util.EventListener;

public interface GlobalEventListener extends EventListener {
	public void onGlobalSet(GlobalEvent e);
}
