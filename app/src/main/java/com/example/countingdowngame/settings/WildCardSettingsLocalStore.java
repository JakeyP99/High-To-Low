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

    public Boolean isWildcardEnabled(int index, Boolean defValue) {
        return mPref.getBoolean("wild_card_enabled_" + index, defValue);
    }

    public String getWildcardActivityText(int index, String defValue) {
        return mPref.getString("wild_card_activity_" + index, defValue);
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

    public String getWildCardCategory(int index, String defValue) {
        return mPref.getString("wild_card_category_" + index, defValue);
    }


    public boolean getWildCardDeletable(int index, boolean defValue) {
        return mPref.getBoolean("wild_card_deletable_" + index, defValue);
    }

}

