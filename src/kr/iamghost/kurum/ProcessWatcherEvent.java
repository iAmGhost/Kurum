package kr.iamghost.kurum;

import java.util.EventObject;

public class ProcessWatcherEvent extends EventObject {

	private static final long serialVersionUID = -3947940226008487625L;
	private String processName;
	
	public ProcessWatcherEvent(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}
	
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	
	public String getProcessName() {
		return processName;
	}

}
