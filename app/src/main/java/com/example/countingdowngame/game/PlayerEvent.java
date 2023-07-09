package com.example.countingdowngame.game;

public class PlayerEvent {
    Player player;
    PlayerEventType type;
    public PlayerEvent(Player player, PlayerEventType type) {
        this.player = player;
        this.type = type;
    }
}
