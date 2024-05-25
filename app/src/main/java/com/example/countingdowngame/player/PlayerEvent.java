package com.example.countingdowngame.player;

public class PlayerEvent {
    Player player;
    public PlayerEventType type;
    public PlayerEvent(Player player, PlayerEventType type) {
        this.player = player;
        this.type = type;
    }
}
