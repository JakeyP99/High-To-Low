package com.example.countingdowngame.wildCards;

public class WildCardProperties {
    private String activity;
    private final int probability;
    private boolean enabled;
    private final boolean deletable;
    private final String answer;
    private String wrongAnswer1;
    private String wrongAnswer2;
    private String wrongAnswer3;
    private final String category;

    public WildCardProperties(String activity, int probability, boolean enabled, boolean deletable, String answer, String wrongAnswer1, String wrongAnswer2, String wrongAnswer3, String category) {
        this.activity = activity;
        this.probability = probability;
        this.enabled = enabled;
        this.deletable = deletable;
        this.answer = answer;
        this.wrongAnswer1 = wrongAnswer1;
        this.wrongAnswer2 = wrongAnswer2;
        this.wrongAnswer3 = wrongAnswer3;
        this.category = category;
    }

    public WildCardProperties(String activity, int probability, boolean enabled, boolean deletable) {
        this.activity = activity;
        this.probability = probability;
        this.enabled = enabled;
        this.deletable = deletable;
        this.answer = null;
        this.category = null;
    }

    public void setText(String text) {
        this.activity = text;
    }

    public String getText() {
        return activity;
    }

    public String getAnswer() {
        return answer;
    }

    public String getWrongAnswer1() {
        return wrongAnswer1;
    }

    public String getWrongAnswer2() {
        return wrongAnswer2;
    }

    public String getWrongAnswer3() {
        return wrongAnswer3;
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
