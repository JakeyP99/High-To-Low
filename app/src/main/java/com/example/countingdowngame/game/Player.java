package com.example.countingdowngame.game;

import android.content.Context;

import com.example.countingdowngame.stores.GeneralSettingsLocalStore;

import java.io.Serializable;

public class Player implements Serializable {

    //-----------------------------------------------------Initialize---------------------------------------------------//

    private final String photo;
    private String name;
    private Game game;
    private int wildCardAmount;
    private int gainWildCardAmount;

    private boolean selected;

    //-----------------------------------------------------Set Game---------------------------------------------------//

    public void setGame(Game game) {
        this.game = game;
    }

    //-----------------------------------------------------Player---------------------------------------------------//
    public Player(Context context, String photo, String name) {
        this.photo = photo;
        this.name = name;
        this.selected = false;
        resetWildCardAmount(context);
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


    public void loseWildCards() {
        wildCardAmount = Math.max(wildCardAmount - 2, 0);
    }

}