package com.example.countingdowngame.wildCards;

import java.util.Objects;

public class WildCardProperties {
    private final String answer;
    private final String category;
    private final String activity;
    private boolean enabled;
    private boolean usedWildCard;
    private String wrongAnswer1;
    private String wrongAnswer2;
    private String wrongAnswer3;

    public WildCardProperties(String activity, boolean enabled, boolean usedWildCard, String answer, String wrongAnswer1, String wrongAnswer2, String wrongAnswer3, String category) {
        this.activity = activity;
        this.enabled = enabled;
        this.usedWildCard = usedWildCard;
        this.answer = answer;
        this.wrongAnswer1 = wrongAnswer1;
        this.wrongAnswer2 = wrongAnswer2;
        this.wrongAnswer3 = wrongAnswer3;
        this.category = category;
    }

    public WildCardProperties(String activity, boolean enabled) {
        this.activity = activity;
        this.enabled = enabled;
        this.answer = null;
        this.category = null;
    }

    public String getWildCard() {
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

    public boolean isUsedWildCard() {
        return usedWildCard;
    }

    public boolean hasAnswer() {
        return answer != null && !answer.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        WildCardProperties that = (WildCardProperties) obj;
        return Objects.equals(activity, that.activity); // Compare unique identifier
    }

    @Override
    public int hashCode() {
        return Objects.hash(activity); // Use unique identifier for hashCode
    }

}
