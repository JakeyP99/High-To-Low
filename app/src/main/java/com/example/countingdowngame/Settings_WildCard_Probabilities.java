package com.example.countingdowngame;

public class Settings_WildCard_Probabilities {
    private String activity;
    private int probability;
    private boolean enabled;
    private final boolean deletable;

    public Settings_WildCard_Probabilities(String activity, int probability, boolean enabled, boolean deletable) {
        this.activity = activity;
        this.probability = probability;
        this.enabled = enabled;
        this.deletable = deletable;
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

    public void setText(String text) {
        this.activity = text;
    }


    public int getProbability() {
        return probability;
    }

    public void setProbability(int probability) {
        this.probability = probability;
    }

    public boolean isDeletable() {
        return deletable;
    }
}
