package kr.iamghost.kurum;

import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.SystemUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AppConfigParser extends DefaultHandler {
	public enum Status {
		NONE, APPCONFIG, TITLE, NAME, ENTRIES, ENTRY, FILE, DIR, FINALIZE;
	}
	
	private Status status;
	private SAXParser parser;
	private AppConfig tempConfig;
	private AppConfigFileEntry tempFileEntry;
	private String platformString;
	private String tempString;
	private boolean found = false;
	
	public AppConfigParser() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		try {
			parser = factory.newSAXParser();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		if (SystemUtils.IS_OS_WINDOWS) {
			platformString = "Windows";
		}
		else if (SystemUtils.IS_OS_MAC) {
			platformString = "Mac";
		}
		else if (SystemUtils.IS_OS_LINUX) {
			platformString = "Linux";
		}
	}
	
	public void startElement(String uri, String localName,String qName, Attributes attributes)
			throws SAXException {
		
		if (qName.equalsIgnoreCase("AppConfig")) {
			status = Status.APPCONFIG;
			tempConfig = new AppConfig();
		}
		else if(qName.equalsIgnoreCase("title")) {
			status = Status.TITLE;
		}
		else if (qName.equals("name")) {
			status = Status.NAME;
		}
		else if (qName.equalsIgnoreCase("entries")) {
			status = Status.ENTRIES;
		}
		else if (qName.equalsIgnoreCase("entry")) {
			if (attributes.getValue("os").equalsIgnoreCase(platformString))
			{
				this.found = true;
				tempConfig.setProcess(attributes.getValue("process"));
				status = Status.ENTRY;
			}
			else
			{
				this.found = false;
			}
		}
		else if (qName.equalsIgnoreCase("file") && found) {
			tempFileEntry = new AppConfigFileEntry();
			tempFileEntry.setDropboxPath(attributes.getValue("dropboxPath"));
			status = Status.FILE;
		}
		else if (qName.equalsIgnoreCase("dir") && found) {
			tempFileEntry = new AppConfigFileEntry();
			tempFileEntry.setDropboxPath(attributes.getValue("dropboxPath"));
			tempFileEntry.setIsFile(false);
			
			if (attributes.getValue("excludes") != null)
				tempFileEntry.setExcludeList(attributes.getValue("excludes"));
			
			if (attributes.getValue("cleanup") != null &&
					attributes.getValue("cleanup").equalsIgnoreCase("true"))
				tempFileEntry.setNeedCleanup(true);
			
			status = Status.DIR;
		}
	}

	public void characters(char ch[], int start, int length) throws SAXException {
		tempString = new String(ch, start, length);

		switch (status) {
		
		case TITLE:
			tempConfig.setAppTitle(tempString);
			status = Status.NONE;
			break;
		case NAME:
			tempConfig.setAppName(tempString);
			status = Status.NONE;
			break;
		case FILE:
		case DIR:
			tempFileEntry.setOriginalPath(Environment.parsePath(tempString));
			status = Status.NONE;
			break;
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("entry") && found) {
				tempConfig.addFile(tempFileEntry);
		}
		else if(qName.equalsIgnoreCase("AppConfig")) {
			status = Status.FINALIZE;
		}
	}
	
	public void parse(String filePath) {
		
		try {
			parser.parse(filePath, this);
		}
		catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public AppConfig getAppConfig() {
		
		return tempConfig;
	}
}
