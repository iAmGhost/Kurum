package kr.iamghost.kurum;

import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import kr.iamghost.kurum.Environment.Platform;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AppConfigParser extends DefaultHandler {
	public enum Status {
		NONE, APPCONFIG, TITLE, NAME, ENTRIES, ENTRY, PATH, FINALIZE;
	}
	
	private Status status;
	private SAXParser parser;
	private AppConfig tempConfig;
	private AppConfigFileEntry tempFileEntry;
	private String platformString;
	private String tempString;
	
	public AppConfigParser() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		try {
			parser = factory.newSAXParser();
		}
		catch (Exception e) {
			e.printStackTrace();
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
		else if (qName.equalsIgnoreCase("entry") &&
				attributes.getValue("os").equalsIgnoreCase(platformString)) {
			tempConfig.setProcess(attributes.getValue("process"));
			status = Status.ENTRY;
		}
		else if (qName.equalsIgnoreCase("path")) {
			tempFileEntry = new AppConfigFileEntry();
			tempFileEntry.setDropboxPath(attributes.getValue("dropboxPath"));
			status = Status.PATH;
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
		case PATH:
			tempFileEntry.setOriginalPath(Environment.parsePath(tempString));
			status = Status.NONE;
			break;
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("entry")) {
			tempConfig.addFile(tempFileEntry);
		}
		else if(qName.equalsIgnoreCase("AppConfig")) {
			status = Status.FINALIZE;
		}
	}
	
	public void parse(String filePath, Platform platform) {
		switch (platform) {
		case WINDOWS:
			platformString = "Windows";
			break;
		case MAC:
			platformString = "Mac";
			break;
		case LINUX:
			platformString = "Linux";
			break;
		}
		
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
		if (status == Status.FINALIZE)
			return tempConfig;
		
		return null;
	}
}
