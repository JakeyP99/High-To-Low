package com.example.countingdowngame.player;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Player implements Serializable {

    //-----------------------------------------------------Initialize---------------------------------------------------//
    private final String id; // Unique identifier for the player
    private final String photo;
    private String name;
    private String classChoice;
    private Game game;
    private int wildCardAmount;
    private int usedWildcards;
    private boolean selected;
    private boolean usedActiveAbility;
    private boolean justUsedClassAbility;
    private int selectionOrder;
    private boolean usedWildCard;
    private boolean removed;
    private int passiveAbilityTurnCounter;
    private int activeAbilityTurnCounter;
    private List<Integer> numbersPlayed = new ArrayList<>();
    private int classAbilityCooldown;

    //-----------------------------------------------------Card Game---------------------------------------------------//

    private List<Integer> bulletsInChamberList; // List representing the chambers, 0 = blank, 1 = bullet
    private int chamberTotalNumberCount; // Index to track where in the chamber rotation they are

    private int chamberIndex;
    //-----------------------------------------------------Stats Setup---------------------------------------------------//
    private int drinksHandedOutByWitch;
    private int drinksTakenByWitch;
    private int correctQuizAnswers;
    private int incorrectQuizAnswers;

    //-----------------------------------------------------Set Game---------------------------------------------------//

    public Player(Context context, String id, String photo, String name, String classChoice) {
        this.id = id;
        this.photo = photo;
        this.name = name;
        this.classChoice = classChoice;
        this.selected = false;
        this.usedActiveAbility = false;
        this.justUsedClassAbility = false;
        this.usedWildCard = false;
        this.removed = false;
        resetWildCardAmount(context);
        this.passiveAbilityTurnCounter = 0;
        this.activeAbilityTurnCounter = 0;
        this.bulletsInChamberList = new ArrayList<>();
        this.chamberTotalNumberCount = 0;
        this.numbersPlayed = new ArrayList<>();
    }


    //-----------------------------------------------------Player---------------------------------------------------//
    public String getId() {
        return id;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public int getSelectionOrder() {
        return selectionOrder;
    }

    public void setSelectionOrder(int selectionOrder) {
        this.selectionOrder = selectionOrder;
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

    public int getRepeatingTurnsForPlayer() {
        if (Game.getInstance().getRepeatingTurnsForPlayer(this) == 0) {
            return 0;
        } else {
            return Game.getInstance().getRepeatingTurnsForPlayer(this);
        }
    }

    public int getPlayerTurnCount() {
        return getRepeatingTurnsForPlayer() + 1;
    }

    public int getActiveAbilityCooldown() {
        return classAbilityCooldown;
    }

    public void setActiveAbilityCooldown(int cooldown) {
        this.classAbilityCooldown = cooldown;
    }

    //-----------------------------------------------------Stats---------------------------------------------------//

    public int getCorrectQuizAnswers() {
        return correctQuizAnswers;
    }

    public void incrementCorrectQuizAnswers() {
        this.correctQuizAnswers++;
    }

    public int getIncorrectQuizAnswers() {
        return incorrectQuizAnswers;
    }

    public void incrementIncorrectQuizAnswers() {
        this.incorrectQuizAnswers++;
    }

    public int getDrinksHandedOutByWitch() {
        return drinksHandedOutByWitch;
    }

    public int getDrinksTakenByWitch() {
        return drinksTakenByWitch;
    }

    public void incrementDrinksHandedOutByWitch(int drinks) {
        this.drinksHandedOutByWitch += drinks;
    }

    public void incrementDrinksTakenByWitch(int drinks) {
        this.drinksTakenByWitch += drinks;
    }

    public void addNumberPlayed(int number) {
        if (numbersPlayed == null) {
            numbersPlayed = new ArrayList<>();
        }
        numbersPlayed.add(number);
    }

    //-----------------------------------------------------Global Stats---------------------------------------------------//

    public static List<String> getSavedPlayerNames(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("PlayerStats", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();
        List<String> playerNames = new ArrayList<>();

        for (String key : allEntries.keySet()) {
            if (key.endsWith("_drinks")) {
                String playerName = key.substring(0, key.length() - "_drinks".length());
                // Replace underscores with spaces (just in case)
                playerName = playerName.replace("_", " ");
                // Capitalize only the first letter
                if (!playerName.isEmpty()) {
                    playerName = Character.toUpperCase(playerName.charAt(0)) + playerName.substring(1);
                }
                playerNames.add(playerName);
            }
        }
        return playerNames;
    }


    //-----------------------------------------------------Passive Abilities---------------------------------------------------//

    public void incrementPassiveAbilityTurnCounter() {
        passiveAbilityTurnCounter++;
    }

    public void resetPassiveAbilityTurnCounter() {
        passiveAbilityTurnCounter = 0;
    }

    public int getPassiveAbilityTurnCounter() {
        return passiveAbilityTurnCounter;
    }

    //-----------------------------------------------------Active Abilities---------------------------------------------------//

    public void incrementActiveAbilityTurnCounter() {
        activeAbilityTurnCounter++;
    }

    public void resetActiveAbilityTurnCounter() {
        activeAbilityTurnCounter = 0;
    }

    public int getActiveAbilityTurnCounter() {
        return activeAbilityTurnCounter;
    }

    public boolean getUsedActiveAbility() {
        return usedActiveAbility;
    }

    public void setUsedActiveAbility(boolean usedActiveAbility) {
        this.usedActiveAbility = usedActiveAbility;
    }

    public boolean getJustUsedActiveAbility() {
        return justUsedClassAbility;
    }

    public void setJustUsedActiveAbility(boolean justUsedActiveAbility) {
        this.justUsedClassAbility = justUsedActiveAbility;
    }

    //-----------------------------------------------------Wildcards---------------------------------------------------//

    public boolean getJustUsedWildCard() {
        return this.usedWildCard;
    }

    public void setJustUsedWildCard(boolean used) {
        this.usedWildCard = used;
    }

    //-----------------------------------------------------Class Ability Specifics---------------------------------------------------//

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }


    //-----------------------------------------------------Chamber Setup---------------------------------------------------//

    // Get the player's chamber as a list of integers, where 1 is the bullet and 0 is a blank.
    public List<Integer> getBulletsInChamberList() {
        return bulletsInChamberList;
    }

    public void setChamberList() {
        if (bulletsInChamberList == null) {
            bulletsInChamberList = new ArrayList<>();
        }
        bulletsInChamberList.clear();

        // Proceed with adding blanks and bullets
        int numberOfBlanks = getTotalChamberNumberCount() - 1;
        int numberOfBullets = getTotalChamberNumberCount() - numberOfBlanks;

        for (int i = 0; i < numberOfBlanks; i++) {
            bulletsInChamberList.add(0);
        }
        for (int i = 0; i < numberOfBullets; i++) {
            bulletsInChamberList.add(1);
        }

        Collections.shuffle(bulletsInChamberList);
        Log.d("Player", "Chamber list set: " + bulletsInChamberList);
    }

    public List<Integer> getChamberList() {
        if (bulletsInChamberList == null) {
            bulletsInChamberList = new ArrayList<>();
        }
        return new ArrayList<>(bulletsInChamberList); // Return a copy to preserve encapsulation
    }


    public int getChamberIndex() {
        return chamberIndex;
    }

    // Set the current chamber index (this represents the chamber the player is about to fire)
    public void setChamberIndex(int chamberIndex) {
        this.chamberIndex = chamberIndex;
    }

    // Get the current chamber index (which chamber the player is at)
    public int getTotalChamberNumberCount() {
        return chamberTotalNumberCount;
    }

    // Set the current chamber index (this represents the chamber the player is about to fire)
    public void setTotalChamberNumberCount(int chamberTotalNumberCount) {
        this.chamberTotalNumberCount = chamberTotalNumberCount;
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

    public void removeWildCard(Player player, int numberOfWildCardsToLose) {
        player.loseWildCards(numberOfWildCardsToLose);
    }

    public int getUsedWildcards() {
        return usedWildcards;
    }

    public void incrementUsedWildcards() {
        this.usedWildcards++;
    }

    public void resetWildCardAmount(Context context) {
        wildCardAmount = GeneralSettingsLocalStore.fromContext(context).playerWildCardCount();
    }

    public void gainWildCards(int numberOfCardsToGain) {
        wildCardAmount += numberOfCardsToGain;
    }

    public void loseWildCards(int numberOfWildCardsToLose) {
        wildCardAmount = Math.max(wildCardAmount - numberOfWildCardsToLose, 0);
    }


    //-----------------------------------------------------Russian Roulette---------------------------------------------------//
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player player = (Player) obj;
        return Objects.equals(name, player.name);  // Compare based on name or another unique field
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);  // Use a unique identifier, like name
    }


}