package edu.uob;

import edu.uob.game_entities.Location;
import edu.uob.game_entities.Player;
import edu.uob.parsers.ActionsParser;
import edu.uob.parsers.EntityParser;

import java.io.File;
import java.util.*;

public class GameEngine {
    private final ActionsParser actionsParser;
    private final EntityParser entityParser;
    private final Map<String, Player> players;
    private String[] commandText;
    private Player player;
    String playerName;
    List<String> listOfBuiltInCommandWords = Arrays.asList("health", "goto",
            "drop", "get", "inv", "inventory", "look");

    String defaultErrorMessage = "Can't understand this command";

    public GameEngine(File entitiesFile, File actionsFile) {
        actionsParser = new ActionsParser(actionsFile);
        entityParser = new EntityParser(entitiesFile);
        players = new HashMap<>();
    }

    public String handleCommand(String command) throws GameException {
        String response;
        this.commandText = extractCommandText(command);
        getThePlayer();
        if (commandContainsBuiltInKeywords()) {
            response = executeBuiltInCommand();
        } else {
            response = handleComplexCommand();
        }
        return response;
    }

    private void getThePlayer() throws GameException {
        if (playerName.isEmpty()) {
            throw new GameException(
                    "Player names must be at least one letter.");
        }
        player = players.get(playerName);
        if (player == null) {
            player = new Player(playerName, "another player",
                    entityParser.getStartLocation(),
                    entityParser.getGameLocations());
            players.put(playerName, player);
            entityParser.getStartLocation().addCharacterToLocation(player);
        }
    }

    private String[] extractCommandText(String command) throws GameException {
        String[] commandParts = command.split(":");
        playerName = commandParts[0];
        checkPlayerNameIsValid();
        if (commandParts.length != 2) {
            throw new GameException("No player name found.");
        }
        return tokenizeCommandText(commandParts[1]);
    }

    private void checkPlayerNameIsValid() throws GameException {
        if (playerName.matches(".*[^a-zA-Z\\s'-].*")) {
            //uppercase and lowercase letters, spaces, apostrophes and hyphens
            throw new GameException("Player name is invalid.");
        }
    }

    private boolean commandContainsBuiltInKeywords() {
        return !Collections.disjoint(Arrays.stream(commandText).toList(),
                listOfBuiltInCommandWords);
    }

    private void placeBuiltInCommandWordsAtStartOfCommand() throws
            GameException {
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

    private String executeBuiltInCommand() {
        try {
            placeBuiltInCommandWordsAtStartOfCommand();
            if (commandContainsCustomActionKeywords()) {
                return defaultErrorMessage;
            }
        } catch (GameException e) {
            return "found more command words than expected";
        }

        return performBuiltInCommandAction(commandText[0]);
    }

    private String performBuiltInCommandAction(String command) {
        switch (command) {
            case "look" -> {
                return handleLook();
            }
            case "inv", "inventory" -> {
                return handleInventory();
            }
            case "get" -> {
                return handleGet();
            }
            case "drop" -> {
                return handleDrop();
            }
            case "goto" -> {
                return handleGoto();
            }
            case "health" -> {
                return handleHealth();
            }
            default -> {
                return defaultErrorMessage;
            }
        }
    }

    private String handleLook() {
        if (commandContainsEntities()) {
            return defaultErrorMessage;
        }
        return player.getLocation().getDescriptionOfLocation(player);
    }

    private String handleInventory() {
        if (commandContainsEntities()) {
            return defaultErrorMessage;
        }
        return player.getInventoryString();
    }

    private String handleGet() {
        try {
            String artifact = getArtifactForGetAndDrop();
            return player.getItemFromCurrentLocation(artifact);
        } catch (GameException e) {
            return defaultErrorMessage;
        }
    }

    private String handleDrop() {
        try {
            String artifact = getArtifactForGetAndDrop();
            return player.dropItemInLocation(artifact);
        } catch (GameException e) {
            return defaultErrorMessage;
        }
    }

    private String handleGoto() {
        try {
            String location = getLocationFromCommand();
            return player.gotoLocation(location);
        } catch (GameException e) {
            return defaultErrorMessage;
        }
    }

    private String handleHealth() {
        if (commandContainsEntities()) {
            return defaultErrorMessage;
        }
        return player.getHealthString();
    }

    private boolean commandContainsCustomActionKeywords() {
        for (String word : commandText) {
            if (actionsParser.getActions().containsKey(word)) {
                return true;
            }
        }
        return false;
    }


    private boolean commandContainsEntities() {
        for (String word : commandText) {
            if (entityParser.isEntity(word)) {
                return true;
            }
        }
        return false;
    }

    private String getLocationFromCommand() throws GameException {
        String location = "";
        boolean isLocationSet = false;

        for (String word : commandText) {
            validateWordNotArtifactCharacterOrFurniture(word);

            String foundLocation = trySetLocation(word, location,
                    isLocationSet);
            if (!foundLocation.isEmpty()) {
                if (isLocationSet) {
                    throw new GameException("too many locations");
                }
                location = foundLocation;
                isLocationSet = true;
            }
        }

        if (!isLocationSet) {
            throw new GameException("no valid location found");
        }

        return location;
    }

    private void validateWordNotArtifactCharacterOrFurniture(String word) throws
            GameException {
        if (entityParser.getGameArtifacts().containsKey(word)) {
            throw new GameException("artifacts");
        }
        if (entityParser.getGameCharacters().containsKey(word)) {
            throw new GameException("characters");
        }
        if (entityParser.getGameFurniture().containsKey(word)) {
            throw new GameException("furniture");
        }
    }

    private String trySetLocation(String word, String currentLocation,
                                  boolean isLocationSet) {
        if (entityParser.getGameLocations().containsKey(word)) {
            if (!isLocationSet || !currentLocation.equals(word)) {
                return entityParser.getGameLocations().get(word).getName();
            }
        }
        return "";
    }


    private String getArtifactForGetAndDrop() throws GameException {
        int countOfArtifacts = 0;
        String artifact = "";
        for (String word : commandText) {
            if (entityParser.getGameArtifacts().containsKey(word)) {
                // duplicates are OK.
                if (artifact.equals(word)) {
                    continue;
                }
                countOfArtifacts++;
                artifact = entityParser.getGameArtifacts().get(word).getName();
            }
            checkNotCharacterFurnitureOrLocation(word);
        }
        if (countOfArtifacts != 1) {
            throw new GameException("too many locations");
        }
        return artifact;
    }

    private void checkNotCharacterFurnitureOrLocation(String word) throws
            GameException {
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

    private String handleComplexCommand() throws GameException {
        List<String> actionKeywords = getActionKeywords();
        List<GameAction> potentialActions = new ArrayList<>();

        for (String actionKeyWord : actionKeywords) {
            populatePotentialActions(actionKeyWord, potentialActions);
        }
        potentialActions = filterForUniqueActions(potentialActions);
        if (potentialActions.size() > 1) {
            return "Multiple actions are available, can you be more specific";
        }
        if (potentialActions.isEmpty()) {
            return "I can't do that.";
        }
        return executeAction(potentialActions.get(0));

    }

    private void populatePotentialActions(String actionKeyWord,
                                          List<GameAction> potentialActions) {
        List<String> subjects = getSubjects();
        if (subjects.isEmpty()) {
            return;
        }
        Set<GameAction> allActions = actionsParser.getActionByKeyPhrase(
                actionKeyWord);
        for (GameAction action : allActions) {
            // take the list of subjects. If an action does not contain ALL of the given subjects, exclude it
            if (!action.actionContainsAllSubjects(subjects)) {
                continue;
            }
            if (isActionPossible(action)) {
                potentialActions.add(action);
            }
        }
    }


    private List<GameAction> filterForUniqueActions(
            List<GameAction> potentialActions) {
        if (potentialActions.size() <= 1) {
            return potentialActions;
        }
        List<GameAction> filteredGameActions = new ArrayList<>();
        for (GameAction gameAction : potentialActions) {
            if (listContainsAction(gameAction, filteredGameActions)) {
                continue;
            }
            filteredGameActions.add(gameAction);
        }

        return filteredGameActions;
    }

    private boolean listContainsAction(GameAction gameAction,
                                       List<GameAction> actionList) {
        for (GameAction gameAction1 : actionList) {
            if (gameAction.deepEquals(gameAction1)) {
                return true;
            }
        }
        return false;
    }


    private boolean playerIsMissingNeededItem(GameAction action) {
        for (String itemName : action.getSubjects()) {
            if (!player.playerImmediateEnviromentContainsItem(itemName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPossibleToProduceRequiredItems(GameAction action) {
        for (String itemName : action.getProduced()) {
            if ("health".equals(itemName)) {
                continue;
            }
            // check if it's the current location. This cannot be produced or consumed.
            if (player.getLocation().getName().equals(itemName)) {
                return false;
            }
            // check if location.
            if (entityParser.getGameLocations().containsKey(itemName)) {
                return true;
            }
            if (!worldIncludesItemName(itemName)) {
                return false;
            }
        }
        return true;
    }

    private boolean isPossibleToConsumeRequiredItems(GameAction action) {
        for (String itemName : action.getConsumed()) {
            if (!isItemConsumable(itemName)) {
                return false;
            }
        }
        return true;
    }

    private boolean isItemConsumable(String itemName) {
        if ("health".equals(itemName)) {
            return true;
        }
        // check if it's the current location. This cannot be produced or consumed.
        if (player.getLocation().getName().equals(itemName)) {
            return false;
        }

        // Check if item is a location and if it's connected to the player's current location.
        if (entityParser.getGameLocations().containsKey(itemName)) {
            return isConnectedLocationAccessible(itemName);
        }
        // Check if the item is included in the world and accessible to the player.
        // noe that unlike the worldIncludes.. this also checks the player inventory.
        return player.worldIncludesItemName(itemName, false);
    }

    private boolean isConnectedLocationAccessible(String locationName) {
        try {
            player.getLocation().getConnectedLocation(locationName);
            return true;
        } catch (GameException e) {
            return false;
        }
    }


    // returns true if this action is possible for this player to complete at the moment
    // ie: do they have the required items.
    private boolean isActionPossible(GameAction action) {
        // Does the player have all the items they need to execute the action?
        if (playerIsMissingNeededItem(action)) {
            return false;
        }
        // can we consume all the items we need to?
        if (!isPossibleToConsumeRequiredItems(action)) {
            return false;
        }

        // can we produce all the required items
        return isPossibleToProduceRequiredItems(action);
    }

    public boolean worldIncludesItemName(String item) {
        for (Location location : entityParser.getGameLocations().values()) {
            if (location.getArtifacts().containsKey(item)) {
                return true;
            }
            if (location.getFurniture().containsKey(item)) {
                return true;
            }
            if (location.getCharacters().containsKey(item)) {
                return true;
            }
        }
        return false;
    }

    // take the action and implement its effects
    private String executeAction(GameAction action) throws
            GameException {
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
    private List<String> getSubjects() {
        List<String> subjects = new ArrayList<>();
        for (String word : commandText) {
            if (subjects.contains(word)) {
                continue;
            }
            if (player.worldIncludesItemName(word, true)) {
                subjects.add(word);
            }
        }
        return subjects;
    }

    private List<String> getActionKeywords() {
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
            commandParts = commandParts.replaceAll(action,
                    action.replace(" ", "-"));
        }
        // split the strings up by spaces - so cut-down test becomes cut-down, test
        String[] commandText = commandParts.toLowerCase(
                Locale.ENGLISH).strip().split(" ");

        // reform the valid commands  so cut-down, test becomes cut down, test
        for (int i = 0; i < commandText.length; i++) {
            commandText[i] = commandText[i].replaceAll("-", " ");
        }
        return commandText;
    }
}
