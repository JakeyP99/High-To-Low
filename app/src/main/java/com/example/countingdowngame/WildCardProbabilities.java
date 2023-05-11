package com.example.countingdowngame;

public class WildCardProbabilities {
    private String activity;
    private int probability;
    private boolean used;
    private boolean enabled;

    public WildCardProbabilities(String activity, int probability, boolean enabled) {
        this.activity = activity;
        this.probability = probability;
        this.used = false;
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getText() {
        return activity;
    }


    public int getProbability() {
        return probability;
    }

    public void setText(String text) {
        this.activity = text;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }
}
