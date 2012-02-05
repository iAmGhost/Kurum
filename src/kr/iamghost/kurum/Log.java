package kr.iamghost.kurum;

public class Log {
	final static String TAG="[Kurum]";
	
	public static void write(String text) {
		System.out.println(TAG + " " + text);
	}
}
