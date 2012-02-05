package kr.iamghost.kurum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

public class ZipUtil {
	private ZipArchiveInputStream zis = null;
	private ZipArchiveOutputStream zos = null;

	private byte[] buffer = new byte[4096];
	
	public ZipUtil loadZip(File zipFile) {
		try {
			FileInputStream fis = new FileInputStream(zipFile);
			
			zis = new ZipArchiveInputStream(fis);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return this;
	}
	
	public ZipUtil createZip(File zipFile) {
		try {
			zos = new ZipArchiveOutputStream(zipFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return this;
	}
	
	public void add(AppConfigFileEntry fileEntry) {
		Log.write(fileEntry.getOriginalFile().getAbsolutePath());
		add(new File(fileEntry.getOriginalPath()), fileEntry.getDropboxPath(),
				fileEntry.getExcludeList());
	}
	
	public void add(File file, String pathInZipFile, ArrayList<String> excludes) {
		if (file.isFile()) {
			
			boolean excludeFound = false;
			
			for (String exclude : excludes) {
				if (file.getAbsolutePath().contains(exclude)) {
					excludeFound = true;
					break;
				}
			}
			
			if (!excludeFound) {
				ZipArchiveEntry entry = new ZipArchiveEntry(file, pathInZipFile);
				entry.setSize(file.length());
				entry.setTime(file.lastModified());
				
				try {
					zos.putArchiveEntry(entry);
					FileInputStream fis = new FileInputStream(file);
					int bytesRead = 0;
					
					while((bytesRead = fis.read(buffer)) >= 0) {
						zos.write(buffer, 0, bytesRead);
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else {
			File[] files = file.listFiles();
			for (File innerFile : files) {
				add(innerFile, pathInZipFile + "/" + innerFile.getName(), excludes);
			}
		}
	}
	
	public void save() {
		try {
			zos.closeArchiveEntry();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void extract(File targetDir) {

		try {
			ZipArchiveEntry zipEntry = null;
			
			while ((zipEntry = zis.getNextZipEntry()) != null) {
				File targetFile = new File(targetDir + "/" + zipEntry.getName());

				targetFile.getParentFile().mkdirs();
				
				FileOutputStream fos = new FileOutputStream(targetFile);
				int bytesRead = 0;
				
				while ((bytesRead = zis.read(buffer)) >= 0) {
					fos.write(buffer, 0, bytesRead);
				}
				fos.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			if (zos != null)
				zos.close();
			if (zis != null)
				zis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
