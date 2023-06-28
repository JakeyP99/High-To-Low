package com.example.countingdowngame;

import android.content.Context;
import android.content.SharedPreferences;

public class GeneralSettingsLocalStore {
    public static GeneralSettingsLocalStore fromContext(Context context) {
        return new GeneralSettingsLocalStore(context);
    }

    private static final String SETTINGS_NAME = "generalSettings";
    private final SharedPreferences mPref;

    private GeneralSettingsLocalStore(Context context) {
        mPref = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
    }

    public Boolean isMuted() {
        return mPref.getBoolean("isMuted", false);
    }

    public void setIsMuted(Boolean value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean("isMuted", value);
        editor.apply();
    }

    public Boolean shouldPlayRegularSound() {
        return mPref.getBoolean("shouldPlayRegularSound", true);
    }

    public void setShouldPlayRegularSound(Boolean value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean("shouldPlayRegularSound", value);
        editor.apply();
    }

    public Boolean isSingleScreen() {
        return mPref.getBoolean("isSingleScreen", true);
    }

    public void setIsSingleScreen(Boolean value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean("isSingleScreen", value);
        editor.apply();
    }

    public int playerWildCardCount() {
        return mPref.getInt("playerWildCardCount", 1);
    }

    public void setPlayerWildCardCount(int value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt("playerWildCardCount", value);
        editor.apply();
    }
}
