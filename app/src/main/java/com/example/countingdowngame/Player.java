package com.example.countingdowngame;

import android.graphics.Bitmap;

import java.util.HashSet;
import java.util.Set;

class Player {
    private Game game;
    private final Set<WildCardProbabilities> usedWildCards = new HashSet<>();
    int skips = 0;
    int wildcard = 1;
    int playerId;

    private String name;
    private Bitmap photo;

    public Player(Game game, int playerId) {
        this.game = game;
        this.playerId = playerId;
    }

    public Player(Bitmap photo, String name) {
        this.photo = photo;
        this.name = name;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void addUsedWildCard(WildCardProbabilities usedWildCard) {
        usedWildCards.add(usedWildCard);
    }

    public void resetAbilities() {
        this.skips = 0;
        this.wildcard = 1;
    }
}
