package com.example.countingdowngame.game;

import android.content.Context;
import android.util.Log;

import com.example.countingdowngame.settings.GeneralSettingsLocalStore;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Player implements Serializable {

    //-----------------------------------------------------Initialize---------------------------------------------------//

    private final String photo;
    private String name;
    private String classChoice;
    private Game game;
    private int wildCardAmount;
    private int usedWildcards;
    private boolean selected;
    private boolean usedClassAbility;
    private boolean justUsedClassAbility;

    private boolean usedWildCard;
    private boolean removed;
    private int abilityTurnCounter; // Add a counter for the active turns of the Survivor class
    private int angryJimTurnCounter;
    private int drinksHandedOutByWitch;

    private int drinksTakenByWitch;


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
        this.justUsedClassAbility = false;
        this.usedWildCard = false;
        this.removed = false;
        resetWildCardAmount(context);
        this.abilityTurnCounter = 0;
    }

    //-----------------------------------------------------Stats---------------------------------------------------//

    public int getDrinksHandedOutByWitch() {
        return drinksHandedOutByWitch;
    }

    public int getDrinksTakenByWitch() {
        return drinksHandedOutByWitch;
    }

    public void incrementDrinksHandedOutByWitch(int drinks) {
        this.drinksHandedOutByWitch += drinks;
    }

    public void incrementDrinksTakenByWitch(int drinks) {
        this.drinksTakenByWitch += drinks;
    }


    //-----------------------------------------------------Survivor Ability---------------------------------------------------//

    public void incrementAbilityTurnCounter() {
        abilityTurnCounter++;
    }
    public void resetSpecificTurnCounter() {
        abilityTurnCounter = 0;
    }

    public int getAbilityTurnCounter() {
        return abilityTurnCounter;
    }

    //--------------------------------------------------------------------------------------------------------//

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public boolean getUsedClassAbility() {
        return usedClassAbility;
    }

    public void setUsedClassAbility(boolean classAbility) {
        this.usedClassAbility = classAbility;
    }

    public void setJustUsedClassAbility(boolean justUsedClassAbility) {
        this.justUsedClassAbility = justUsedClassAbility;
    }

    public boolean getJustUsedClassAbility() {
        return justUsedClassAbility;
    }

    public void setJustUsedWildCard(boolean used) {
        this.usedWildCard = used;
    }

    public boolean getJustUsedWildCard() {
        return this.usedWildCard;
    }


    public void setInRepeatingTurn() {
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


    public void incrementAngryJimTurnCounter() {
        angryJimTurnCounter++;
    }

    // Reset Angry Jim's turn counter
    public void resetAngryJimTurnCounter() {
        angryJimTurnCounter = 0;
    }

    // Get Angry Jim's turn counter
    public int getAngryJimTurnCounter() {
        return angryJimTurnCounter;
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

    public Player removeRandomWildCard() {
        List<Player> players = Game.getInstance().getPlayers();
        List<Player> otherPlayers = players.stream()
                .filter(p -> !p.equals(this) && p.getWildCardAmount() > 0)
                .collect(Collectors.toList());
        if (!otherPlayers.isEmpty()) {
            Player randomPlayer = otherPlayers.get(new Random().nextInt(otherPlayers.size()));
            randomPlayer.loseWildCards(1);
            Log.d("GoblinAbility", randomPlayer.getName() + " has lost a wildcard due to Goblin's ability.");
            return randomPlayer;
        } else {
            Log.d("GoblinAbility", "No other players have wildcards to lose.");
            return null;
        }
    }


    public int getUsedWildcards() {
        return usedWildcards;
    }

    public void incrementUsedWildcards() {
        this.usedWildcards++;
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