package com.example.countingdowngame.game;

public class GameEvent {
    Game game;
    public GameEventType type;
    public GameEvent(Game game, GameEventType type) {
        this.game = game;
        this.type = type;
    }
}
