package com.example.countingdowngame.settings;

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

    public String getWildcardWrongAnswer(int index, String defValue) {
        return mPref.getString("wild_card_wronganswer1_" + index, defValue);
    }

    public String getWildcardWrongAnswer2(int index, String defValue) {
        return mPref.getString("wild_card_wronganswer2_" + index, defValue);
    }

    public String getWildcardWrongAnswer3(int index, String defValue) {
        return mPref.getString("wild_card_wronganswer3_" + index, defValue);
    }

    public String getWildcardCategory(int index) {
        return mPref.getString("wild_card_category_" + index, "");
    }

    public String getWildCardCategory(int index, String defValue) {
        return mPref.getString("wild_card_category_" + index, defValue);
    }

    public boolean getWildCardDeletable(int index) {
        return mPref.getBoolean("wild_card_deletable_" + index, true);
    }
    public boolean getWildCardDeletable(int index, boolean defValue) {
        return mPref.getBoolean("wild_card_deletable_" + index, defValue);
    }

    public int getWildcardProbability(int index) {
        return mPref.getInt("wild_card_probability_" + index, 0);
    }

    public void setWildcardState(int index, Boolean enabled, String activity, String answer, String wrongAnswer1, String wrongAnswer2, String wrongAnswer3, String category) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("wild_card_activity_" + index, activity);
        editor.putBoolean("wild_card_enabled_" + index, enabled);
        editor.putString("wild_card_answer_" + index, answer);
        editor.putString("wild_card_wronganswer1_" + index, wrongAnswer1);
        editor.putString("wild_card_wronganswer2_" + index, wrongAnswer2);
        editor.putString("wild_card_wronganswer3_" + index, wrongAnswer3);

        editor.putString("wild_card_category_" + index, category);
        editor.apply();

    }

    public void setWildcardState(int index, Boolean enabled, String activity) {
        {
            SharedPreferences.Editor editor = mPref.edit();
            editor.putString("wild_card_activity_" + index, activity);
            editor.putBoolean("wild_card_enabled_" + index, enabled);
            editor.apply();
        }
    }
}

