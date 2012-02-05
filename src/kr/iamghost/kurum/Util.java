package kr.iamghost.kurum;

import java.io.IOException;
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
	    try {
			if (SystemUtils.IS_OS_WINDOWS) {
				Runtime.getRuntime().exec("explorer.exe \"" +
						path.replaceAll("/", "\\\\") + "\"");
			}
			else {
				Runtime.getRuntime().exec("/usr/bin/open \"" + path + "\"");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	    
	}
}
