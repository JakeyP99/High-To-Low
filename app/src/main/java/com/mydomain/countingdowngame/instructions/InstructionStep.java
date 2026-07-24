package com.mydomain.countingdowngame.instructions;

public class InstructionStep {
    private final String title;
    private final int descriptionResId;
    private final int imageResId;

    public InstructionStep(String title, int descriptionResId, int imageResId) {
        this.title = title;
        this.descriptionResId = descriptionResId;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public int getDescriptionResId() {
        return descriptionResId;
    }

    public int getImageResId() {
        return imageResId;
    }
}
