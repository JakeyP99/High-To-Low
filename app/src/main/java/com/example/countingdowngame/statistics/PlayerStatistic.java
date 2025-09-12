package com.example.countingdowngame.statistics;

public class PlayerStatistic {
    private String playerName;
    private int totalDrinks;
    private int totalGamesLost;
    private int totalGamesPlayed;

    public PlayerStatistic(String playerName, int totalDrinks, int totalGamesLost, int totalGamesPlayed) {
        this.playerName = playerName;
        this.totalDrinks = totalDrinks;
        this.totalGamesLost = totalGamesLost;
        this.totalGamesPlayed = totalGamesPlayed;

    }

    public String getPlayerName() { return playerName; }
    public int getTotalDrinks() { return totalDrinks; }
    public int getTotalGamesLost() { return totalGamesLost; }
    public int getTotalGamesPlayed() { return totalGamesPlayed; }

}
