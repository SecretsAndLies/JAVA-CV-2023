package edu.uob;

import edu.uob.GameEntities.Player;
import edu.uob.parsers.ActionsParser;
import edu.uob.parsers.EntityParser;

import java.io.File;
import java.util.HashMap;

public class GameWorld {
    ActionsParser actionsParser;
    EntityParser entityParser;
    private HashMap<String, Player> players;

    public GameWorld(File entitiesFile, File actionsFile) {
        actionsParser = new ActionsParser(actionsFile);
        entityParser = new EntityParser(entitiesFile);
        players = new HashMap<>();
    }

    public String handleCommand(String command) {
        // if the player has never been seen before, add them to list of players, and put them in start location
        String response = "";
        // todo adding like this causes players to see themselves.
        // entityParser.getStartLocation().addCharacterToLocation(player);
        String[] commandParts = command.split(":");
        String playerName = commandParts[0];
        String[] commandText = tokenizeCommandText(commandParts[1]);
        Player player;
        player = players.get(playerName);
        if (player == null) {
            player = new Player(playerName, "Another player.", entityParser.getStartLocation());
            players.put(playerName, player);
        }
        if (commandText[0].equals("look")) {
            response = player.getLocation().toString();
        }
        if (commandText[0].equals("inv") || commandText[0].equals("inventory")) {
            response = player.getInventoryString();
        }
        // todo: this is obviously too permissive linguistically
        if (commandText[0].equals("get")) {
            response = player.getItemFromCurrentLocation(commandText[1]);
        }
        if (commandText[0].equals("drop")) {
            response = player.dropItemInLocation(commandText[1]);
        }
        if (commandText[0].equals("goto")) {
            response = player.gotoLocation(commandText[1]);
        }

        return response;
    }

    private String[] tokenizeCommandText(String commandParts) {
        // todo make more robust - handle double whitespaces etc.
        String[] commandText = commandParts.toLowerCase().strip().split(" ");
        return commandText;
    }
}
