package edu.uob.game_entities;

import edu.uob.GameException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player extends Character {

    private Location location;
    private final Map<String, Location> gameLocations;
    private final Location startLocation;
    private Map<String, Item> inventory;
    private int health;
    private static final int START_HEALTH = 3;

    public Player(String name, String description, Location location,
                  Map<String, Location> gameLocations) {
        super(name, description);
        this.location = location;
        this.startLocation = location;
        inventory = new HashMap<>();
        this.gameLocations = gameLocations;
        health = START_HEALTH;
    }

    public void consumeItem(String itemName) throws GameException {
        if ("health".equals(itemName)) {
            this.reduceHealth();
            return;
        }

        if (tryConsumeFromInventory(itemName)) return;
        if (tryConsumeCharacterFromLocation(itemName)) return;
        if (tryConsumeFromAllLocations(itemName)) return;

        // If no item or character matches, assume it's a location change
        location.removeAccessibleLocation(itemName);
    }

    private boolean tryConsumeFromInventory(String itemName) throws
            GameException {
        Item inventoryItem = inventory.remove(itemName);
        if (inventoryItem != null) {
            storeItem(inventoryItem);
            return true;
        }
        return false;
    }

    private boolean tryConsumeCharacterFromLocation(String itemName) throws
            GameException {
        Character locationCharacter = location.takeCharacterFromLocation(
                itemName);
        if (locationCharacter != null) {
            getStoreroom().addCharacterToLocation(locationCharacter);
            return true;
        }
        return false;
    }

    private boolean tryConsumeFromAllLocations(String itemName) throws
            GameException {
        for (Location gameLocation : getAllLocationsExceptStoreroom()) {
            if (tryRemoveAndStoreItem(gameLocation.getFurniture(), itemName))
                return true;
            if (tryRemoveAndStoreItem(gameLocation.getArtifacts(), itemName))
                return true;
        }
        return false;
    }

    private boolean tryRemoveAndStoreItem(Map<String, Item> items,
                                          String itemName) throws
            GameException {
        Item item = items.remove(itemName);
        if (item != null) {
            storeItem(item);
            return true;
        }
        return false;
    }

    private void storeItem(Item item) throws GameException {
        getStoreroom().addItemToLocation(item);
    }

    public void reduceHealth() throws GameException {
        health--;
        if (health == 0) {
            // todo: also provide custom error?
            // drop all items in current location
            for (Item item : inventory.values()) {
                location.addItemToLocation(item);
            }
            inventory = new HashMap<>();
            // remove the player from this location
            location.removeCharacterFromLocation(this);
            // move player to start location.
            this.location = startLocation;
            this.location.addCharacterToLocation(this);
            health = START_HEALTH;
            throw new GameException("You died.");
        }
    }


    public void increaseHealth() {
        if (health == 3) {
            return;
        }
        health++;
    }

    private Location getStoreroom() throws GameException {
        Location storeroom = getLocationByName("storeroom");
        if (storeroom == null) {
            throw new GameException("Storeroom not found");
        }
        return storeroom;
    }

    // Gets item from the storeroom and adds to the room.
    // can also get a location from the locations list and add it as an accessible location.
    public void produceItem(String itemName) {
        if ("health".equals(itemName)) {
            increaseHealth();
            return;
        }

        if (tryAddPathToLocation(itemName)) {
            return;
        }
        tryAddCharacterOrItemToCurrentLocation(itemName);
    }

    private boolean tryAddPathToLocation(String itemName) {
        Location location = getLocationByName(itemName);
        if (location != null) {
            this.location.addAccessibleLocation(location);
            return true;
        }
        return false;
    }

    private void tryAddCharacterOrItemToCurrentLocation(String itemName) {
        for (Location locationToTakeFrom : gameLocations.values()) {
            if (locationToTakeFrom.equals(this.getLocation())) {
                continue;
            }
            if (tryAddCharacterToLocation(itemName, locationToTakeFrom)) {
                return;
            }
            if (tryAddItemToLocation(itemName, locationToTakeFrom)) {
                return;
            }
        }
    }

    private boolean tryAddCharacterToLocation(String itemName,
                                              Location locationToTakeFrom) {
        Character character = locationToTakeFrom.takeCharacterFromLocation(
                itemName);
        if (character != null) {
            this.location.addCharacterToLocation(character);
            return true;
        }
        return false;
    }

    private boolean tryAddItemToLocation(String itemName,
                                         Location locationToTakeFrom) {
        Item item = locationToTakeFrom.takeItem(itemName);
        if (item != null) {
            this.location.addItemToLocation(item);
            return true;
        }
        return false;
    }

    private Location getLocationByName(String name) {
        for (Location location : gameLocations.values()) {
            if (location.getName().equals(name)) {
                return location;
            }
        }
        return null;
    }

    private List<Location> getAllLocationsExceptStoreroom() {
        List<Location> gameLocationsExceptStoreroom = new ArrayList<>();
        for (Location location : gameLocations.values()) {
            if ("storeroom".equals(location.getName())) {
                continue;
            }
            gameLocationsExceptStoreroom.add(location);
        }
        return gameLocationsExceptStoreroom;

    }

    // searches environment (including player inventory and current location) for item returns true if found.
    public boolean playerImmediateEnviromentContainsItem(String item) {
        return inventory.containsKey(item) || itemIsLocationOrContainsItem(
                this.location,
                item);
    }

    private boolean itemIsLocationOrContainsItem(Location location,
                                                 String item) {
        return location.getName().equals(item) ||
                location.getArtifacts().containsKey(item) ||
                location.getFurniture().containsKey(item) ||
                location.getCharacters().containsKey(item);
    }


    public boolean worldIncludesItemName(String item) {
        if (inventory.containsKey(item)) {
            return true;
        }
        for (Location location : gameLocations.values()) {
            if (itemIsLocationOrContainsItem(location, item)) {
                return true;
            }
        }
        return false;
    }


    public String getHealthString() {
        return "your health is " + health;
    }

    public String getItemFromCurrentLocation(String itemName) {
        Item item = location.removeItemFromLocation(itemName);
        if (item == null) {
            return "I can't pick up " + itemName;
        }
        inventory.put(itemName, item);
        return itemName + " added to your inventory.";
    }

    public String gotoLocation(String locationName) {
        Location oldLocation = this.location;
        try {
            this.location = location.getConnectedLocation(locationName);
        } catch (GameException e) {
            return "Can't access that location from here.";
        }
        oldLocation.removeCharacterFromLocation(this);
        this.location.addCharacterToLocation(this);
        return location.getDescriptionOfLocation(this);
    }

    public String dropItemInLocation(String itemName) {
        Item item = inventory.get(itemName);
        if (item == null) {
            return itemName + " isn't in your inventory.";

        }
        location.addItemToLocation(item);
        inventory.remove(itemName);
        return item + " has been dropped on the ground. I guess you didn't want it?";
    }

    public String getInventoryString() {
        if (inventory.isEmpty()) {
            return "Your inventory is empty.";
        }
        StringBuilder inventoryString = new StringBuilder();
        for (String item : inventory.keySet()) {
            inventoryString.append(item).append(", ");
        }
        return "Your inventory contains: " + inventoryString;
    }

    public Location getLocation() {
        return location;
    }

}
