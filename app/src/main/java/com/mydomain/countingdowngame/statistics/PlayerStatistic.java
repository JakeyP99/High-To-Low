package com.mydomain.countingdowngame.statistics;

import android.graphics.Bitmap;

public class PlayerStatistic {
    private final String playerName;
    private final int totalDrinks;
    private final int totalGamesLost;
    private final int totalGamesPlayed;
    private final Bitmap playerPhoto;

    public PlayerStatistic(String playerName, int totalDrinks, int totalGamesLost, int totalGamesPlayed, Bitmap playerPhoto) {
        this.playerName = playerName;
        this.totalDrinks = totalDrinks;
        this.totalGamesLost = totalGamesLost;
        this.totalGamesPlayed = totalGamesPlayed;
        this.playerPhoto = playerPhoto;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getTotalDrinks() {
        return totalDrinks;
    }

    public int getTotalGamesLost() {
        return totalGamesLost;
    }

    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public Bitmap getPlayerPhoto() {
        return playerPhoto;
    }
}
