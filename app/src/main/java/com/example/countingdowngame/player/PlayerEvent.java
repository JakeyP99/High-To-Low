package com.example.countingdowngame.player;

public class PlayerEvent {
    Player player;
    PlayerEventType type;
    public PlayerEvent(Player player, PlayerEventType type) {
        this.player = player;
        this.type = type;
    }
}
