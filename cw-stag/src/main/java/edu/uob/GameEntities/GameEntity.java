package edu.uob.GameEntities;

public abstract class GameEntity {
    private String name;
    private String description;

    public GameEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
