package com.example.countingdowngame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

public class PlayerSelection extends AppCompatActivity {

    private List<Player> playerList;
    private PlayerListAdapter playerListAdapter;
    private ListView playerListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item_player);

        playerListView = findViewById(R.id.playerListView);
        playerList = getPlayerList(); // Assuming you have a method to retrieve the player list

        playerListAdapter = new PlayerListAdapter(this, playerList);
        playerListView.setAdapter(playerListAdapter);

        playerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Player selectedPlayer = playerList.get(position);
                // Handle the selected player
                // For example, you can pass the selected player to the next activity
                Intent intent = new Intent(PlayerSelection.this, NumberChoice.class);
                intent.putExtra("selectedPlayer", (CharSequence) selectedPlayer);
                startActivity(intent);
            }
        });
    }

    private List<Player> getPlayerList() {
        SharedPreferences sharedPreferences = getSharedPreferences("PlayerData", Context.MODE_PRIVATE);
        String jsonPlayerList = sharedPreferences.getString("playerList", "");

        Gson gson = new Gson();
        Type type = new TypeToken<List<Player>>() {}.getType();
        return gson.fromJson(jsonPlayerList, type);
    }

}
