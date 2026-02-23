package com.example.countingdowngame.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class GeneralSettingsLocalStore {
    private final SharedPreferences mPref;

    private GeneralSettingsLocalStore(Context context) {
        mPref = context.getSharedPreferences("generalSettings", Context.MODE_PRIVATE);
    }

    public static GeneralSettingsLocalStore fromContext(Context context) {
        return new GeneralSettingsLocalStore(context);
    }

    private void setBooleanPreference(String key, boolean value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private boolean getBooleanPreference(String key) {
        return mPref.getBoolean(key, true);
    }

    private void setIntPreference(String key, int value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }


    private int getIntPreference(String key, int defaultValue) {
        return mPref.getInt(key, defaultValue);
    }

    public void setIsQuizActivated(boolean value) {
        setBooleanPreference("isQuizActive", value);
    }

    public boolean isQuizActivated() {
        return getBooleanPreference("isQuizActive");
    }

    public void setIsTaskActivated(boolean value) {
        setBooleanPreference("isTaskActive", value);
    }

    public boolean isTaskActivated() {
        return getBooleanPreference("isTaskActive");
    }

    public void setIsTruthActivated(boolean value) {
        setBooleanPreference("isTruthActive", value);
    }

    public boolean isTruthActivated() {
        return getBooleanPreference("isTruthActive");
    }

    public boolean shouldPlayRegularSound() {
        return mPref.getBoolean("shouldPlayRegularSound", true);
    }

    public void setShouldPlayRegularSound(boolean value) {
        setBooleanPreference("shouldPlayRegularSound", value);
    }

    public boolean isMultiChoice() {
        return mPref.getBoolean("isMultiChoice", true);
    }

    public void setIsMultiChoice(boolean value) {
        setBooleanPreference("isMultiChoice", value);
    }

    public int playerWildCardCount() {
        return getIntPreference("playerWildCardCount", 1);
    }

    public void setPlayerWildCardCount(int value) {
        setIntPreference("playerWildCardCount", value);
    }

    public int totalDrinkAmount() {
        return getIntPreference("totalDrinkAmount", 5);
    }

    public void setTotalDrinkAmount(int value) {
        setIntPreference("totalDrinkAmount", value);
    }

    public void setChamberCount(int value) {
        setIntPreference("playerChamberCount", value);
    }




}
