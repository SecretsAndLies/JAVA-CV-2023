package edu.uob.GameEntities;

public class Item extends GameEntity {

    private final boolean isCollectable;

    public Item(String name, String description, boolean isCollectable) {
        super(name, description);
        this.isCollectable = isCollectable;
    }

    public boolean isCollectable() {
        return isCollectable;
    }

}


