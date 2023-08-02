package com.example.countingdowngame.wildCards.wildCardTypes.quizWildCardCategories;

import com.example.countingdowngame.wildCards.WildCardProperties;

import java.util.List;

public class WildCardGroup {
    private String category;
    private List<WildCardProperties> wildCards;
    private boolean isExpanded;

    public WildCardGroup(String category, List<WildCardProperties> wildCards, boolean isExpanded) {
        this.category = category;
        this.wildCards = wildCards;
        this.isExpanded = isExpanded;
    }

    public String getCategory() {
        return category;
    }

    public List<WildCardProperties> getWildCards() {
        return wildCards;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
