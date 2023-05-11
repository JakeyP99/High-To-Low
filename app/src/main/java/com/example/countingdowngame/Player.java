package com.example.countingdowngame;

import java.util.HashSet;
import java.util.Set;

class Player {
    int skips = 0;
    int wildcard = 1;
    int playerId;
    Game game;

    public Player(Game game, int playerId) {

        this.game = game;
        this.playerId = playerId;

    }


    public void useSkip() {
        this.game.triggerPlayerEvent(new PlayerEvent(this, PlayerEventType.SKIP));
        this.skips = this.skips - 1;
    }

    private Set<WildCardProbabilities> usedWildCards = new HashSet<>();


    public void useWildCard() {
        this.game.triggerPlayerEvent(new PlayerEvent(this, PlayerEventType.WILD_CARD));
        this.wildcard = this.wildcard - 1;
    }

    public int getSkipAmount() {
        return this.skips;
    }

    public int getWildCardAmount() {
        return this.wildcard;
    }

    public void resetAbilities() {
        this.skips = 0;
        this.wildcard = 1;
    }

}
