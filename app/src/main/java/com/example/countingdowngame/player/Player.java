package com.example.countingdowngame.player;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Player implements Serializable {

    //-----------------------------------------------------Initialize---------------------------------------------------//
    private final String id; // Unique identifier for the player
    private final String photo;
    private String name;
    private List<String> classChoices = new ArrayList<>();
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
    private Map<String, Integer> classCooldowns = new HashMap<>();
    private List<Integer> numbersPlayed = new ArrayList<>();
    private int classAbilityCooldown;
    private List<String> powerUps = new ArrayList<>();
    private boolean trollPassiveUsed = false;

    //-----------------------------------------------------Card Game---------------------------------------------------//

    private List<Integer> bulletsInChamberList; // List representing the chambers, 0 = blank, 1 = bullet
    private int chamberTotalNumberCount; // Index to track where in the chamber rotation they are

    private int chamberIndex;
    //-----------------------------------------------------Stats Setup---------------------------------------------------//
    private int drinksHandedOutByWitch;
    private int drinksTakenByWitch;
    private int correctQuizAnswers;
    private int incorrectQuizAnswers;
    private int drinksHandedOutByGambler;
    private int drinksTakenByGambler;

    //-----------------------------------------------------Set Game---------------------------------------------------//

    public Player(Context context, String id, String photo, String name, String classChoice) {
        this.id = id;
        this.photo = photo;
        this.name = name;
        if (classChoice != null) {
            ensureClassChoices().add(classChoice);
        }
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
        this.powerUps = new ArrayList<>();
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
        if (ensureClassChoices().isEmpty()) return "No Class";
        return classChoices.get(classChoices.size() - 1);
    }

    private List<String> ensureClassChoices() {
        if (classChoices == null) {
            classChoices = new ArrayList<>();
        }
        return classChoices;
    }

    public List<String> getClassChoices() {
        return ensureClassChoices();
    }

    public void setClassChoice(String classChoice) {
        ensureClassChoices().clear();
        if (classChoice != null) {
            this.classChoices.add(classChoice);
        }
    }

    public void addClassChoice(String classChoice) {
        if (classChoice != null) {
            if (!ensureClassChoices().contains(classChoice)) {
                classChoices.add(classChoice);
            }
        }
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

    public int getDrinksHandedOutByGambler() {
        return drinksHandedOutByGambler;
    }

    public void incrementDrinksHandedOutByGambler(int drinks) {
        this.drinksHandedOutByGambler += drinks;
    }

    public int getDrinksTakenByGambler() {
        return drinksTakenByGambler;
    }

    public void incrementDrinksTakenByGambler(int drinks) {
        this.drinksTakenByGambler += drinks;
    }

    public void addNumberPlayed(int number) {
        if (ensureNumbersPlayed() != null) {
            numbersPlayed.add(number);
        }
    }

    private List<Integer> ensureNumbersPlayed() {
        if (numbersPlayed == null) {
            numbersPlayed = new ArrayList<>();
        }
        return numbersPlayed;
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

    private Map<String, Integer> ensureClassCooldowns() {
        if (classCooldowns == null) {
            classCooldowns = new HashMap<>();
        }
        return classCooldowns;
    }

    public int getClassCooldown(String className) {
        return ensureClassCooldowns().getOrDefault(className, 0);
    }

    public void setClassCooldown(String className, int turns) {
        ensureClassCooldowns().put(className, turns);
    }

    public void decrementCooldowns() {
        for (Map.Entry<String, Integer> entry : ensureClassCooldowns().entrySet()) {
            if (entry.getValue() > 0) {
                entry.setValue(entry.getValue() - 1);
            }
        }
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

    public List<Integer> getBulletsInChamberList() {
        if (bulletsInChamberList == null) {
            bulletsInChamberList = new ArrayList<>();
        }
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

    //-----------------------------------------------------Powerups---------------------------------------------------//

    public List<String> getPowerUps() {
        if (powerUps == null) {
            powerUps = new ArrayList<>();
        }
        return powerUps;
    }

    public void usePowerUp(String powerUpName) {
        if (game != null) {
            game.triggerPlayerEvent(new PlayerEvent(this, PlayerEventType.POWER_UP));
        }
        getPowerUps().remove(powerUpName);
    }

    public boolean hasUsedTrollPassive() {
        return trollPassiveUsed;
    }

    public void setTrollPassiveUsed(boolean used) {
        this.trollPassiveUsed = used;
    }

}
