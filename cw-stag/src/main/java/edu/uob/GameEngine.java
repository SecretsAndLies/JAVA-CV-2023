package edu.uob;

import edu.uob.game_entities.Player;
import edu.uob.parsers.ActionsParser;
import edu.uob.parsers.EntityParser;

import java.io.File;
import java.util.*;

public class GameEngine {
    private final ActionsParser actionsParser;
    private final EntityParser entityParser;
    private final Map<String, Player> players;

    public GameEngine(File entitiesFile, File actionsFile) {
        actionsParser = new ActionsParser(actionsFile);
        entityParser = new EntityParser(entitiesFile);
        players = new HashMap<>();
    }

    public String handleCommand(String command) throws GameException {
        String response;
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

    private void placeBuiltInCommandWordsAtStartOfCommand(String[] commandText, List<String> listOfBuiltInCommandWords) throws GameException {
        int numOfCommandWords = 0;
        List<String> commandTextList = Arrays.stream(commandText).toList();
        for (String word : listOfBuiltInCommandWords) {
            if (commandTextList.contains(word)) {
                numOfCommandWords++;
            }
            int index = commandTextList.indexOf(word);
            if (index == -1) {
                continue;
            }
            // put the keyword at the start.
            String temp = commandText[index];
            commandText[index] = commandText[0];
            commandText[0] = temp;
        }
        if (numOfCommandWords != 1) {
            throw new GameException("Multiple command words found.");
        }
    }

    // todo repetitive.
    private String executeBuiltInCommand(String[] commandText, Player player, List<String> listOfBuiltInCommandWords) {

        try {
            placeBuiltInCommandWordsAtStartOfCommand(commandText, listOfBuiltInCommandWords);
        } catch (GameException e) {
            return "found more command words than expected";
        }
        String invalidCommandString = "Can't understand this command";

        // todo: ask if this is acceptable - ie: can we safely reject any built in commands that contain non built in keyprhases?
        if (commandContainsCustomActionKeywords(commandText)) {
            return invalidCommandString;
        }

        switch (commandText[0]) {
            case "look" -> {
                if (isInvalidCommandWithZeroAdditionalEntites(commandText)) {
                    return invalidCommandString;
                }
                return player.getLocation().getDescriptionOfLocation(player);
            }
            case "inv", "inventory" -> {
                if (isInvalidCommandWithZeroAdditionalEntites(commandText)) {
                    return invalidCommandString;
                }
                return player.getInventoryString();
            }
            case "get" -> {
                String artifact;
                try {
                    artifact = getArtifactForGetAndDrop(commandText);
                } catch (GameException e) {
                    return invalidCommandString;
                }
                return player.getItemFromCurrentLocation(artifact);
            }
            case "drop" -> {
                String artifact;
                try {
                    artifact = getArtifactForGetAndDrop(commandText);
                } catch (GameException e) {
                    return invalidCommandString;
                }
                return player.dropItemInLocation(artifact);
            }
            case "goto" -> {
                String location;
                try {
                    location = getLocationFromCommand(commandText);
                } catch (GameException e) {
                    return invalidCommandString;
                }
                return player.gotoLocation(location);
            }
            case "health" -> {
                if (isInvalidCommandWithZeroAdditionalEntites(commandText)) {
                    return invalidCommandString;
                }
                return player.getHealthString();
            }
            default -> {
                return invalidCommandString;
            }
        }
    }

    private boolean commandContainsCustomActionKeywords(String[] commandText) {
        for (String word : commandText) {
            if (actionsParser.getActions().containsKey(word)) {
                return true;
            }
        }
        return false;
    }


    // TODO: validators are repetitive.
    private boolean isInvalidCommandWithZeroAdditionalEntites(String[] commandText) {
        for (String word : commandText) {
            // todo: potnetially you could simplify this if you had a method in the entityparser classs.
            if (entityParser.getGameArtifacts().containsKey(word)) {
                return true;
            }
            if (entityParser.getGameCharacters().containsKey(word)) {
                return true;
            }
            if (entityParser.getGameFurniture().containsKey(word)) {
                return true;
            }
            if (entityParser.getGameLocations().containsKey(word)) {
                return true;
            }
        }
        return false;
    }

    private String getLocationFromCommand(String[] commandText) throws GameException {
        int countOfLocationsFound = 0;
        String location = "";
        for (String word : commandText) {
            if (entityParser.getGameArtifacts().containsKey(word)) {
                throw new GameException("artifacts");
            }
            if (entityParser.getGameCharacters().containsKey(word)) {
                throw new GameException("characters");
            }
            if (entityParser.getGameFurniture().containsKey(word)) {
                throw new GameException("furniture");
            }
            if (entityParser.getGameLocations().containsKey(word)) {
                location = entityParser.getGameLocations().get(word).getName();
                countOfLocationsFound++;
            }
        }
        if (countOfLocationsFound != 1) {
            throw new GameException("too many locations");
        }
        return location;
    }

    private String getArtifactForGetAndDrop(String[] commandText) throws GameException {
        int countOfArtifacts = 0;
        String artifact = "";
        for (String word : commandText) {
            if (entityParser.getGameArtifacts().containsKey(word)) {
                countOfArtifacts++;
                artifact = entityParser.getGameArtifacts().get(word).getName();
            }
            if (entityParser.getGameCharacters().containsKey(word)) {
                throw new GameException("characters");
            }
            if (entityParser.getGameFurniture().containsKey(word)) {
                throw new GameException("furniture");
            }
            if (entityParser.getGameLocations().containsKey(word)) {
                throw new GameException("location");
            }
        }
        if (countOfArtifacts != 1) {
            throw new GameException("too many locations");
        }
        return artifact;
    }


    private String handleComplexCommand(String[] commandText, Player player) throws GameException {
        List<String> actionKeywords = getActionKeywords(commandText);
        // is there ONE actionkeyword only. If not error.
        if (actionKeywords.size() != 1) {
            return "A command must include only and only one action keyphrase.";
        }
        String actionKeyWord = actionKeywords.get(0);
        List<String> subjects = getSubjects(commandText, player);
        if (subjects.isEmpty()) {
            // this could be caused if a player lacks a resource or if they don't include any entity keywords
            // (eg: open)
            return "Can't execute this action.";
        }
        Set<GameAction> allActions = actionsParser.getActionByKeyPhrase(actionKeyWord);
        List<GameAction> potentialActions = new ArrayList<>();
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
    private List<String> getSubjects(String[] commandText, Player player) {
        List<String> subjects = new ArrayList<>();
        for (String word : commandText) {
            if (player.environmentIncludesItemName(word)) {
                subjects.add(word);
            }
        }
        return subjects;
    }

    private List<String> getActionKeywords(String[] commandText) {
        List<String> actionKeywords = new ArrayList<>();
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

        // gets the actions that contain multiple words, ordered by most to least words.
        List<String> mutliWordActions = actionsParser.getMultiWordActions();
        // replace those actions in the command with -. So cut down becomes cut-down
        for (String action : mutliWordActions) {
            commandParts = commandParts.replaceAll(action, action.replace(" ", "-"));
        }
        // split the strings up by spaces - so cut-down test becomes cut-down, test
        String[] commandText = commandParts.toLowerCase(Locale.ENGLISH).strip().split(" ");

        // reform the valid commands  so cut-down, test becomes cut down, test
        for (int i = 0; i < commandText.length; i++) {
            commandText[i] = commandText[i].replaceAll("-", " ");
        }
        return commandText;
    }
}
