package com.example.countingdowngame;

import android.content.Context;
import android.content.SharedPreferences;

public class GeneralSettingsLocalStore {
    private static final String SETTINGS_NAME = "general_settings";
    private final SharedPreferences mPref;

    private GeneralSettingsLocalStore(Context context) {
        mPref = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
    }

    public Boolean isMuted() {
        return mPref.getBoolean("isMuted", false);
    }

    public Boolean shouldPlayRegularSound() {
        return mPref.getBoolean("shouldPlayRegularSound", true);
    }
}
