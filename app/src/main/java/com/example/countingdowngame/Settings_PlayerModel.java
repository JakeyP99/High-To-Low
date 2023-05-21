package com.example.countingdowngame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Settings_PlayerModel extends ButtonUtilsActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private List<Player> playerList;
    private PlayerListAdapter playerListAdapter;
    private TextView playerCountTextView;
    private int totalPlayerCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a3_player_choice);

        // Initialize views and adapters
        Button proceedButton = findViewById(R.id.button_done);

        RecyclerView playerRecyclerView = findViewById(R.id.playerRecyclerView);
        playerList = new ArrayList<>();
        playerListAdapter = new PlayerListAdapter(this, playerList, 3);
        loadPlayerData();

        int selectedPlayerCount = Game.getInstance().getPlayerAmount();
        playerList = new ArrayList<>();
        playerListAdapter = new PlayerListAdapter(this, playerList, selectedPlayerCount);

        // Set up grid layout
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        playerRecyclerView.setLayoutManager(layoutManager);

        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        playerRecyclerView.addItemDecoration(new SpaceItemDecoration(spacing));
        playerRecyclerView.setAdapter(playerListAdapter);

        // Choose image button click listener
        Button chooseImageButton = findViewById(R.id.chooseImageButton);
        chooseImageButton.setOnClickListener(view -> captureImage());

        playerCountTextView = findViewById(R.id.text_view_counter);
        totalPlayerCount = Game.getInstance().getPlayerAmount();


        // Call updatePlayerCounter() to display the counter text initially
        updatePlayerCounter();

        btnUtils.setButton(proceedButton, () -> {
            int remainingPlayers = totalPlayerCount - selectedPlayerCount;
            if (remainingPlayers == 0) {
                String counterText;
                counterText = "All Players Selected: " + totalPlayerCount;
                playerCountTextView.setText(counterText);
                startActivity(getIntentForClass(MainActivity.class, true));
            }
            {
                Toast.makeText(this, "Select more players", Toast.LENGTH_SHORT);
            }
        });
    }

    // Open image picker to capture an image
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    // Add a new player with the selected image and name
    private void addNewPlayer(Bitmap bitmap, String name) {
        String photoString = convertBitmapToString(bitmap);
        Player newPlayer = new Player(photoString, name);
        playerList.add(newPlayer);
        playerListAdapter.notifyItemInserted(playerList.size() - 1);
        savePlayerData();
        updatePlayerCounter();
    }

    // Delete a player at a given position
    public void deletePlayer(int position) {
        playerList.remove(position);
        playerListAdapter.notifyItemRemoved(position);
        savePlayerData();
        updatePlayerCounter();
    }

    // RecyclerView item decoration for spacing
    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int spacing;

        public SpaceItemDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.left = spacing;
            outRect.right = spacing;
            outRect.bottom = spacing;

            GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
            assert layoutManager != null;
            if (parent.getChildAdapterPosition(view) < layoutManager.getSpanCount()) {
                outRect.top = spacing;
            } else {
                outRect.top = 0;
            }
        }
    }

    public void updatePlayerCounter() {
        int selectedPlayerCount = 0;
        List<Player> selectedPlayers = new ArrayList<>();

        for (Player player : playerList) {
            if (player.isSelected()) {
                selectedPlayerCount++;
                selectedPlayers.add(player);
            }
        }

        int remainingPlayers = totalPlayerCount - selectedPlayerCount;
        if (remainingPlayers == 0) {
            String counterText = "All Players Selected: " + totalPlayerCount;
            playerCountTextView.setText(counterText);
        } else {
            String counterText = "Please select " + remainingPlayers + " more player(s)";
            playerCountTextView.setText(counterText);
        }
    }


    // Handle the result of the image picker activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Player Name");

            View dialogView = getLayoutInflater().inflate(R.layout.player_enter_name, null);
            EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
            nameEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

            builder.setView(dialogView)
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        String name = nameEditText.getText().toString();
                        addNewPlayer(bitmap, name);
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    // Save player data to SharedPreferences
    private void savePlayerData() {
        SharedPreferences sharedPreferences = getSharedPreferences("player_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(playerList);
        editor.putString("player_list", json);
        editor.apply();
    }

    // Load player data from SharedPreferences
    private void loadPlayerData() {
        SharedPreferences sharedPreferences = getSharedPreferences("player_data", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("player_list", null);
        Type type = new TypeToken<ArrayList<Player>>() {
        }.getType();
        List<Player> loadedPlayerList = gson.fromJson(json, type);

        if (loadedPlayerList != null) {
            playerList.clear();
            playerList.addAll(loadedPlayerList);
            playerListAdapter.notifyDataSetChanged();
        }
    }

    // Convert a Bitmap to a Base64-encoded string
    private String convertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


}
