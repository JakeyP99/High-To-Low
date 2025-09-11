package com.example.countingdowngame.statistics;

public class PlayerStatistic {
    private String playerName;
    private int totalDrinks;

    public PlayerStatistic(String playerName, int totalDrinks) {
        this.playerName = playerName;
        this.totalDrinks = totalDrinks;
    }

    public String getPlayerName() { return playerName; }
    public int getTotalDrinks() { return totalDrinks; }
}
