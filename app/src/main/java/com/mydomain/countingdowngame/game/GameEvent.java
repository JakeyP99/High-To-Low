package com.mydomain.countingdowngame.game;

public class GameEvent {
    public GameEventType type;
    Game game;

    public GameEvent(Game game, GameEventType type) {
        this.game = game;
        this.type = type;
    }
}
