package kr.iamghost.kurum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
	public static boolean delete(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File innerFile : files) {
				delete(innerFile);
			}
		}
		
		return file.delete();
	}
	
	public static void copy(File source, File dest) {
		if (source.isFile()) {
			try {
				dest.getParentFile().mkdirs();
				
				FileInputStream fis = new FileInputStream(source);
				FileOutputStream fos = new FileOutputStream(dest);
				
				fos.getChannel().transferFrom(fis.getChannel(), 0, source.length());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			File[] files = source.listFiles();
			for (File innerFile : files) {
				copy(innerFile, new File(dest.getAbsolutePath()
						+ "/" + innerFile.getName()));
			}
		}
	}
}
