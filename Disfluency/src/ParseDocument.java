

import java.io.*;
import java.text.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

public class ParseDocument {

	DocumentBuilder builder = null;

	public ParseDocument(){
		
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
	}
	
	
	
	public SpeakerDoc process(File f)
	{		
		SpeakerDoc sd = new SpeakerDoc();
		Document d = null; 
		try{
			d = builder.parse(f);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		NodeList listAG = d.getElementsByTagName("AG");		
		
		for(int i=0; i<listAG.getLength(); i++){
			
			AG ag = new AG();
			Element agelement = (Element) listAG.item(i);
			
			//set Attributes of an AG
			NamedNodeMap nnm = listAG.item(i).getAttributes();
			Node n1 = nnm.getNamedItem("id");
			Node n2 = nnm.getNamedItem("timeline");			
			ag.setId(n1.getTextContent());
			ag.setTimeline(n2.getTextContent());
			
			//set Metadata for an AG
			Metadata m = new Metadata();
			
			agelement.getElementsByTagName("MetaData");
			
			
			
			
			
		}
		
		return sd;

	}
	
	
}
