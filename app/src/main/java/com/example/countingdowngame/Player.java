package com.example.countingdowngame;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

class Player implements Serializable {

    //-----------------------------------------------------Initialize---------------------------------------------------//

    private final String photo;
    private String name;
    private Game game;
    private final Set<Settings_WildCard_Probabilities> usedWildCards = new HashSet<>();
    private int wildCardAmount = 0;
    private boolean selected;

    //-----------------------------------------------------Set Game---------------------------------------------------//

    public void setGame(Game game) {
        this.game = game;
    }

    //-----------------------------------------------------Player---------------------------------------------------//
    public Player(String photo, String name) {
        this.photo = photo;
        this.name = name;
        this.selected = false;
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

    public int getWildCardAmountFromSettings(Context context) {
        SharedPreferences wildcardPreferences = context.getSharedPreferences("wildcard_amount", Context.MODE_PRIVATE);
        wildCardAmount = wildcardPreferences.getInt("wildcardAmount", 0);
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

    public void addUsedWildCard(Settings_WildCard_Probabilities usedWildCard) {
        usedWildCards.add(usedWildCard);
    }
    //-----------------------------------------------------Reset Abilities---------------------------------------------------//

    public void resetAbilities() {
        this.wildCardAmount = wildCardAmount;
    }
}