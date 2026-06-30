package com.mydomain.countingdowngame.player;

public class PlayerEvent {
    public PlayerEventType type;
    Player player;

    public PlayerEvent(Player player, PlayerEventType type) {
        this.player = player;
        this.type = type;
    }
}
