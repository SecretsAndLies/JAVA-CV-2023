package edu.uob.GameEntities;

public class Location extends GameEntity{
    // will contain a list of characters currently in location
    // Artifacts in location
    // furniture in location
    // list of locations accessible from here? (The spec says "paths to other locations.)
    boolean isStartLocation;
    public Location(String name, String description) {
        super(name, description);
    }
}
