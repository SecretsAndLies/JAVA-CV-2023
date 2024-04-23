package edu.uob.game_entities;

public abstract class GameEntity {
    private final String name;
    private final String description;

    public GameEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return name + " : " + description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
