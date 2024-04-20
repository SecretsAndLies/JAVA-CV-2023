package edu.uob;

import edu.uob.GameEntities.Player;
import edu.uob.parsers.ActionsParser;
import edu.uob.parsers.EntityParser;

import java.io.File;
import java.util.*;

public class GameEngine {
    private final ActionsParser actionsParser;
    private final EntityParser entityParser;
    private final HashMap<String, Player> players;

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
        List<String> listOfBuiltInCommandWords = Arrays.asList("health", "goto", "drop", "get", "inv", "inventory", "look");
        if (commandContainsBuiltInKeywords(commandText, listOfBuiltInCommandWords)) {
            response = executeBuiltInCommand(commandText, player, listOfBuiltInCommandWords);
        } else {
            response = handleComplexCommand(commandText, player);
        }

        return response;
    }

    private boolean commandContainsBuiltInKeywords(String[] commandText, List<String> listOfBuiltInCommandWords) {
        return !Collections.disjoint(Arrays.stream(commandText).toList(), listOfBuiltInCommandWords);
    }

    private void placeBuiltInCommandWordsAtStartOfCommand(String[] commandText, List<String> listOfBuiltInCommandWords) {
        for (String word : listOfBuiltInCommandWords) {
            int index = Arrays.stream(commandText).toList().indexOf(word);
            if (index == -1) {
                continue;
            }
            // put the keyword at the start.
            String temp = commandText[index];
            commandText[index] = commandText[0];
            commandText[0] = temp;
        }
    }

    // todo repetitive.
    private String executeBuiltInCommand(String[] commandText, Player player, List<String> listOfBuiltInCommandWords) {
        placeBuiltInCommandWordsAtStartOfCommand(commandText, listOfBuiltInCommandWords);
        String invalidLength = "Can't understand this command";
        switch (commandText[0]) {
            case "look" -> {
                if (commandIsInvalidLength(commandText, 1)) {
                    return invalidLength;
                }
                return player.getLocation().getDescriptionOfLocation(player);
            }
            case "inv", "inventory" -> {
                if (commandIsInvalidLength(commandText, 1)) {
                    return invalidLength;
                }
                return player.getInventoryString();
            }
            case "get" -> {
                if (commandIsInvalidLength(commandText, 2)) {
                    return invalidLength;
                }
                return player.getItemFromCurrentLocation(commandText[1]);
            }
            case "drop" -> {
                if (commandIsInvalidLength(commandText, 2)) {
                    return invalidLength;
                }
                return player.dropItemInLocation(commandText[1]);
            }
            case "goto" -> {
                if (commandIsInvalidLength(commandText, 2)) {
                    return invalidLength;
                }
                return player.gotoLocation(commandText[1]);
            }
            case "health" -> {
                if (commandIsInvalidLength(commandText, 1)) {
                    return invalidLength;
                }
                return player.getHealthString();
            }
        }
        return invalidLength;
    }

    private boolean commandIsInvalidLength(String[] command, int length) {
        return command.length != length;
    }

    // open trapdoor
    // open trapdoor with key
    // unlock trapdoor
    // unlock trapdoor with key
    private String handleComplexCommand(String[] commandText, Player player) throws GameException {
        ArrayList<String> actionKeywords = getActionKeywords(commandText);
        // is there ONE actionkeyword only. If not error.
        if (actionKeywords.size() != 1) {
            return "A command must include only and only one action keyphrase.";
        }
        String actionKeyWord = actionKeywords.get(0);
        ArrayList<String> subjects = getSubjects(commandText, player);
        if (subjects.isEmpty()) {
            // this could be caused if a player lacks a resource or if they don't include any entity keywords
            // (eg: open)
            return "Can't execute this action.";
        }
        HashSet<GameAction> allActions = actionsParser.getActionByKeyPhrase(actionKeyWord);
        ArrayList<GameAction> potentialActions = new ArrayList<>();
        for (GameAction action : allActions) {
            // take the list of subjects. If an action does not contain ALL of the given subjects, exclude it
            if (!action.actionContainsAllSubjects(subjects)) {
                continue;
            }
            if (isActionPossible(action, player)) {
                potentialActions.add(action);
            }
        }
        if (potentialActions.size() > 1) {
            return "Multiple actions are available, can you be more specific";
        }
        if (potentialActions.isEmpty()) {
            return "I can't do that.";
        }
        return executeAction(potentialActions.get(0), player);
    }


    // todo: put in the action class.
    // TODO: returns true if this action is possible for this player to complete at the moment
    // ie: do they have the required items.
    private boolean isActionPossible(GameAction action, Player player) {
        // get the items in the location and players inventory
        for (String itemName : action.getSubjects()) {
            if (!player.environmentIncludesItemName(itemName)) {
                return false;
            }
        }
        return true;
    }

    // take the action and implement its effects
    private String executeAction(GameAction action, Player player) throws GameException {
        // produce the items that should be produced
        for (String item : action.getProduced()) {
            player.produceItem(item);
        }
        for (String item : action.getConsumed()) {
            player.consumeItem(item);
        }
        return action.getNarration();
    }

    // get all the subjects mentioned in the command
    private ArrayList<String> getSubjects(String[] commandText, Player player) {
        ArrayList<String> subjects = new ArrayList<>();
        for (String word : commandText) {
            if (player.environmentIncludesItemName(word)) {
                subjects.add(word);
            }
        }
        return subjects;
    }

    private ArrayList<String> getActionKeywords(String[] commandText) {
        ArrayList<String> actionKeywords = new ArrayList<>();
        // get all the actionKeywords in the command
        for (String word : commandText) {
            if (actionsParser.getActions().containsKey(word)) {
                actionKeywords.add(word);
            }
        }
        return actionKeywords;

    }


    private String[] tokenizeCommandText(String commandParts) {
        // Remove any whitespace at the beginning and end of the query
        commandParts = commandParts.strip();

        // remove special characters "a a," becomes a a
        commandParts = commandParts.replaceAll("[^a-zA-Z0-9\\s]", " ");

        // replace multiple spaces with single space a   a becomes a a
        commandParts = commandParts.replaceAll(" +", " ");

        // gets the actions that contain multiple words, ordered by most to least words
        ArrayList<String> mutliWordActions = actionsParser.getMultiWordActions();
        // replace those actions in the command with -. So cut down becomes cut-down
        for (String action : mutliWordActions) {
            commandParts = commandParts.replaceAll(action, action.replace(" ", "-"));
        }
        // split the strings up by spaces - so cut-down test becomes cut-down, test
        String[] commandText = commandParts.toLowerCase().strip().split(" ");

        // reform the valid commands  so cut-down, test becomes cut down, test
        for (int i = 0; i < commandText.length; i++) {
            commandText[i] = commandText[i].replaceAll("-", " ");
        }
        return commandText;
    }
}
