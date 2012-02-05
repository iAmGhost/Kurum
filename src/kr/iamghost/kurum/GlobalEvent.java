package kr.iamghost.kurum;

import java.util.EventObject;

public class GlobalEvent extends EventObject {

	private static final long serialVersionUID = -201301932131513807L;
	private String eventKey;

	public GlobalEvent(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

	public String getEventKey() {
		return eventKey;
	}

	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}
	
	public String getString() {
		return Global.getString(eventKey);
	}
	
	public int getInt() {
		return Global.getInt(eventKey);
	}
	
	public boolean getBool() {
		return Global.getBool(eventKey);
	}
	
	
	public Object getObject() {
		return Global.getObject(eventKey);
	}

}
