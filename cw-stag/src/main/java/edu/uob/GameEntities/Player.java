package edu.uob.GameEntities;

import edu.uob.GameException;

import java.util.HashMap;

public class Player extends Character {


    Location location;

    HashMap<String, Item> inventory;
    int health;

    public Player(String name, String description, Location location) {
        super(name, description);
        this.location = location;
        inventory = new HashMap<>();
        health=3;
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
        location.addArtifactToLocation(item);
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
