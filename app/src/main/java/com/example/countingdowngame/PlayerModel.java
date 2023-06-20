package com.example.countingdowngame;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
    private static final int REQUEST_DRAW = 2;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a3_player_choice);

        initializeViews();
        setupPlayerRecyclerView();
        setupDrawButton(); // Add this line
        updatePlayerCounter();
        setupProceedButton();

        // Load player data and add it to the existing playerList
        List<Player> loadedPlayerList = loadPlayerData(this);
        playerList.addAll(loadedPlayerList);
        playerListAdapter.notifyDataSetChanged();
    }

    private void initializeViews() {
        playerRecyclerView = findViewById(R.id.playerRecyclerView);
        playerCountTextView = findViewById(R.id.text_view_counter);

        totalPlayerCount = Game.getInstance().getPlayerAmount();

        playerList = new ArrayList<>(); // Initialize playerList

        int selectedPlayerCount = totalPlayerCount - playerList.size();
    }

    //-----------------------------------------------------Buttons---------------------------------------------------//

    private void setupDrawButton() {
        Button drawButton = findViewById(R.id.createPlayerBtn);
        btnUtils.setButton(drawButton, this::startDrawing);
    }

    private void startDrawing() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_choose_option, null);
        TextView titleTextView = dialogView.findViewById(R.id.titleTextView);
        ListView optionsListView = dialogView.findViewById(R.id.optionsListView);

        titleTextView.setTextColor(getResources().getColor(R.color.bluedark));

        CharSequence[] options = {"Capture a Photo", "Draw a Photo"};
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, R.layout.list_item_option_choose_character, options);

        optionsListView.setAdapter(adapter);

        AlertDialog dialog = builder.setView(dialogView)
                .setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
                .create();

        optionsListView.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Request the permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    captureImage();
                }
            } else if (position == 1) {
                startDrawingActivity();
            }

            dialog.dismiss();
        });

        dialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            }
        }
    }

    // Helper method to start the DrawingActivity
    private void startDrawingActivity() {
        Intent intent = new Intent(this, DrawingActivity.class);
        startActivityForResult(intent, REQUEST_DRAW);
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
                }

        });
    }



    //-----------------------------------------------------Image and player creation functionality---------------------------------------------------//
    // Open image picker to capture an image
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }
    private Bitmap flipBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1); // Flip the bitmap horizontally

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }



    // Convert a Bitmap to a Base64-encoded string
    private String convertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap convertStringToBitmap(String bitmapString) {
        byte[] decodedString = Base64.decode(bitmapString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }


    // Handle the result of the image picker activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            Bitmap rotatedBitmap = flipBitmap(bitmap); // Rotate the bitmap

            showNameInputDialog(rotatedBitmap);
        } else if (requestCode == REQUEST_DRAW && resultCode == RESULT_OK && data != null) {
            String drawnBitmapString = data.getStringExtra("drawnBitmap");
            Bitmap drawnBitmap = convertStringToBitmap(drawnBitmapString);
            showNameInputDialog(drawnBitmap);
        } else if (requestCode == REQUEST_DRAW && resultCode == RESULT_CANCELED) {
            // Handle cancelation
        }
    }


    private void showNameInputDialog(Bitmap bitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Player Name");

        View dialogView = getLayoutInflater().inflate(R.layout.player_enter_name, null);
        EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        nameEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        builder.setView(dialogView)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    String name = nameEditText.getText().toString();
                    if (name.length() < 20) {
                        if (name.length() == 0) {
                            Toast.makeText(this, "Sorry, you need to enter a name.", Toast.LENGTH_SHORT).show();
                            showNameInputDialog(bitmap);
                        } else {
                            createNewCharacter(bitmap, name);
                        }
                    } else {
                        Toast.makeText(this, "Name must be less than 20 characters.", Toast.LENGTH_SHORT).show();
                        showNameInputDialog(bitmap);
                    }
                })

                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void createNewCharacter(Bitmap bitmap, String name) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        int desiredSize = size; // Desired zoomed-in size

        float scale = (float) desiredSize / size;

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        Bitmap zoomedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        String photoString = convertBitmapToString(zoomedBitmap);
        Player newPlayer = new Player(this, photoString, name);
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

    public static List<Player> loadPlayerData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("player_data", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("player_list", null);
        Type type = new TypeToken<ArrayList<Player>>() {}.getType();
        List<Player> loadedPlayerList = gson.fromJson(json, type);

        // Set isSelected to false for all loaded players
        if (loadedPlayerList != null) {
            for (Player player : loadedPlayerList) {
                player.setSelected(false);
            }
        } else {
            loadedPlayerList = new ArrayList<>();
        }

        return loadedPlayerList;
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
            counterText = "❤️ All Players Selected ❤️";
        } else if (remainingPlayers == 1) {
            counterText = "Please Select 1 More Player ❤️";
        } else if (remainingPlayers < 0) {
            int excessPlayers = Math.abs(remainingPlayers);
            if (excessPlayers == 1) {
                counterText = "Please Remove 1 Player \uD83E\uDD13";
            } else {
                counterText = "Please Remove " + excessPlayers + " Players \uD83E\uDD13";
            }
        } else {
            counterText = "Select " + remainingPlayers + " More Players ❤️";
        }
        playerCountTextView.setText(counterText);
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
