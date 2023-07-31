package com.example.countingdowngame.wildCards;

public class WildCardHeadings {
    private String activity;
    private final int probability;
    private boolean enabled;
    private final boolean deletable;
    private final String answer;
    private final String category; // Add the category field

    public WildCardHeadings(String activity, int probability, boolean enabled, boolean deletable, String answer, String category) {
        this.activity = activity;
        this.probability = probability;
        this.enabled = enabled;
        this.deletable = deletable;
        this.answer = answer;
        this.category = category;
    }

    public WildCardHeadings(String activity, int probability, boolean enabled, boolean deletable) {
        this.activity = activity;
        this.probability = probability;
        this.enabled = enabled;
        this.deletable = deletable;
        this.answer = null; // Set answer to null for wildcards without an answer
        this.category = null;
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

    public boolean isDeletable() {
        return deletable;
    }

    public String getAnswer() {
        return answer;
    }

    public String getCategory() {
        return category;
    }
    public boolean hasAnswer() {
        return answer != null && !answer.isEmpty();
    }
}
