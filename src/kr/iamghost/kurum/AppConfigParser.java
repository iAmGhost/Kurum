package kr.iamghost.kurum;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.SystemUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AppConfigParser extends DefaultHandler {
	public enum Status {
		NONE, APPCONFIG, TITLE, AUTHOR, ENTRIES, ENTRY, VAR, LUA, FILE, DIR, FINALIZE;
	}
	
	private Status status;
	private SAXParser parser;
	private AppConfig tempConfig;
	private AppConfigFileEntry tempFileEntry;
	private AppConfigVariable tempVariable;
	private String platformString;
	private String tempString;
	private String filePath;
	private StringBuffer stringBuffer;
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
	
	public void startElement(String uri, String localName, String qName,
			Attributes attributes)
			throws SAXException {
		stringBuffer = new StringBuffer();
		
		if (qName.equalsIgnoreCase("AppConfig")) {
			status = Status.APPCONFIG;
			tempConfig = new AppConfig();
			tempConfig.setOriginalFile(new File(filePath));
			tempConfig.setAppName(attributes.getValue("internalName"));
		}
		else if(qName.equalsIgnoreCase("title")) {
			status = Status.TITLE;
		}
		else if(qName.equalsIgnoreCase("author")) {
			status = Status.AUTHOR;
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
		else if (qName.equalsIgnoreCase("var") && found) {
			tempVariable = new AppConfigVariable();
			tempVariable.setName(attributes.getValue("name"));
			tempVariable.setType(attributes.getValue("type"));
			tempVariable.setFilter(attributes.getValue("filter"));
			status = Status.VAR;
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
		else if(qName.equalsIgnoreCase("lua")) {
			tempConfig.setUsesLuaScript(true);
			
			status = Status.LUA;
		}
		else
		{
			status = Status.NONE;
		}
	}

	public void characters(char ch[], int start, int length) throws SAXException {
		tempString = new String(ch, start, length);
		
		
		switch (status) {
		
		case TITLE:
			tempConfig.setAppTitle(tempString);
			status = Status.NONE;
			break;
			
		case VAR:
			tempVariable.setMessage(tempString);
			status = Status.NONE;
			break;
			
		case AUTHOR:
			tempConfig.setAuthor(tempString);
			status = Status.NONE;
			break;

		case FILE:
		case DIR:
			tempFileEntry.setOriginalPath(Environment.parsePath(tempString));
			status = Status.NONE;
			break;
			
		case LUA:
			if (stringBuffer != null)
				stringBuffer.append(tempString);
			break;
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ((qName.equalsIgnoreCase("dir") || qName.equalsIgnoreCase("file")) && found) {
			tempConfig.addFile(tempFileEntry);
		}
		else if(qName.equalsIgnoreCase("var") && found) {
			tempConfig.addVar(tempVariable);
		}
		else if(qName.equalsIgnoreCase("AppConfig")) {
			status = Status.FINALIZE;
		}
		else if(qName.equalsIgnoreCase("Lua")) {
			tempConfig.setLuaScriptContent(stringBuffer.toString());
			status = Status.NONE;
		}
		
		stringBuffer = null;
	}
	
	public void parse(String filePath) {
		try {
			this.filePath = filePath;
			parser.parse(new File(filePath), this);
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
