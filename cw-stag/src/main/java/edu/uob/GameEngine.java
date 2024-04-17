package edu.uob;

import edu.uob.GameEntities.Player;
import edu.uob.parsers.ActionsParser;
import edu.uob.parsers.EntityParser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GameEngine {
    ActionsParser actionsParser;
    EntityParser entityParser;
    private HashMap<String, Player> players;

    public GameEngine(File entitiesFile, File actionsFile) {
        actionsParser = new ActionsParser(actionsFile);
        entityParser = new EntityParser(entitiesFile);
        players = new HashMap<>();
    }

    public String handleCommand(String command) {
        String response = "";
        String[] commandParts = command.split(":");
        String playerName = commandParts[0];
        String[] commandText = tokenizeCommandText(commandParts[1]);
        Player player;
        // if the player has never been seen before, add them to list of players, and put them in start location
        player = players.get(playerName);
        if (player == null) {
            player = new Player(playerName, "another player", entityParser.getStartLocation());
            players.put(playerName, player);
            entityParser.getStartLocation().addCharacterToLocation(player);
        }

        // todo: this is too permissive linguistically
        if (commandText[0].equals("look")) {
            response = player.getLocation().getDescriptionOfLocation(player);
        }
        else if (commandText[0].equals("inv") || commandText[0].equals("inventory")) {
            response = player.getInventoryString();
        }
        else if (commandText[0].equals("get")) {
            response = player.getItemFromCurrentLocation(commandText[1]);
        }
        else if (commandText[0].equals("drop")) {
            response = player.dropItemInLocation(commandText[1]);
        }
        else if (commandText[0].equals("goto")) {
            response = player.gotoLocation(commandText[1]);
        }
        else if (commandText[0].equals("health")) {
            response = player.getHealthString();
        }
        else{
            response = handleComplexCommand(commandText, player);
        }

        return response;
    }

    // open trapdoor
    // open trapdoor with key
    // unlock trapdoor
    // unlock trapdoor with key
    private String handleComplexCommand(String[] commandText, Player player){
        ArrayList<String> actionKeywords=getActionKeywords(commandText);
        // is there ONE actionkeyword only. If not error.
        if(actionKeywords.size()!=1){
            return "A command must include only and only one action keyphrase.";
        }
        String actionKeyWord = actionKeywords.get(0);
        ArrayList<String> subjects = getSubjects(commandText,player);
        if(subjects.isEmpty()){
            // this could be caused if a player lacks a resource or if they don't include any entity keywords
            // (eg: open)
            return "Can't execute this action.";
        }

        HashSet<GameAction> allActions = actionsParser.getActionByKeyPhrase(actionKeyWord);
        ArrayList<GameAction> potentialActions = new ArrayList<>();
        for (GameAction action : allActions){
            // take the list of subjects. If an action does not contain ALL of the given subjects, exclude it
            // note that this could be a subset.
            if(!action.actionContainsAllSubjects(subjects)){
                continue;
            }
            if(isActionPossible(action,player)){
                potentialActions.add(action);
            }
        }
        if(potentialActions.size()!=1){
            return "I'm not sure what action you'd like to do.";
        }

        return executeAction(potentialActions.get(0), player);

    }



    // todo: put in the action class.
    // TODO: returns true if this action is possible for this player to complete at the moment
    // ie: do they have the required items.
    private boolean isActionPossible(GameAction action, Player player){
        return true;
    }

    private String executeAction(GameAction action, Player player){
        // take the action and implement its effects
        // delete the items that are consumed (player.consumeItem) - this can be in the location or inventory.
        // produce the items that should be produced
        // (note that if the item is a location - then move the player to that location.)
        return action.narration;
    }

    // get all the subjects mentioned in the command
    private ArrayList<String> getSubjects(String[] commandText, Player player) {
        ArrayList<String> subjects =new ArrayList<>();
        for (String word : commandText){
            if(player.environmentIncludesItemName(word)){
                subjects.add(word);
            }
        }
        return subjects;
    }

    private ArrayList<String> getActionKeywords(String[] commandText){
        ArrayList<String> actionKeywords =new ArrayList<>();
        // get all the actionKeywords in the command
        for (String word : commandText){
            if(actionsParser.getActions().containsKey(word)){
                actionKeywords.add(word);
            }
        }
        return actionKeywords;

    }


    private String[] tokenizeCommandText(String commandParts) {
        // todo make more robust - handle double whitespaces etc.
        String[] commandText = commandParts.toLowerCase().strip().split(" ");
        return commandText;
    }
}
