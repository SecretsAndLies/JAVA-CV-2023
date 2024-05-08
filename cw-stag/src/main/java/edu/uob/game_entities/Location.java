package edu.uob.game_entities;

import edu.uob.GameException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Location extends GameEntity {
    private final Map<String, Item> artifacts;
    private final Map<String, Item> furniture;
    private final Map<String, Character> characters;
    private final Map<String, Location> accessibleLocations;


    public Location(String name, String description,
                    Map<String, Item> artifacts, Map<String, Item> furniture,
                    Map<String, Character> characters) {
        super(name, description);
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


    public void removeCharacterFromLocation(Character character) {
        characters.remove(character.getName());
    }


    private String getCharactersString(Player currentPlayer) {
        Map<String, Character> characterCopy = new HashMap<>(characters);
        characterCopy.remove(currentPlayer.getName());
        return formatCollectionDescription(characterCopy.values(),
                "\nYou can see characters:");
    }


    private String getArtifactsString() {
        return formatCollectionDescription(artifacts.values(),
                "\nYou can see the following objects:");
    }

    private String getFurnitureString() {
        return formatCollectionDescription(furniture.values(),
                "\nYou can see the following furniture:");
    }


    private String getAccessibleLocationsString() {
        return formatCollectionDescription(
                accessibleLocations.values().stream().map(Location::getName)
                        .collect(Collectors.toList()),
                "\nLocations accessible from here are:");
    }

    private String formatCollectionDescription(Collection<?> items,
                                               String header) {
        if (items.isEmpty()) {
            return "";
        }
        return items.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n      ", header + "\n      ",
                        ""));
    }


    public String getDescriptionOfLocation(Player currentPlayer) {
        return "You are in " + this.getDescription()
                + getArtifactsString() +
                getFurnitureString() +
                getCharactersString(currentPlayer) +
                getAccessibleLocationsString();
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
}
