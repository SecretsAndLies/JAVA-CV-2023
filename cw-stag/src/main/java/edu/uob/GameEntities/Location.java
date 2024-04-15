package edu.uob.GameEntities;

import java.util.ArrayList;

public class Location extends GameEntity{
    public boolean isStartLocation;
    private ArrayList<String> artifacts;
    private ArrayList<String> furniture;
    private ArrayList<String> characters;
    private ArrayList<String> accessibleLocations;


    public Location(String name, String description, boolean isStartLocation,
                    ArrayList<String> artifacts, ArrayList<String> furniture,
                    ArrayList<String> characters, ArrayList<String> accessibleLocations) {
        super(name, description);
        this.isStartLocation = isStartLocation;
        this.artifacts = artifacts;
        this.furniture = furniture;
        this.characters = characters;
        this.accessibleLocations = accessibleLocations;
    }

}
