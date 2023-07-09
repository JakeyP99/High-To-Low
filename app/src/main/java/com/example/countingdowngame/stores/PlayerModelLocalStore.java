package com.example.countingdowngame.stores;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.countingdowngame.game.Player;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PlayerModelLocalStore {
    public static PlayerModelLocalStore fromContext(Context context) {
        return new PlayerModelLocalStore(context);
    }

    private static final String SETTINGS_NAME = "playerModelSettings";
    private final SharedPreferences mPref;
    private final Context mContext;

    private PlayerModelLocalStore(Context context) {
        mContext = context;
        mPref = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
    }

    public String getPlayersJSON() {
        return mPref.getString("playersList", null);
    }

    public void setPlayersJSON(String value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("playersList", value);
        editor.apply();
    }

    public String getSelectedPlayersJSON() {
        return mPref.getString("selectedPlayersList", null);
    }

    public void setSelectedPlayersJSON(String value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("selectedPlayersList", value);
        editor.apply();
    }

    public void saveSelectedPlayers(List<Player> selectedPlayers) {
        Gson gson = new Gson();
        String json = gson.toJson(selectedPlayers);
        this.setSelectedPlayersJSON(json);
    }

    public List<Player> loadSelectedPlayers() {
        String json = this.getSelectedPlayersJSON();
        Type type = new TypeToken<ArrayList<Player>>() {
        }.getType();
        Gson gson = new Gson();
        List<Player> selectedPlayers = gson.fromJson(json, type);

        // Return an empty list if no data is loaded
        if (selectedPlayers == null) {
            selectedPlayers = new ArrayList<>();
        }

        return selectedPlayers;
    }

    public List<Player> loadPlayerData() {
        String json = this.getPlayersJSON();
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Player>>() {
        }.getType();
        List<Player> loadedPlayerList = gson.fromJson(json, type);

        // Set isSelected to false for all loaded players
        if (loadedPlayerList != null) {
            for (Player player : loadedPlayerList) {
                player.setSelected(false);
            }
        } else {
            loadedPlayerList = new ArrayList<>();
        }
        for (Player p : loadedPlayerList) {
            p.resetWildCardAmount(mContext);
        }
        return loadedPlayerList;
    }
}
