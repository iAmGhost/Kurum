package kr.iamghost.kurum;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SuggestionParser extends DefaultHandler {
	public enum Status {
		NONE, SUGGESTION, ENTRY, TITLE, INTERNALNAME, DESCRIPTION, URL;
	}
	
	private ArrayList<Suggestion> suggestions = new ArrayList<Suggestion>();
	
	private Status status;
	private SAXParser parser;
	private Suggestion tempSuggestion;
	private String currentProcess;
	
	public SuggestionParser() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		try {
			parser = factory.newSAXParser();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startElement(String uri, String localName, String qName,
			Attributes attributes)
			throws SAXException {
		
		if (qName.equalsIgnoreCase("suggestion")) {
			status = Status.SUGGESTION;
			currentProcess = attributes.getValue("processName");
		}
		else if (qName.equalsIgnoreCase("entry"))
		{
			status = Status.ENTRY;
			tempSuggestion = new Suggestion();
			tempSuggestion.setProcessName(currentProcess);
		}
		else if (qName.equalsIgnoreCase("title"))
		{
			status = Status.TITLE;
		}
		else if (qName.equalsIgnoreCase("internalName"))
		{
			status = Status.INTERNALNAME;
		}
		else if (qName.equalsIgnoreCase("description"))
		{
			status = Status.DESCRIPTION;
		}
		else if (qName.equalsIgnoreCase("url"))
		{
			status = Status.URL;
		}
		else
		{
			status = Status.NONE;
		}
	}

	public void characters(char ch[], int start, int length) throws SAXException {
		String tempString = new String(ch, start, length);

		switch (status) {
		
		case TITLE:
			tempSuggestion.setTitle(tempString);
			status = Status.NONE;
			break;
		
		case INTERNALNAME:
			tempSuggestion.setInternalName(tempString);
			status = Status.NONE;
			break;
			
		case DESCRIPTION:
			tempSuggestion.setDescription(tempString);
			status = Status.NONE;
			break;
			
		case URL:
			tempSuggestion.setUrl(tempString);
			status = Status.NONE;
			break;
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("suggestion")) {
			suggestions.add(tempSuggestion);
		}
	}
	
	public void parse(String url) {
		try {
			URL xmlURL = new URL(url);
			parser.parse(new InputSource(xmlURL.openStream()), this);
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
	
	public ArrayList<Suggestion> getSuggestions()
	{
		return suggestions;
	}
}
