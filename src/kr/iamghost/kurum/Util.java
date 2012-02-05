package kr.iamghost.kurum;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.SystemUtils;

public class Util {

	public static Date stringToDate(String dateString) {
		DateFormat sdFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
		dateString = dateString.replaceAll("\\p{Cntrl}", "");
		Date newDate = null;
		
		try {
			newDate = sdFormat.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return newDate;
	}
	
	public static void browseDirectory(String path) {
	    File file = new File(path);
	    if (!file.isDirectory()) file.mkdirs();
	    
	    try {
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
