package com.example.countingdowngame.createPlayer;

public class CharacterClassStore {
    private final String className;
    private final String specialAbility;
    private final int id; // Unique identifier for each item

    public CharacterClassStore(int id, String className, String specialAbility) {
        this.id = id;
        this.className = className;
        this.specialAbility = specialAbility;
    }

    public int getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public String getCharacterClassDescriptions() {
        return specialAbility;
    }
}