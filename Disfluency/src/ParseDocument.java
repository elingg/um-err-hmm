

import java.io.*;
//import java.text.*;
//import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
//import org.w3c.dom.Text;
import org.w3c.dom.NodeList;
//import org.xml.sax.SAXException;
//import org.xml.sax.SAXParseException;
//import org.xml.sax.ErrorHandler;

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
		catch(Exception e){
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
			NodeList metadataelementslist = agelement.getElementsByTagName("MetadataElement");
			for(int j=0; j<metadataelementslist.getLength(); j++){
				Element metadataelement= (Element) metadataelementslist.item(j);
				if(metadataelement.getAttribute("name").equalsIgnoreCase("endOffset"))
					m.setEndOffset(Integer.parseInt(metadataelement.getTextContent()));
				else if(metadataelement.getAttribute("name").equalsIgnoreCase("paragraph"))
					m.setParagraph(Integer.parseInt(metadataelement.getTextContent()));
				else if(metadataelement.getAttribute("name").equalsIgnoreCase("id"))
					m.setId(metadataelement.getTextContent());
				else if(metadataelement.getAttribute("name").equalsIgnoreCase("startOffset"))
					m.setStartOffset(Integer.parseInt(metadataelement.getTextContent()));
				else if(metadataelement.getAttribute("name").equalsIgnoreCase("sentence"))
					m.setSentence(metadataelement.getTextContent());
				else if(metadataelement.getAttribute("name").equalsIgnoreCase("treebanking"))
					m.setTreeBanking(metadataelement.getTextContent());				
				
			}
			ag.setMetadata(m);
			
			//set anchors for an ag;
			NodeList anchorlist = agelement.getElementsByTagName("Anchor");
            for(int j=0; j<anchorlist.getLength(); j++){
            	Element anchor = (Element) anchorlist.item(j);
            	String id = anchor.getAttribute("id");
            	String offset = anchor.getAttribute("offset");
            	ag.addAnchor(id, Double.valueOf(offset));
            }
                        
            //set annotations for an ag;
			NodeList annotationlist = agelement.getElementsByTagName("Annotation");
			for(int j=0; j< annotationlist.getLength(); j++)			{
				Annotation ann = new Annotation();
				Element annelement= (Element) annotationlist.item(j);
				
				NamedNodeMap nnm1 = annelement.getAttributes();
				ann.setID(nnm1.getNamedItem("id").getTextContent());
				ann.setType(nnm1.getNamedItem("type").getTextContent());
				ann.setStartAnchor(nnm1.getNamedItem("start").getTextContent());
				ann.setStopAnchor(nnm1.getNamedItem("end").getTextContent());
				
				NodeList featurelist = annelement.getElementsByTagName("Feature");
				for(int k=0; k<featurelist.getLength(); k++) {
					Element feature = (Element) featurelist.item(k);
					if(feature.getAttribute("name").equalsIgnoreCase("mdeTokenId")){
						ann.setMdeTokenId(feature.getTextContent());						
					}
					else if (feature.getAttribute("name").equalsIgnoreCase("tag")){
						ann.setTag(feature.getTextContent());
					}
					else if (feature.getAttribute("name").equalsIgnoreCase("index")){
						ann.setIndex(feature.getTextContent());
					}
					
				}
				ag.addAnnotation(ann);
			}		
			
			sd.addAG(ag);			
		}
		
		return sd;

	}	
	
}
