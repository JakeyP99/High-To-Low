package com.example.countingdowngame.wildCards;

public class WildCardProperties {
    private String activity;
    private final int probability;
    private boolean enabled;
    private final boolean deletable;
    private String answer;
    private final String category; // Add the category field

    public WildCardProperties(String activity, int probability, boolean enabled, boolean deletable, String answer, String category) {
        this.activity = activity;
        this.probability = probability;
        this.enabled = enabled;
        this.deletable = deletable;
        this.answer = answer;
        this.category = category;
    }

    public WildCardProperties(String activity, int probability, boolean enabled, boolean deletable) {
        this.activity = activity;
        this.probability = probability;
        this.enabled = enabled;
        this.deletable = deletable;
        this.answer = null; // Set answer to null for wildcards without an answer
        this.category = null;
    }
    public void setText(String text) {
        this.activity = text;
    }
    public String getText() {
        return activity;
    }

    public void setAnswer(String text) {
        this.answer = text;
    }

    public String getAnswer() {
        return answer;
    }

    public String getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getProbability() {
        return probability;
    }

    public boolean isDeletable() {
        return deletable;
    }


    public boolean hasAnswer() {
        return answer != null && !answer.isEmpty();
    }
}
