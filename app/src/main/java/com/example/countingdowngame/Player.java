package com.example.countingdowngame;

import android.content.Intent;

class Player {


    int skips = 1;

    int playerId;
    Game game;


    public Player(Game game, int playerId) {

        this.game = game;
        this.playerId = playerId;

    }

    public String getName() {
        return new Integer(playerId + 1).toString();
    }

    public void useSkip() {
        this.game.triggerPlayerEvent(new PlayerEvent(this, PlayerEventType.SKIP));
        this.skips = this.skips - 1;
    }

    public void useChancecard() {
        this.game.triggerPlayerEvent(new PlayerEvent(this, PlayerEventType.CHANCE_CARD));
    }

    public int getSkipAmount() {
        return this.skips;
    }


    public void resetAbilities() {
        this.skips = 1;
    }
}
