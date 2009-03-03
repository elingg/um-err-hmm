

import java.io.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

public class ParseDocument {
	
	private Document document;

	public ParseDocument(File f){
		DocumentBuilder builder = null;
		try {
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    factory.setValidating(true);
		    factory.setIgnoringElementContentWhitespace(true);      
		    builder = factory.newDocumentBuilder();
		}
		catch (FactoryConfigurationError e) {
		    System.out.println("unable to get a document builder factory");
		    System.exit(2);
		} 
		catch (ParserConfigurationException e) {
		    System.out.println("parser was unable to be configured");
		    System.exit(2);
		}
		
		try{
			document = builder.parse(f);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}		 

	}
	
	public void process(Document d)
	{
		
	}
	
	
	
	
}
