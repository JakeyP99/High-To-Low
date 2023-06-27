package com.example.countingdowngame;

import android.content.Context;
import android.content.SharedPreferences;

public class WildCardSettingsLocalStore {
    private static final String SETTINGS_NAME = "wildcard_settings";
    private final SharedPreferences mPref;

    private WildCardSettingsLocalStore(Context context) {
        mPref = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
    }

    public int wildCardQuantity() {
        return mPref.getInt("wildcardAmount", 1);
    }

    public int truthWildCard() {
        return mPref.getInt("TruthPrefs", 1);
    }



    }


