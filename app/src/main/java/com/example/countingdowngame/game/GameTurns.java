package com.example.countingdowngame.game;

public class GameTurns {
    private final String playerName;
    private final int number;

    public GameTurns(String playerName, int number) {
        this.playerName = playerName;
        this.number = number;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getNumber() {
        return number;
    }
}
