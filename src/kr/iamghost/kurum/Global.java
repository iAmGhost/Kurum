package kr.iamghost.kurum;
import java.util.HashMap;

import javax.swing.event.EventListenerList;


public class Global {
	
	private static HashMap<String, String> strings = new HashMap<String, String>();
	private static HashMap<String, Integer> ints = new HashMap<String, Integer>();
	private static HashMap<String, Boolean> bools = new HashMap<String, Boolean>();
	private static EventListenerList eventList = new EventListenerList();
	
	public static void addEventlistener(GlobalEventListener ls) {
		eventList.add(GlobalEventListener.class, ls);
	}
	
	public static void raiseEvents(String key) {
		GlobalEventListener[] ls = eventList.getListeners(GlobalEventListener.class);
		
		for (GlobalEventListener l: ls) {
			GlobalEvent event = new GlobalEvent(Global.class);
			event.setEventKey(key);
			l.onGlobalSet(event);
		}
	}
	
	public static void set(String key, String value) {
		strings.put(key, value);
		raiseEvents(key);
	}
	
	public static void set(String key, int value) {
		ints.put(key, value);
		raiseEvents(key);
	}
	
	public static void set(String key, boolean value) {
		bools.put(key, value);
		raiseEvents(key);
	}
	
	public static String getString(String key) {
		return strings.get(key);
	}
	
	public static int getInt(String key) {
		return ints.get(key);
	}
	
	public static boolean getBool(String key) {
		return bools.get(key);
	}
}
