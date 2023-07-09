package com.example.countingdowngame.stores;

import android.content.Context;
import android.content.SharedPreferences;

public class PlayerModelLocalStore {
    private static final String SETTINGS_NAME = "playermodel_settings";
    private final SharedPreferences mPref;

    private PlayerModelLocalStore(Context context) {
        mPref = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
    }

    public String playerModelSelected() {
        return mPref.getString("selected_players", null);
    }

    public void setPlayerModelSelected(Boolean value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean("wildcardAmount", value);
        editor.apply();
    }
}

