package com.example.countingdowngame;

class Player {


    int skips = 1;

    int playerId;
    Game game;


    public Player(Game game, int playerId) {

        this.game = game;
        this.playerId = playerId;

    }

    public String getName() {
        return Integer.toString(playerId + 1);
    }

    public void useSkip() {
        this.game.triggerPlayerEvent(new PlayerEvent(this, PlayerEventType.SKIP));
        this.skips = this.skips - 1;
    }

    public int getSkipAmount() {
        return this.skips;
    }


    public void resetAbilities() {
        this.skips = 1;
    }
}
