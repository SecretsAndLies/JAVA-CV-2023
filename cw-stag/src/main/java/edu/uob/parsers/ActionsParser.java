package edu.uob.parsers;

import edu.uob.GameAction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ActionsParser {


    private HashMap<String, HashSet<GameAction>> actions;
    private File actionFile;

    public ActionsParser(File actionFile) {
        actions = new HashMap<>();
        this.actionFile = actionFile;
        parse();
    }

    public HashMap<String, HashSet<GameAction>> getActions() {
        return actions;
    }

    public HashSet<GameAction> getActionByKeyPhrase(String keyPhrase){
        return this.actions.get(keyPhrase);
    }

    // todo: too long. Needs to be broken up into mehtods.
    private void parse() {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(actionFile);
            Element root = document.getDocumentElement();
            NodeList actions = root.getChildNodes();
            for (int i = 0; i < actions.getLength(); i++) {
                if (i % 2 == 0) {
                    continue;
                }
                // create a new action.
                Element action = (Element) actions.item(i);
                Element subjectsElement = (Element) action.getElementsByTagName("subjects").item(0);
                List<String> subjects = getStringListFromElement(subjectsElement, "entity");
                Element consumedElement = (Element) action.getElementsByTagName("consumed").item(0);
                List<String> consumed = getStringListFromElement(consumedElement, "entity");
                Element producedElement = (Element) action.getElementsByTagName("produced").item(0);
                List<String> produced = getStringListFromElement(producedElement, "entity");
                String narration = action.getElementsByTagName("narration").item(0).getTextContent();
                Element triggers = (Element) action.getElementsByTagName("triggers").item(0);
                List<String> triggerList = getStringListFromElement(triggers, "keyphrase");
                for (String trigger : triggerList) {
                    GameAction gameAction = new GameAction(subjects, consumed, produced, narration);
                    if (this.actions.get(trigger) == null) {
                        HashSet<GameAction> gameActionHashSet = new HashSet<>();
                        gameActionHashSet.add(gameAction);
                        this.actions.put(trigger, gameActionHashSet);
                    } else {
                        this.actions.get((trigger)).add(gameAction);
                    }
                }

            }
        } catch (ParserConfigurationException | SAXException | IOException pce) {
            System.err.println(pce.getMessage());
        }
    }

    private List<String> getStringListFromElement(Element element, String targetElementName) {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < element.getElementsByTagName(targetElementName).getLength(); i++) {
            String phrase = element.getElementsByTagName(targetElementName).item(i).getTextContent();
            stringList.add(phrase);
        }
        return stringList;
    }

    public void print() {
        System.out.println(actions);
    }
}
