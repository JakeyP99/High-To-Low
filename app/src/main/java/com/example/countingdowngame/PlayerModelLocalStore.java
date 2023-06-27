package com.example.countingdowngame;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PlayerModelLocalStore {
    private static final String SETTINGS_NAME = "playermodel_settings";
    private final SharedPreferences mPref;

    private PlayerModelLocalStore(Context context) {
        mPref = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
    }

    public String playerModelSelected() {
        return mPref.getString("selected_players", null);
    }

    public List<Player> playerModelData() {
        return mPref.getString("player_list", null);
    }
}