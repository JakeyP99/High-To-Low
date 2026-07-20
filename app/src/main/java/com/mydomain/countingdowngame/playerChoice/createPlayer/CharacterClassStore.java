package com.mydomain.countingdowngame.playerChoice.createPlayer;

public class CharacterClassStore {
    private final String className;
    private final String activeAbility;
    private final String passiveAbility;
    private final String shortActive;
    private final String shortPassive;
    private final String quote;
    private final int cooldown;

    private final int id; // Unique identifier for each item
    private final int imageResource; // Image resource ID for the character class


    public CharacterClassStore(int id, String className, String activeAbility, String shortActive, String passiveAbility, String shortPassive, String quote, int cooldown, int imageResource) {
        this.id = id;
        this.className = className;
        this.activeAbility = activeAbility;
        this.shortActive = shortActive;
        this.passiveAbility = passiveAbility;
        this.shortPassive = shortPassive;
        this.quote = quote;
        this.cooldown = cooldown;
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

    public String getShortActive() {
        return shortActive;
    }

    public String getCharacterPassiveDescriptions() {
        return passiveAbility;
    }

    public String getShortPassive() {
        return shortPassive;
    }

    public String getQuote() {
        return quote;
    }

    public int getCooldown() {
        return cooldown;
    }
}
