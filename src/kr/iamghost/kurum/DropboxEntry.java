package kr.iamghost.kurum;

import java.util.Date;
import java.util.List;

import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.RESTUtility;

public class DropboxEntry {
	private Entry entry;
	public String rev = "";
	public String size = "";
	public Date modifydate = new Date();
	public long bytes = 0;
	public boolean isDir = false;
	public boolean isValid = false;
	public boolean isDeleted = false;
	public String fileName = "";
	
	public DropboxEntry() {
		
	}
	
	public DropboxEntry(Entry entry) {
		setEntry(this.entry);
	}
	
	public void setEntry(Entry entry) {
		if (entry != null) {
			isValid = true;
			this.entry = entry;
			rev = entry.rev;
			isDeleted = entry.isDeleted;
			modifydate = RESTUtility.parseDate(entry.modified);
			size = entry.size;
			bytes = entry.bytes;
			isDir = entry.isDir;
			fileName = entry.fileName();
		}
	}
	
	public List<Entry> getContents() {
		if (isValid) {
			return entry.contents;
		} else {
			return null;
		}
	}
	
	public Entry getEntry() {
		return entry;
	}
}
