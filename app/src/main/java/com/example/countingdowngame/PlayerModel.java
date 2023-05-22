package com.example.countingdowngame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PlayerModel extends ButtonUtilsActivity {
    private static final int REQUEST_IMAGE_PICK = 1;
    private static List<Player> playerList;
    private static PlayerListAdapter playerListAdapter;
    private TextView playerCountTextView;
    private int totalPlayerCount;
    private RecyclerView playerRecyclerView; // Declare playerRecyclerView

    private Button chooseImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a3_player_choice);

        initializeViews();
        setupPlayerRecyclerView();
        setupChooseImageButton();
        updatePlayerCounter();
        setupProceedButton();

        // Load player data and add it to the existing playerList
        List<Player> loadedPlayerList = loadSelectedPlayers(this);
        playerList.addAll(loadedPlayerList);
        playerListAdapter.notifyDataSetChanged();
    }

    private void initializeViews() {
        playerRecyclerView = findViewById(R.id.playerRecyclerView);
        chooseImageButton = findViewById(R.id.chooseImageButton);
        playerCountTextView = findViewById(R.id.text_view_counter);

        totalPlayerCount = Game.getInstance().getPlayerAmount();

        playerList = new ArrayList<>(); // Initialize playerList

        // Calculate selectedPlayerCount after initializing playerList
        int selectedPlayerCount = totalPlayerCount - playerList.size();
    }


    //-----------------------------------------------------Buttons---------------------------------------------------//

    private void setupChooseImageButton() {
        chooseImageButton.setOnClickListener(view -> captureImage());
    }

    private void setupProceedButton() {
        Button proceedButton = findViewById(R.id.button_done);

        btnUtils.setButton(proceedButton, () -> {
            List<String> selectedPlayerNames = new ArrayList<>();
            for (Player player : playerList) {
                if (player.isSelected()) {
                    selectedPlayerNames.add(player.getName());
                }
            }

            if (selectedPlayerNames.isEmpty()) {
                Toast.makeText(this, "Please select players", Toast.LENGTH_SHORT).show();
            } else {
                int remainingPlayers = totalPlayerCount - selectedPlayerNames.size();
                if (remainingPlayers == 0) {
                    List<Player> selectedPlayers = new ArrayList<>();
                    for (Player player : playerList) {
                        if (player.isSelected()) {
                            selectedPlayers.add(player);
                        }
                    }

                    saveSelectedPlayers(this, selectedPlayers);
                    Intent intent = new Intent(this, NumberChoice.class);
                    intent.putStringArrayListExtra("playerNames", (ArrayList<String>) selectedPlayerNames);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Please select " + remainingPlayers + " more player(s)", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    //-----------------------------------------------------Image and player creation functionality---------------------------------------------------//
    // Open image picker to capture an image
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    // Convert a Bitmap to a Base64-encoded string
    private String convertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
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
                        createNewCharacter(bitmap, name);
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    // Add a new player with the selected image and name
    private void createNewCharacter(Bitmap bitmap, String name) {
        String photoString = convertBitmapToString(bitmap);
        Player newPlayer = new Player(photoString, name);
        newPlayer.setSelected(false); // Set isSelected to false initially
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


    //-----------------------------------------------------Save and load functionality---------------------------------------------------//

    // Save player data to SharedPreferences
    private void savePlayerData() {
        SharedPreferences sharedPreferences = getSharedPreferences("player_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(playerList);
        editor.putString("player_list", json);
        editor.apply();
    }

    public static void saveSelectedPlayers(Context context, List<Player> selectedPlayers) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("selected_players", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(selectedPlayers);
        editor.putString("selected_players_list", json);
        editor.apply();
    }

    // Load player data from SharedPreferences
    public static List<Player> loadSelectedPlayers(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("selected_players", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("selected_players_list", null);
        Type type = new TypeToken<ArrayList<Player>>() {}.getType();
        List<Player> selectedPlayers = gson.fromJson(json, type);

        // Return an empty list if no data is loaded
        if (selectedPlayers == null) {
            selectedPlayers = new ArrayList<>();
        }

        return selectedPlayers;
    }


    //-----------------------------------------------------Player Counter Functionality---------------------------------------------------//
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
        String counterText;
        if (remainingPlayers == 0) {
            counterText = "All Players Selected: " + totalPlayerCount;
            // Proceed with the game since all players are selected
            // Trigger the startGame() method in your activity here
        } else {
            counterText = "Please select " + remainingPlayers + " more player(s)";
        }
        playerCountTextView.setText(counterText);
    }

    public static void resetPlayerData(Context context) {
        // Clear the selected state of players
        for (Player player : playerList) {
            player.setSelected(false);
        }
        playerListAdapter.notifyDataSetChanged();

        // Clear the stored selected player data from shared preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("selectedPlayerName");
        editor.remove("selectedPlayerImage");
        editor.apply();
    }


    //-----------------------------------------------------UI Decoration---------------------------------------------------//
    private void setupPlayerRecyclerView() {
        playerList = new ArrayList<>();
        playerListAdapter = new PlayerListAdapter(this, playerList);
        loadSelectedPlayers(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);

        playerRecyclerView.setLayoutManager(layoutManager);
        playerRecyclerView.addItemDecoration(new SpaceItemDecoration(spacing));
        playerRecyclerView.setAdapter(playerListAdapter);
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
}
