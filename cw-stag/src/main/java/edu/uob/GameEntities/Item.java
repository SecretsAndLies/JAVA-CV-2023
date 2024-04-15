package edu.uob.GameEntities;

public class Item extends GameEntity{

    // this is an Artifact or a Furniture, defined by if it's collectable or not.
    public Item(String name, String description) {
        super(name, description);
    }
}
