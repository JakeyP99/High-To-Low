package com.example.countingdowngame.createPlayer;

public class CharacterClassStore {
    private final String className;
    private final String specialAbility;

    public CharacterClassStore(String className, String specialAbility) {
        this.className = className;
        this.specialAbility = specialAbility;
    }

    public String getClassName() {
        return className;
    }

    public String getSpecialAbility() {
        return specialAbility;
    }

}
