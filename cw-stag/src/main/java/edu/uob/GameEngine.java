package edu.uob;

import edu.uob.GameEntities.Player;
import edu.uob.parsers.ActionsParser;
import edu.uob.parsers.EntityParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class GameEngine {
    private ActionsParser actionsParser;
    private EntityParser entityParser;
    private HashMap<String, Player> players;

    public GameEngine(File entitiesFile, File actionsFile) {
        actionsParser = new ActionsParser(actionsFile);
        entityParser = new EntityParser(entitiesFile);
        players = new HashMap<>();
    }

    public String handleCommand(String command) throws GameException {
        String response = "";
        String[] commandParts = command.split(":");
        String playerName = commandParts[0];
        String[] commandText = tokenizeCommandText(commandParts[1]);
        Player player;
        // if the player has never been seen before, add them to list of players, and put them in start location
        player = players.get(playerName);
        if (player == null) {
            player = new Player(playerName, "another player", entityParser.getStartLocation(), entityParser.getGameLocations());
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

    public ActionsParser getActionsParser() {
        return actionsParser;
    }

    public EntityParser getEntityParser() {
        return entityParser;
    }

    public HashMap<String, Player> getPlayers() {
        return players;
    }

    // open trapdoor
    // open trapdoor with key
    // unlock trapdoor
    // unlock trapdoor with key
    private String handleComplexCommand(String[] commandText, Player player) throws GameException {
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
            if(!action.actionContainsAllSubjects(subjects)){
                continue;
            }
            if(isActionPossible(action,player)){
                potentialActions.add(action);
            }
        }
        if(potentialActions.size()!=1){
            return "I can't do that.";
        }
        return executeAction(potentialActions.get(0), player);
    }



    // todo: put in the action class.
    // TODO: returns true if this action is possible for this player to complete at the moment
    // ie: do they have the required items.
    private boolean isActionPossible(GameAction action, Player player){
        // get the items in the location and players inventory
        for(String itemName : action.getSubjects()){
            if(!player.environmentIncludesItemName(itemName)){
                return false;
            }
        }
        return true;
    }

    // take the action and implement its effects
    private String executeAction(GameAction action, Player player) throws GameException {
        // produce the items that should be produced
        for(String item : action.getProduced()){
            player.produceItem(item);
        }
        for(String item : action.getConsumed()) {
            player.consumeItem(item);
        }
        return action.getNarration();
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
        // Remove any whitespace at the beginning and end of the query
        commandParts = commandParts.strip();

       // remove special characters "a a," becomes a a
        commandParts = commandParts.replaceAll("[^a-zA-Z0-9\\s]"," ");

        // replace multiple spaces with single space a   a becomes a a
        commandParts = commandParts.replaceAll(" +"," ");

        // gets the actions that contain multiple words, ordered by most to least words
        ArrayList<String> mutliWordActions = actionsParser.getMultiWordActions();
        // replace those actions in the command with -. So cut down becomes cut-down
        for(String action : mutliWordActions){
            commandParts = commandParts.replaceAll(action,action.replace(" ", "-"));
        }
        // split the strings up by spaces - so cut-down test becomes cut-down, test
        String[] commandText = commandParts.toLowerCase().strip().split(" ");

        // reform the valid commands  so cut-down, test becomes cut down, test
        for(int i=0; i<commandText.length; i++){
            commandText[i] = commandText[i].replaceAll("-", " ");
        }
        return commandText;
    }
}
