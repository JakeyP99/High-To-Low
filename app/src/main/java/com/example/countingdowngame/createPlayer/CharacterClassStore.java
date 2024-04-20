package com.example.countingdowngame.createPlayer;

public class CharacterClassStore {
    private final String className;
    private final String activeAbility;
    private final String passiveAbility;

    private final int id; // Unique identifier for each item
    private final int imageResource; // Image resource ID for the character class


    public CharacterClassStore(int id, String className, String activeAbility, String passiveAbility, int imageResource) {
        this.id = id;
        this.className = className;
        this.activeAbility = activeAbility;
        this.passiveAbility = passiveAbility;
        this.imageResource = imageResource;
    }

    public int getId() {
        return id;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getClassName() {
        return className;
    }

    public String getCharacterActiveDescriptions() {
        return activeAbility;
    }

    public String getCharacterPassiveDescriptions() {
        return passiveAbility;
    }

}
