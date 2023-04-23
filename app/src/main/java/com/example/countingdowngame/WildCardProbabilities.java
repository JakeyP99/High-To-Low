package com.example.countingdowngame;

public class WildCardProbabilities {
    private String activity;
    private int probability;
    private boolean used;

    public WildCardProbabilities(String activity, int probability) {
        this.activity = activity;
        this.probability = probability;
        this.used = false;
    }

    public String getActivity() {
        return activity;
    }

    public int getProbability() {
        return probability;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
