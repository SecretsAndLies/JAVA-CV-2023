package edu.uob;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class Parser {
    HashMap<String, HashSet<GameAction>> actions;
    // todo: entities
    public Parser(){
         actions = new HashMap<String, HashSet<GameAction>>();

    }
    public void parseActions(){
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse("config" + File.separator + "basic-actions.xml");
            Element root = document.getDocumentElement();
            NodeList actions = root.getChildNodes();
            for(int i =0; i< actions.getLength(); i++){
                if(i%2==0){
                    continue;
                }
                // create a new action.
                Element action = (Element)actions.item(i);
                System.out.println("Triggers:");
                Element triggers = (Element)action.getElementsByTagName("triggers").item(0);
                getSubElement(triggers,"keyphrase");
                System.out.println("Subjects");
                Element subjects = (Element)action.getElementsByTagName("subjects").item(0);
                getSubElement(subjects,"entity");
                System.out.println("consumed");
                Element consumed = (Element)action.getElementsByTagName("consumed").item(0);
                getSubElement(consumed,"entity");
                System.out.println("oridyced");
                Element produced = (Element)action.getElementsByTagName("produced").item(0);
                getSubElement(produced,"entity");
                System.out.println("narration");
                String narration = action.getElementsByTagName("narration").item(0).getTextContent();
                System.out.println("\t"+narration);
            }

        } catch(ParserConfigurationException pce) {
        } catch(SAXException saxe) {
        } catch(IOException ioe) {
        }
    }
    private void getSubElement(Element element, String targetElementName){
        for (int i=0; i<element.getElementsByTagName(targetElementName).getLength(); i++){
            String triggerPhrase = element.getElementsByTagName(targetElementName).item(i).getTextContent();
            System.out.println("\t"+triggerPhrase);
        }

    }
    // populate the entities list
}
