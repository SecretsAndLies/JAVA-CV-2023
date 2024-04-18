package edu.uob.GameEntities;

import edu.uob.GameException;

import java.util.HashMap;

public class Player extends Character {


    Location location;
    private HashMap<String, Location> gameLocations;
    Location startLocation;
    HashMap<String, Item> inventory;
    int health;
    int START_HEALTH = 3;

    public Player(String name, String description, Location location, HashMap<String, Location> gameLocations) {
        super(name, description);
        this.location = location;
        this.startLocation=location;
        inventory = new HashMap<>();
        this.gameLocations=gameLocations;
        health=START_HEALTH;
    }

    // consume item from inventory or location.
    public void consumeItem(String itemName){
        if(itemName.equals("health")){
            this.reduceHealth();
            return;
        }
        // todo: confirm this approach is OK - ie does the lack of unique checks cause issues?
        inventory.remove(itemName);
        location.getFurniture().remove(itemName);
        location.getArtifacts().remove(itemName);
        location.removeAccessibleLocation(itemName);
    }

    public void reduceHealth(){
        health--;
        if(health==0){
            // drop all items in current location
            for(Item item : inventory.values()) {
                location.addItemToLocation(item);
            }
            // move player to start location.
            this.location=startLocation;
            health=START_HEALTH;
        }
    }


    public void increaseHealth(){
        if(health==3){
            return;
        }
        health++;
    }

    // Gets item from the storeroom and adds to the room.
    // can also get a location from the locations list and add it as an accessible location.
    public void produceItem(String itemName) throws GameException {
        // if item is a location
        if(itemName.equals("health")){
            increaseHealth();
            return;
        }
        Location location = getLocationByName(itemName);
        if(location!=null){
            this.location.addAccessibleLocation(location);
            return;
        }

        Location storeroom = getLocationByName("storeroom");
        if(storeroom==null){
            throw new GameException("Storeroom not found");
        }
        // search the storeroom for the item you need. Delete that item
        Item item = storeroom.takeItem(itemName);
        this.location.addItemToLocation(item);
    }

    private Location getLocationByName(String name){
        for(Location location : gameLocations.values()){
            if(location.getName().equals(name)){
                return location;
            }
        }
        return null;
    }

    // searches environment (including player inventory) for item returns true if found.
    public boolean environmentIncludesItemName(String item){
        if(inventory.containsKey(item)){
            return true;
        }
        if(location.getArtifacts().containsKey(item)){
            return true;
        }
        if(location.getFurniture().containsKey(item)){
            return true;
        }
        if(location.getCharacters().containsKey(item)){
            return true;
        }
        return false;
    }

    public String getHealthString(){
        return "your health is "+health;
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

    public HashMap<String, Item> getInventory() {
        return inventory;
    }

    public String gotoLocation(String locationName) {
        try {
            this.location = location.getConnectedLocation(locationName);
        } catch (GameException e) {
            return "Can't access that location from here.";
        }
        return location.toString();
    }

    public String dropItemInLocation(String itemName) {
        Item item = inventory.get(itemName);
        if (item == null) {
            return (itemName + " isn't in your inventory.");

        }
        location.addItemToLocation(item);
        inventory.remove(itemName);
        return item + " has been dropped on the ground. I guess you didn't want it?";
    }

    public String getInventoryString() {
        if (inventory.isEmpty()) {
            return "Your inventory is empty.";
        }
        // todo clean this up.
        return "Your inventory contains: " + inventory.toString();
    }

    public Location getLocation() {
        return location;
    }

}
