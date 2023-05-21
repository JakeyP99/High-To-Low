package com.example.countingdowngame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

        int selectedPlayerCount = getIntent().getIntExtra("playerCount", 0);
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
        totalPlayerCount = getIntent().getIntExtra("playerCount", 0);
        // Call updatePlayerCounter() to display the counter text initially
        updatePlayerCounter();

        btnUtils.setButton(proceedButton,  () -> {
          startActivity(getIntentForClass(MainActivity.class, true));
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
    private void deletePlayer(int position) {
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

    // RecyclerView adapter for player list
    private class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.ViewHolder> {
        private final Context context;
        private final List<Player> players;
        private int selectedPosition = RecyclerView.NO_POSITION;
        private final int maxSelectedPlayers;

        public PlayerListAdapter(Context context, List<Player> players, int maxSelectedPlayers) {
            this.context = context;
            this.players = players;
            this.maxSelectedPlayers = maxSelectedPlayers;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_view_player_name, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Player player = players.get(position);
            holder.bind(player, position);
        }

        @Override
        public int getItemCount() {
            return players.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView playerPhotoImageView;
            TextView playerNameTextView;
            ImageView deletePlayerImageView;
            View playerItemView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                playerItemView = itemView;
                playerPhotoImageView = itemView.findViewById(R.id.playerPhotoImageView);
                playerNameTextView = itemView.findViewById(R.id.playerNameTextView);
                deletePlayerImageView = itemView.findViewById(R.id.deletePlayerImageView);

                deletePlayerImageView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        deletePlayer(position);
                    }
                });

                // Set click listener for player selection
                playerItemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        togglePlayerSelection(position);
                    }
                });
            }

            public void bind(Player player, int position) {
                String photoString = player.getPhoto();
                Glide.with(context)
                        .load(Base64.decode(photoString, Base64.DEFAULT))
                        .apply(RequestOptions.circleCropTransform())
                        .into(playerPhotoImageView);

                playerNameTextView.setBackgroundResource(R.drawable.outlineforbutton);
                playerNameTextView.setText(player.getName());
                playerNameTextView.setPadding(20, 20, 20, 20);

                // Highlight the selected player
                if (player.isSelected()) {
                    playerItemView.setBackgroundResource(R.drawable.outlineforbutton);
                } else {
                    playerItemView.setBackgroundResource(0);
                }
            }

            private void togglePlayerSelection(int position) {
                Player player = players.get(position);
                player.setSelected(!player.isSelected());
                notifyItemChanged(position);
                updatePlayerCounter();
            }
        }

    }
    private void updatePlayerCounter() {
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
            proceedToMainActivity(selectedPlayers);
        } else {
            counterText = "Remaining Players Needed: " + remainingPlayers;
        }

        playerCountTextView.setText(counterText);
    }

    private void proceedToMainActivity(List<Player> selectedPlayers) {
        Intent intent = new Intent(this, MainActivity.class);

        // Pass the selected player names to the MainActivity
        ArrayList<String> playerNames = new ArrayList<>();
        for (Player player : selectedPlayers) {
            playerNames.add(player.getName());
        }
        intent.putStringArrayListExtra("playerNames", playerNames);
        startActivity(intent);
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
