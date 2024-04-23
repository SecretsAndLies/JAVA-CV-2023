package edu.uob.game_entities;

import edu.uob.GameException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Location extends GameEntity {
    private final boolean isStartLocation;
    private final Map<String, Item> artifacts;
    private final Map<String, Item> furniture;
    private final Map<String, Character> characters;
    private final Map<String, Location> accessibleLocations;


    public Location(String name, String description, boolean isStartLocation,
                    Map<String, Item> artifacts, Map<String, Item> furniture, Map<String, Character> characters) {
        super(name, description);
        this.isStartLocation = isStartLocation;
        this.artifacts = artifacts;
        this.furniture = furniture;
        this.characters = characters;
        this.accessibleLocations = new HashMap<>();
    }

    //Determines if this location is connected to the given location.

    public Location getConnectedLocation(String name) throws GameException {
        Location location = accessibleLocations.get(name);
        if (location == null) {
            throw new GameException("Location not accessible.");
        }
        return location;
    }

    // removes the item from this location and gives it to the caller
    public Item takeItem(String itemName) {
        Item item = artifacts.remove(itemName);
        if (item == null) {
            item = furniture.remove(itemName);
        }
        return item;
    }

    public Character takeCharacterFromLocation(String characterName) {
        return characters.remove(characterName);
    }

    public void addCharacterToLocation(Character character) {
        characters.put(character.getName(), character);
    }

    public void addItemToLocation(Item item) {
        // not adding characters to location.
        if (item.isCollectable()) {
            artifacts.put(item.getName(), item);
        } else {
            furniture.put(item.getName(), item);
        }
    }

    public Item removeItemFromLocation(String itemName) {
        Item item = artifacts.get(itemName);
        artifacts.remove(itemName);
        return item;
    }

    public void addAccessibleLocation(Location location) {
        accessibleLocations.put(location.getName(), location);
    }

    public void removeAccessibleLocation(String name) {
        accessibleLocations.remove(name);
    }

    public List<String> getAccessibleLocationNames() {
        return new ArrayList<>(accessibleLocations.keySet().stream().toList());
    }

    private String getCharactersString(Map<String, Character> characters) {
        if (characters.isEmpty()) {
            return "";
        }
        final String[] charactersString = {"You can see characters: "};
        characters.forEach((key, value) -> charactersString[0] = charactersString[0] + " " + key + " " + value);
        return charactersString[0];
    }

    public String getDescriptionOfLocation(Player currentPlayer) {
        Map<String, Character> characterCopy = new HashMap<>(characters);
        characterCopy.remove(currentPlayer.getName());
        return "You are in " + getDescription() +
                ". You can see " + artifacts.values() +
                " " + furniture.values() + " " +
                getCharactersString(characterCopy) +
                // todo: turn this into a prettier list - key: value
                " Locations accessible from here are: " + getAccessibleLocationNames();
    }

    public boolean isStartLocation() {
        return isStartLocation;
    }

    public Map<String, Item> getArtifacts() {
        return artifacts;
    }

    public Map<String, Item> getFurniture() {
        return furniture;
    }

    public Map<String, Character> getCharacters() {
        return characters;
    }

    @Override
    public String toString() {
        // todo: at some point you'll wanna clean up this string. (the thing says it should include names and descrpitons?)
        return "You are in " + getDescription() +
                ". You can see " + artifacts.values() +
                " " + furniture.values() + " " +
                characters.values() +
                " Locations accessible from here are: " + getAccessibleLocationNames();
    }
}
