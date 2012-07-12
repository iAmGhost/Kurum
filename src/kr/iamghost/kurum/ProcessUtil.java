package kr.iamghost.kurum;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang3.SystemUtils;

public class ProcessUtil {
	private static String processList;
	
	public static void refresh() {
		Process process;
		Runtime runtime;
		String processString = "tasklist";
		
		if (!SystemUtils.IS_OS_WINDOWS) {
			processString = "ps -ax";
		}
		
		try {
			runtime = Runtime.getRuntime();
			process = runtime.exec(processString);
			
			InputStream is = process.getInputStream();
			InputStreamReader isReader = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isReader);
			
			processList = "";
			String line;
			while ((line = br.readLine()) != null) {
				processList += line + "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isProcessExists(String processName) {
		if (processList.contains(processName)) {
			return true;
		}
		
		return false;
	}

}
