package kr.iamghost.kurum;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	public static void write(String text) {
		Date date = new Date();
		final SimpleDateFormat format = new SimpleDateFormat("h:mm:ss a");
		
		text = String.format("[%s] %s", format.format(date), text);
		System.out.println(text);
		Global.set("Log", text);
	}
}
