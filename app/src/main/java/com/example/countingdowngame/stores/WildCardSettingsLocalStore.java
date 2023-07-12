package com.example.countingdowngame.stores;

import android.content.Context;
import android.content.SharedPreferences;

public class WildCardSettingsLocalStore {

    public static WildCardSettingsLocalStore fromContext(Context context, String saveKey) {
        return new WildCardSettingsLocalStore(context, saveKey);
    }

    private final SharedPreferences mPref;

    private WildCardSettingsLocalStore(Context context, String saveKey) {
        mPref = context.getSharedPreferences(saveKey, Context.MODE_PRIVATE);
    }

    public int getWildCardQuantity() {
        return mPref.getInt("wildcardAmount", 0);
    }

    public void setWildCardQuantity(int value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt("wildcardAmount", value);
        editor.apply();
    }

    public Boolean isWildcardEnabled(int index) {
        return mPref.getBoolean("wild_card_enabled_" + index, false);
    }

    public Boolean isWildcardEnabled(int index, Boolean defValue) {
        return mPref.getBoolean("wild_card_enabled_" + index, defValue);
    }

    public String getWildcardActivityText(int index) {
        return mPref.getString("wild_card_activity_" + index, "");
    }

    public String getWildcardActivityText(int index, String defValue) {
        return mPref.getString("wild_card_activity_" + index, defValue);
    }

    public String getWildcardAnswer(int index) {
        return mPref.getString("wild_card_answer_" + index, "");
    }
    public String getWildcardAnswer(int index, String defValue) {
        return mPref.getString("wild_card_answer_" + index, defValue);
    }

    public int getWildcardProbability(int index) {
        return mPref.getInt("wild_card_probability_" + index, 0);
    }

    public int getWildcardProbability(int index, int defValue) {
        return mPref.getInt("wild_card_probability_" + index, defValue);
    }

    public void setWildcardState(int index, Boolean enabled, String activity, int probability, String answer) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean("wild_card_enabled_" + index, enabled);
        editor.putString("wild_card_activity_" + index, activity);
        editor.putInt("wild_card_probability_" + index, probability);
        editor.putString("wild_card_answer_" + index, answer);

        editor.apply();
    }
}

