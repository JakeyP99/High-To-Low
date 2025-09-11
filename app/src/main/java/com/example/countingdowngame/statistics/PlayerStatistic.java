package com.example.countingdowngame.statistics;

public class PlayerStatistic {
    private String playerName;
    private int totalDrinks;
    private int totalGamesLost;

    public PlayerStatistic(String playerName, int totalDrinks, int totalGamesLost) {
        this.playerName = playerName;
        this.totalDrinks = totalDrinks;
        this.totalGamesLost = totalGamesLost;

    }

    public String getPlayerName() { return playerName; }
    public int getTotalDrinks() { return totalDrinks; }
    public int getTotalGamesLost() { return totalGamesLost; }

}
