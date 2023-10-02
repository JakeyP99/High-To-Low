package com.example.countingdowngame.createPlayer;

public class CharacterClassStore {
    private String className;
    private String specialAbility;

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
