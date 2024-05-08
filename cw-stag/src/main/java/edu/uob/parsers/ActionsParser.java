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
import java.util.*;

public class ActionsParser {


    private final Map<String, Set<GameAction>> actions;
    private final File actionFile;

    public ActionsParser(File actionFile) {
        actions = new HashMap<>();
        this.actionFile = actionFile;
        parseCommand();
    }

    public Map<String, Set<GameAction>> getActions() {
        return actions;
    }

    // gets the list of actions that contain multiple words, ordered by word number desc.
    public List<String> getMultiWordActions() {
        List<String> multiWordActions = new ArrayList<>();
        for (String keyphrase : actions.keySet()) {
            if (keyphrase.split(" ").length > 1) {
                multiWordActions.add(keyphrase);
            }
        }
        multiWordActions.sort(new WordLenComparator());
        return multiWordActions;
    }

    public Set<GameAction> getActionByKeyPhrase(String keyPhrase) {
        return this.actions.get(keyPhrase);
    }

    // sorts by number of words in the string in descending order (most first.)
    static class WordLenComparator implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            return b.split(" ").length - a.split(" ").length;
        }
    }

    private void parseCommand() {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document document = builder.parse(actionFile);
            Element root = document.getDocumentElement();
            NodeList actions = root.getChildNodes();
            for (int i = 0; i < actions.getLength(); i++) {
                if (i % 2 == 0) {
                    continue;
                }
                // create a new action.
                addNewActionToActionsList(actions, i);
            }
        } catch (ParserConfigurationException | SAXException |
                 IOException pce) {
            System.err.println(pce.getMessage());
        }
    }

    private void addNewActionToActionsList(NodeList actions, int i) {
        Element action = (Element) actions.item(i);
        Element subjectsElement = (Element) action.getElementsByTagName(
                "subjects").item(0);
        List<String> subjects = getStringListFromElement(subjectsElement,
                "entity");
        Element consumedElement = (Element) action.getElementsByTagName(
                "consumed").item(0);
        List<String> consumed = getStringListFromElement(consumedElement,
                "entity");
        Element producedElement = (Element) action.getElementsByTagName(
                "produced").item(0);
        List<String> produced = getStringListFromElement(producedElement,
                "entity");
        String narration = action.getElementsByTagName("narration").item(0)
                .getTextContent();
        Element triggers = (Element) action.getElementsByTagName("triggers")
                .item(0);
        List<String> triggerList = getStringListFromElement(triggers,
                "keyphrase");
        addTriggersFromTriggerList(triggerList, subjects, consumed, produced,
                narration);
    }

    private void addTriggersFromTriggerList(List<String> triggerList,
                                            List<String> subjects,
                                            List<String> consumed,
                                            List<String> produced,
                                            String narration) {
        for (String trigger : triggerList) {
            GameAction gameAction = new GameAction(subjects, consumed, produced,
                    narration);
            if (this.actions.get(trigger) == null) {
                Set<GameAction> gameActionHashSet = new HashSet<>();
                gameActionHashSet.add(gameAction);
                this.actions.put(trigger, gameActionHashSet);
            } else {
                this.actions.get(trigger).add(gameAction);
            }
        }
    }

    private List<String> getStringListFromElement(Element element,
                                                  String targetElementName) {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < element.getElementsByTagName(targetElementName)
                .getLength(); i++) {
            String phrase = element.getElementsByTagName(targetElementName)
                    .item(i).getTextContent().toLowerCase(Locale.ENGLISH);
            stringList.add(phrase);
        }
        return stringList;
    }

}
