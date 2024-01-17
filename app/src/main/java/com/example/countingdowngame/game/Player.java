package com.example.countingdowngame.game;

import android.content.Context;

import com.example.countingdowngame.settings.GeneralSettingsLocalStore;

import java.io.Serializable;

public class Player implements Serializable {

    //-----------------------------------------------------Initialize---------------------------------------------------//

    private final String photo;
    private String name;
    private String classChoice;
    private Game game;
    private int wildCardAmount;
    private boolean selected;
    private int turnCounter;
    private boolean usedClassAbility;

    private boolean usedWildCard;

    private boolean removed;
    private int survivorActiveTurnCounter; // Add a counter for the active turns of the Survivor class


    //-----------------------------------------------------Set Game---------------------------------------------------//

    public void setGame(Game game) {
        this.game = game;
    }

    //-----------------------------------------------------Player---------------------------------------------------//
    public Player(Context context, String photo, String name, String classChoice) {
        this.photo = photo;
        this.name = name;
        this.classChoice = classChoice;
        this.selected = false;
        this.usedClassAbility = false;
        this.usedWildCard = false;
        this.removed = false;
        resetWildCardAmount(context);
        this.turnCounter = 0; // Initialize the turn counter to 0
        this.survivorActiveTurnCounter = 0; // Initialize the Survivor class active turn counter to 0
    }

    //-----------------------------------------------------Survivor Ability---------------------------------------------------//

    public void incrementSurvivorActiveTurnCounter() {
        survivorActiveTurnCounter++;
    }

    public void resetSurvivorActiveTurnCounter() {
        survivorActiveTurnCounter = 0;
    }

    public int getSurvivorActiveTurnCounter() {
        return survivorActiveTurnCounter;
    }


    //--------------------------------------------------------------------------------------------------------//

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public boolean usedClassAbility() {
        return usedClassAbility;
    }

    public void setJustUsedWildCard(boolean used) {
        this.usedWildCard = used;
    }

    public boolean getJustUsedWildCard() {
        return this.usedWildCard;
    }

    public void setClassAbility(boolean classAbility) {
        this.usedClassAbility = classAbility;
    }

    public void setInRepeatingTurn() {
    }

    public int getTurnCounter() {
        return turnCounter;
    }

    public String getClassChoice() {
        return classChoice;
    }

    public void setClassChoice(String classChoice) {
        this.classChoice = classChoice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    //-----------------------------------------------------Wild Card/Skip---------------------------------------------------//

    public int getWildCardAmount() {
        return wildCardAmount;
    }

    public void useWildCard() {
        if (game != null) {
            game.triggerPlayerEvent(new PlayerEvent(this, PlayerEventType.WILD_CARD));
        }
        wildCardAmount--; // Decrease the wildcard amount
    }

    public void useSkip() {
        this.game.triggerPlayerEvent(new PlayerEvent(this, PlayerEventType.SKIP));
    }


    //-----------------------------------------------------Reset Abilities---------------------------------------------------//

    public void resetWildCardAmount(Context context) {
        wildCardAmount = GeneralSettingsLocalStore.fromContext(context).playerWildCardCount();
    }

    public void gainWildCards(int numberOfCardsToGain) {
        wildCardAmount += numberOfCardsToGain;
    }


    public void loseWildCards(int numberOfWildCardsToLose) {
        wildCardAmount = Math.max(wildCardAmount - numberOfWildCardsToLose, 0);
    }


}