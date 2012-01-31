package kr.iamghost.kurum;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.event.EventListenerList;

public class ProcessWatcher implements ActionListener {
	private ArrayList<String> watchList = new ArrayList<String>();
	private ArrayList<String> appearedList = new ArrayList<String>();
	private EventListenerList eventList = new EventListenerList();
	private Timer timer;
	
	
	public void start(int delay) {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				checkProcesses();
				
			}
		}, 0, delay);
	}
	
	public void stop() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
	
	public void checkProcesses() {
		ProcessUtil.refresh();
		
		for (Iterator<String> it = watchList.iterator(); it.hasNext(); )
		{
			String processName = it.next();
			if (ProcessUtil.isProcessExists(processName) && !appearedList.contains(processName)) {
				appearedList.add(processName);
			}
		}
		
		for (Iterator<String> it = appearedList.iterator(); it.hasNext(); )
		{
			String processName = it.next();
			
			if (!ProcessUtil.isProcessExists(processName)) {
				it.remove();
				ProcessWatcherEvent e = new ProcessWatcherEvent(this);
				e.setProcessName(processName);
				fireOnProcessDisappeared(e);
			}
		}
	}
	
	public void addProcess(String processName, ProcessWatcherListener l) {
		watchList.add(processName);
		addEventListener(l);
	}
	
	public void addEventListener(ProcessWatcherListener l) {
		eventList.add(ProcessWatcherListener.class, l);
	}
	
	public void removeEventListener(ProcessWatcherListener l) {
		eventList.remove(ProcessWatcherListener.class, l);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg) {
		checkProcesses();
	}
	
	private void fireOnProcessDisappeared(ProcessWatcherEvent e) {
		ProcessWatcherListener[] ls = eventList.getListeners(ProcessWatcherListener.class);
		
		for (ProcessWatcherListener l: ls) {
			l.onProcessDisappeared(e);
		}
	}

}
