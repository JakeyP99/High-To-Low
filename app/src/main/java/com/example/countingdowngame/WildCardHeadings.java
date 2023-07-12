package com.example.countingdowngame;

public class WildCardHeadings {
    private String activity;
    private int probability;
    private boolean enabled;
    private boolean deletable;
    private String answer;

    public WildCardHeadings(String activity, int probability, boolean enabled, boolean deletable, String answer) {
        this.activity = activity;
        this.probability = probability;
        this.enabled = enabled;
        this.deletable = deletable;
        this.answer = answer;
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean hasAnswer() {
        return answer != null && !answer.isEmpty();
    }
}

