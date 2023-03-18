package com.example.countingdowngame;

public class GameEvent {
    Game game;
    GameEventType type;

    public GameEvent(Game game, GameEventType type) {
        this.game = game;
        this.type = type;
    }
}
