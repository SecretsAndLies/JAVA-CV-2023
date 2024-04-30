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
    private final Map<String, Item> inventory;
    private int health;
    private static final int START_HEALTH = 3;

    public Player(String name, String description, Location location, Map<String, Location> gameLocations) {
        super(name, description);
        this.location = location;
        this.startLocation = location;
        inventory = new HashMap<>();
        this.gameLocations = gameLocations;
        health = START_HEALTH;
    }

    // consume item from inventory or location.
    public void consumeItem(String itemName) throws GameException {
        if ("health".equals(itemName)) {
            this.reduceHealth();
            return;
        }
        // todo repetive
        // remove item if it's in the inventory.
        Item inventoryItem = inventory.remove(itemName);
        if (inventoryItem != null) {
            getStoreroom().addItemToLocation(inventoryItem);
            return;
        }

        Character locationCharacter = location.takeCharacterFromLocation(itemName);
        if (locationCharacter != null) {
            getStoreroom().addCharacterToLocation(locationCharacter);
            return;
        }

        // remove item which can be in any location (not just the current one)
        for (Location gameLocation : getAllLocationsExceptStoreroom()) {
            Item furnitureItem = gameLocation.getFurniture().remove(itemName);
            if (furnitureItem != null) {
                getStoreroom().addItemToLocation(furnitureItem);
                return;
            }
            Item artifactItem = gameLocation.getArtifacts().remove(itemName);
            if (artifactItem != null) {
                getStoreroom().addItemToLocation(artifactItem);
                return;
            }
        }

        // if all else hasn't happened - this is a location so remove the path from the current location.
        location.removeAccessibleLocation(itemName);

    }

    public void reduceHealth() {
        health--;
        if (health == 0) {
            // todo: also provide custom error?
            // drop all items in current location
            for (Item item : inventory.values()) {
                location.addItemToLocation(item);
            }
            // remove the player from this location
            location.removeCharacterFromLocation(this);
            // move player to start location.
            this.location = startLocation;
            this.location.addCharacterToLocation(this);
            health = START_HEALTH;
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
    public void produceItem(String itemName) throws GameException {
        // if item is a location
        if ("health".equals(itemName)) {
            increaseHealth();
            return;
        }
        Location location = getLocationByName(itemName);
        if (location != null) {
            this.location.addAccessibleLocation(location);
            return;
        }

        for (Location locationFromList : gameLocations.values()) {
            if(locationFromList.equals(this.getLocation())){
                continue;
            }
            Character character = locationFromList.takeCharacterFromLocation(itemName);
            if (character != null) {
                this.location.addCharacterToLocation(character);
                return;
            }
            Item item = locationFromList.takeItem(itemName);
            if (item == null) {
                continue;
            }
            this.location.addItemToLocation(item);
        }
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

    // searches environment (including player inventory) for item returns true if found.
    public boolean environmentIncludesItemName(String item) {
        if (inventory.containsKey(item)) {
            return true;
        }
        if (location.getArtifacts().containsKey(item)) {
            return true;
        }
        if (location.getFurniture().containsKey(item)) {
            return true;
        }
        return location.getCharacters().containsKey(item);
    }

    public boolean worldIncludesItemName(String item) {
        if (inventory.containsKey(item)) {
            return true;
        }
        for(Location location : gameLocations.values()) {
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

    public String getHealthString() {
        return "your health is " + health;
    }

    public String getItemFromCurrentLocation(String itemName) {
        Item item = location.removeItemFromLocation(itemName);
        if (item == null) {
            // todo: ideally you'd distinguish between furnitue and items here. Maybe even combine into a single list?
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
        for(String item : inventory.keySet()){
            inventoryString.append(item).append(", ");
        }
        return "Your inventory contains: " + inventoryString.toString();
    }

    public Location getLocation() {
        return location;
    }

}
