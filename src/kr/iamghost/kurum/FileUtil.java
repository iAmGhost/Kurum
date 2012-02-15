package kr.iamghost.kurum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

	public static void copy(InputStream fis, FileOutputStream fos) {
		try {
			int bytesRead = 0;
			byte[] buffer = new byte[4096];
			
			while ((bytesRead = fis.read(buffer)) >= 0) {
				fos.write(buffer, 0, bytesRead);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void copy(File source, File dest) {
		if (source.isFile()) {
			try {
				if (dest.isFile()) dest.delete();
				
				dest.getParentFile().mkdirs();
				
				FileInputStream fis = new FileInputStream(source);
				FileOutputStream fos = new FileOutputStream(dest);
				
				copy(fis, fos);
				
				fis.close();
				fos.close();
				
				
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
