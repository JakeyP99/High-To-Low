package com.example.countingdowngame;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

class Player implements Serializable {
    private String photo;
    private String name;
    private Game game;
    private final Set<Settings_WildCard_Probabilities> usedWildCards = new HashSet<>();
    int skips = 0;
    int wildcard = 1;

    private String photoFilePath; // Store the file path instead of Bitmap
    private String photoUrl; // Store the photo URL as a string
    private boolean selected;


    public Player(String photo, String name) {
        this.photo = photo;
        this.name = name;
        this.selected = false;
    }

    // Getter and setter methods

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getPhoto() {
        return photo;
    }
    public int getSkipAmount() {
        return this.skips;
    }

    public int getWildCardAmount() {
        return this.wildcard;
    }

    public void useWildCard() {
        this.game.triggerPlayerEvent(new PlayerEvent(this, PlayerEventType.WILD_CARD));
        this.wildcard = this.wildcard - 1;
    }

    public void useSkip() {
        this.game.triggerPlayerEvent(new PlayerEvent(this, PlayerEventType.SKIP));
        this.skips = this.skips - 1;
    }

    public void addUsedWildCard(Settings_WildCard_Probabilities usedWildCard) {
        usedWildCards.add(usedWildCard);
    }

    public void resetAbilities() {
        this.skips = 0;
        this.wildcard = 1;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
