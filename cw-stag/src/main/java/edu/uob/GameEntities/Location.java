package edu.uob.GameEntities;

import java.util.ArrayList;

public class Location extends GameEntity {
    public boolean isStartLocation;
    private ArrayList<Item> artifacts;
    private ArrayList<Item> furniture;
    private ArrayList<Character> characters;
    private ArrayList<Location> accessibleLocations;


    public Location(String name, String description, boolean isStartLocation,
                    ArrayList<Item> artifacts, ArrayList<Item> furniture, ArrayList<Character> characters) {
        super(name, description);
        this.isStartLocation = isStartLocation;
        this.artifacts = artifacts;
        this.furniture = furniture;
        this.characters = characters;
        this.accessibleLocations = new ArrayList<>();
    }

    public void addAccessibleLocation(Location location) {
        accessibleLocations.add(location);
    }

    public ArrayList<String> getAccessibleLocationNames() {
        ArrayList<String> listOfNames = new ArrayList<>();
        for (Location location : accessibleLocations) {
            listOfNames.add(location.getName());
        }
        return listOfNames;
    }

    @Override
    public String toString() {
        return "Location{" +
                " name=" + getName() +
                ", description=" + getDescription() +
                ", isStartLocation=" + isStartLocation +
                ", artifacts=" + artifacts +
                ", furniture=" + furniture +
                ", characters=" + characters +
                ", accessibleLocations=" + getAccessibleLocationNames() +
                '}';
    }
}
