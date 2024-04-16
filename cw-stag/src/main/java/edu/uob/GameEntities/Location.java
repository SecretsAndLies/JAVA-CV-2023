package edu.uob.GameEntities;

import edu.uob.GameException;

import java.util.ArrayList;
import java.util.HashMap;

public class Location extends GameEntity {
    public boolean isStartLocation;
    private HashMap<String, Item> artifacts;
    private HashMap<String, Item> furniture;
    private HashMap<String, Character> characters;
    private HashMap<String, Location> accessibleLocations;


    public Location(String name, String description, boolean isStartLocation,
                    HashMap<String, Item> artifacts, HashMap<String, Item> furniture, HashMap<String, Character> characters) {
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

    public void addCharacterToLocation(Character character) {
        characters.put(character.getName(), character);
    }

    public void addArtifactToLocation(Item item) {
        artifacts.put(item.getName(), item);
    }

    public Item removeItemFromLocation(String itemName) {
        Item item = artifacts.get(itemName);
        artifacts.remove(itemName);
        return item;
    }

    public void addAccessibleLocation(Location location) {
        accessibleLocations.put(location.getName(), location);
    }

    public ArrayList<String> getAccessibleLocationNames() {
        return new ArrayList<>(accessibleLocations.keySet().stream().toList());
    }

    private String getCharactersString(HashMap<String, Character> characters) {
        if (characters.isEmpty()) {
            return "";
        }
        final String[] charactersString = {"You can see characters: "};
        characters.forEach((key, value) -> {
            charactersString[0] = charactersString[0] + " " + key + " " + value;
        });
        return charactersString[0];
    }

    public String getDescriptionOfLocation(Player currentPlayer) {
        HashMap<String, Character> characterCopy = new HashMap<>(characters);
        characterCopy.remove(currentPlayer.getName());
        return "You are in " + getDescription() +
                ". You can see " + artifacts.values() +
                " " + furniture.values() + " " +
                getCharactersString(characterCopy) +
                " Locations accessible from here are: " + getAccessibleLocationNames();
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
