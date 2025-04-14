package com.example.countingdowngame.playerChoice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.countingdowngame.R;
import com.example.countingdowngame.audio.AudioManager;
import com.example.countingdowngame.createPlayer.CharacterClassPagerAdapter;
import com.example.countingdowngame.createPlayer.CharacterClassStore;
import com.example.countingdowngame.createPlayer.PlayerListAdapter;
import com.example.countingdowngame.createPlayer.PlayerModelLocalStore;
import com.example.countingdowngame.drawing.DrawingPlayerModels;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.home.GameModeChoice;
import com.example.countingdowngame.numberChoice.NumberChoice;
import com.example.countingdowngame.onlinePlay.ServerFind;
import com.example.countingdowngame.player.Player;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.github.muddz.styleabletoast.StyleableToast;
import io.socket.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;


public class PlayerChoice extends playerChoiceComplimentary implements PlayerListAdapter.ClickListener {


    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_DRAW = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private List<Player> playerList;
    private PlayerListAdapter playerListAdapter;
    private TextView playerCountTextView;
    private int totalPlayerCount;
    private RecyclerView playerRecyclerView;
    private int selectedPlayerCount;
    private Button proceedButton;

    @Override
    protected void onResume() {
        super.onResume();
        boolean isMuted = getMuteSoundState();
        AudioManager.getInstance().resumeBackgroundMusic();
        AudioManager.updateMuteStateWithoutButtons(isMuted);

        for (Player existingPlayer : playerList) {
            if (existingPlayer != null) {
                existingPlayer.setName(existingPlayer.getName());
                existingPlayer.setClassChoice(existingPlayer.getClassChoice());
                existingPlayer.setSelected(existingPlayer.isSelected());
            }
        }
        selectedPlayerCount = 0;
        playerListAdapter.notifyDataSetChanged();
        updatePlayerCounter();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_choice);

        initializeViews();
        setupPlayerRecyclerView();
        setupDrawButton();
        setupProceedButton();
        updatePlayerCounter();
        clearPlayerSelection();
        loadPlayerData();
    }


    public void onPlayerClick(int position) {
        Player player = playerList.get(position);
        if (!player.isSelected()) {
            player.setSelected(false); // Change this to true
            player.setSelectionOrder(++selectedPlayerCount); // Track the order of selection
            playerListAdapter.notifyItemChanged(position);
            updatePlayerCounter();
            if (!Game.getInstance().isPlayCards()){
                chooseClass(position);
            }
        }
    }

    @Override
    public void onPlayerLongClick(int position) {
        Player player = playerList.get(position);
        showEditNameDialog(player);
    }

    private void showEditNameDialog(Player player) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.player_enter_name, null);
        EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        nameEditText.setText(player.getName());
        Button okayButton = dialogView.findViewById(R.id.okButton);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Set onClickListener for the okayButton
        okayButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            if (name.length() < 20) {
                if (name.isEmpty()) {
                    StyleableToast.makeText(PlayerChoice.this, "Sorry, you need to enter a name.", R.style.newToast).show();
                } else {
                    player.setName(name);
                    playerListAdapter.notifyItemChanged(playerList.indexOf(player));
                    savePlayerData(); // Update player name in storage
                    dialog.dismiss(); // Dismiss the dialog after changing the name
                }
            } else {
                StyleableToast.makeText(PlayerChoice.this, "Name must be less than 20 characters.", R.style.newToast).show();
            }
        });

        dialog.show();
    }

    private void clearPlayerSelection() {
        for (Player player : playerList) {
            player.setSelected(false);
        }
        playerListAdapter.notifyDataSetChanged();
    }

    private void initializeViews() {
        playerRecyclerView = findViewById(R.id.playerRecyclerView);
        playerCountTextView = findViewById(R.id.text_view_counter);


        totalPlayerCount = Game.getInstance().getPlayerAmount();
        playerList = new ArrayList<>();
        playerListAdapter = new PlayerListAdapter(this, playerList, this);
        proceedButton = findViewById(R.id.button_done);
        selectedPlayerCount = 0;


    }

    //-----------------------------------------------------Buttons---------------------------------------------------//

    private void setupDrawButton() {
        Button drawButton = findViewById(R.id.createPlayerBtn);
        btnUtils.setButton(drawButton, this::chooseCharacterCreation);
    }

    //-----------------------------------------------------Choose the player class---------------------------------------------------//

    private void chooseClass(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.characterclass_selection, null);
        Button confirmClass = dialogView.findViewById(R.id.btnConfirmClass);

        List<CharacterClassStore> characterClasses = generateCharacterClasses();

        int itemsPerPage = 1;
        List<List<CharacterClassStore>> pages = new ArrayList<>();
        for (int i = 0; i < characterClasses.size(); i += itemsPerPage) {
            int endIndex = Math.min(i + itemsPerPage, characterClasses.size());
            pages.add(characterClasses.subList(i, endIndex));
        }

        ViewPager viewPager = dialogView.findViewById(R.id.classRecyclerView);
        ProgressBar progressBar = dialogView.findViewById(R.id.progress);

        CharacterClassPagerAdapter pagerAdapter = new CharacterClassPagerAdapter(pages);
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Calculate the progress based on the ViewPager's scroll position
                int maxProgress = pagerAdapter.getCount() - 1;
                int currentProgress = calculateProgress(position, maxProgress);
                progressBar.setProgress(currentProgress);
            }

            @Override
            public void onPageSelected(int position) {
                // Unused method
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Unused method
            }
        });

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialog.setOnCancelListener(dialogInterface -> handleCancelClick(position, dialog));

        dialog.show();

        confirmClass.setOnClickListener(v -> handleConfirmClick(position, dialog));
    }

    private int calculateProgress(int position, int maxProgress) {
        // Calculate progress based on the position and the maximum progress value
        return (position * 100) / maxProgress;
    }

    private void handleCancelClick(int position, AlertDialog dialog) {
        Player player = playerList.get(position);
        dialog.dismiss();

        if (player.isSelected()) {
            player.setSelected(false);
            playerListAdapter.notifyItemChanged(position);
            updatePlayerCounter();
            Log.d("Confirm Button", "Player selection was cancelled");

        }

    }

    private void handleConfirmClick(int position, AlertDialog dialog) {
        Player selectedPlayer = playerList.get(position);
        ViewPager viewPager = dialog.findViewById(R.id.classRecyclerView);

        if (viewPager != null) {
            int selectedPage = viewPager.getCurrentItem();
            int selectedPageNumber = selectedPage + 1; // Add 1 since ViewPager starts counting from 0
            CharacterClassStore selectedCharacterClass = findCharacterClassById(selectedPageNumber);

            if (selectedCharacterClass != null) {
                selectedPlayer.setClassChoice(selectedCharacterClass.getClassName());
                String message = selectedCharacterClass.getClassName().equals("No Class") ? selectedPlayer.getName() + " chose no class!" : selectedPlayer.getName() + " chose the " + selectedCharacterClass.getClassName() + " class!";
                StyleableToast.makeText(getApplicationContext(), message, R.style.newToast).show();
                Log.d("Confirm Button", "Confirm Button Clicked - Page Number: " + selectedPageNumber + ", Character ID: " + selectedCharacterClass.getId());
                
                // Send player information to server
                Socket mSocket = ServerFind.getSocket();
                if (mSocket != null && mSocket.connected()) {
                    Log.d("PlayerChoice", "Emitting join with player: " + selectedPlayer.getName() + ", class: " + selectedPlayer.getClassChoice());
                    JSONObject playerData = new JSONObject();
                    try {
                        playerData.put("name", selectedPlayer.getName());
                        playerData.put("classChoice", selectedPlayer.getClassChoice());
                        mSocket.emit("join", playerData);
                    } catch (JSONException e) {
                        Log.e("PlayerChoice", "Error creating JSON object", e);
                    }
                } else {
                    Log.e("PlayerChoice", "Socket is null or not connected");
                }
                
                dialog.dismiss();
            } else {
                selectedPlayer.setClassChoice(null);
                StyleableToast.makeText(this, selectedPlayer.getName() + " chose no class!", R.style.newToast).show();
                Log.d("Confirm Button", "No class chosen");
                dialog.dismiss();
            }
        } else {
            Log.e("Confirm Button", "ViewPager not found");
            dialog.dismiss();
        }
    }


    private CharacterClassStore findCharacterClassById(int id) {
        List<CharacterClassStore> characterClasses = generateCharacterClasses();
        for (CharacterClassStore characterClass : characterClasses) {
            if (characterClass.getId() == id) {
                return characterClass;
            }
        }
        return null; // If ID is not found, return null
    }


    //-----------------------------------------------------Choose the player creation---------------------------------------------------//

    private void chooseCharacterCreation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_choose_option, null);

        Button capturePhotoButton = dialogView.findViewById(R.id.capturePhotoButton);
        Button drawPhotoButton = dialogView.findViewById(R.id.drawPhotoButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        AlertDialog dialog = builder.setView(dialogView).create(); // Create the AlertDialog

        capturePhotoButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                captureImage();
            }
            dialog.dismiss(); // Close the dialog after clicking the button
        });

        drawPhotoButton.setOnClickListener(v -> {
            startDrawingActivity();
            dialog.dismiss(); // Close the dialog after clicking the button
        });

        // Set onClickListener for the cancel button
        cancelButton.setOnClickListener(v -> dialog.dismiss()); // Dismiss the dialog when cancel button is clicked

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

    private void startDrawingActivity() {
        Intent intent = new Intent(this, DrawingPlayerModels.class);
        startActivityForResult(intent, REQUEST_DRAW);
    }
    private void setupProceedButton() {
        btnUtils.setButton(proceedButton, () -> {
            Log.d("PlayerChoice", "selectedPlayerCount: " + selectedPlayerCount);
            Log.d("PlayerChoice", "totalPlayerCount: " + totalPlayerCount);

            if (!GameModeChoice.isOnlineGame() && selectedPlayerCount == totalPlayerCount) {
                handleSelectedPlayers();
                Log.d("PlayerChoice", "online: " + totalPlayerCount);

            } else if (GameModeChoice.isOnlineGame() && selectedPlayerCount == 1) {
                handleSelectedPlayers();
            } else {
                StyleableToast.makeText(this, "Please select all players.", R.style.newToast).show();
            }
        });
    }

    private void handleSelectedPlayers() {
        List<Player> selectedPlayers = new ArrayList<>();
        for (Player player : playerList) {
            if (player.isSelected()) {
                selectedPlayers.add(player);
            }
        }

        // Sort the players by their selection order
        selectedPlayers.sort(Comparator.comparingInt(Player::getSelectionOrder));

        proceedButton.setEnabled(false);
        final long delayMillis = 3000;
        new Handler().postDelayed(() -> proceedButton.setEnabled(true), delayMillis);

        PlayerModelLocalStore.fromContext(this).saveSelectedPlayers(selectedPlayers);
        ArrayList<String> selectedPlayerNames = new ArrayList<>();
        for (Player player : selectedPlayers) {
            selectedPlayerNames.add(player.getName());
        }
        Intent intent = new Intent(this, NumberChoice.class);
        intent.putStringArrayListExtra("playerNames", selectedPlayerNames);
        startActivity(intent);
    }



    //-----------------------------------------------------Image and player creation functionality---------------------------------------------------//
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private Bitmap flipBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            Bitmap rotatedBitmap = flipBitmap(bitmap); // Rotate the bitmap

            showNameInputDialog(rotatedBitmap);
        } else if (requestCode == REQUEST_DRAW && resultCode == RESULT_OK && data != null) {
            String drawnBitmapString = data.getStringExtra("drawnBitmap");
            Bitmap drawnBitmap = convertStringToBitmap(drawnBitmapString);
            showNameInputDialog(drawnBitmap);
        }
    }

    private void showNameInputDialog(Bitmap bitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);

        View dialogView = getLayoutInflater().inflate(R.layout.player_enter_name, null);
        EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        Button okayButton = dialogView.findViewById(R.id.okButton);

        nameEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        int blueDarkColor = ContextCompat.getColor(this, R.color.bluedark);
        nameEditText.getBackground().mutate().setColorFilter(blueDarkColor, PorterDuff.Mode.SRC_ATOP);
        nameEditText.setHighlightColor(blueDarkColor);

        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // Set onClickListener for the okayButton
        okayButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            if (name.length() < 20) {
                if (name.isEmpty()) {
                    StyleableToast.makeText(PlayerChoice.this, "Sorry, you need to enter a name.", R.style.newToast).show();
                    showNameInputDialog(bitmap);
                } else {
                    dialog.dismiss(); // Dismiss the dialog when okayButton is clicked
                    createNewCharacter(bitmap, name);
                }
            } else {
                StyleableToast.makeText(PlayerChoice.this, "Name must be less than 20 characters.", R.style.newToast).show();
                showNameInputDialog(bitmap);
            }
        });

        dialog.show();
    }

    private void createNewCharacter(Bitmap bitmap, String name) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());

        float scale = (float) size / size;

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        Bitmap zoomedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        String photoString = convertBitmapToString(zoomedBitmap);

        // Generate a UUID for the player
        String playerId = UUID.randomUUID().toString();

        Player newPlayer = new Player(this, playerId, photoString, name, null); // Pass the generated ID
        newPlayer.setSelected(false); // Set isSelected to false initially
        playerList.add(newPlayer);
        playerListAdapter.notifyItemInserted(playerList.size() - 1);
        savePlayerData();
    }


    public void deletePlayer(int position) {
        playerList.remove(position);
        playerListAdapter.notifyItemRemoved(position);
        savePlayerData();
        updatePlayerCounter();
    }

    //-----------------------------------------------------Save and load functionality---------------------------------------------------//

    // Save player data to SharedPreferences
    private void savePlayerData() {
        Gson gson = new Gson();
        String json = gson.toJson(playerList);
        PlayerModelLocalStore.fromContext(this).setPlayersJSON(json);
    }

    private void loadPlayerData() {
        playerList.clear();

        // Load player data from local store
        List<Player> loadedPlayerList = PlayerModelLocalStore.fromContext(this).loadPlayerData();
        int startPosition = playerList.size();
        playerList.addAll(loadedPlayerList);
        int newItemCount = playerList.size() - startPosition;

        // Notify adapter for any new items added
        if (newItemCount > 0) {
            playerListAdapter.notifyItemRangeInserted(startPosition, newItemCount);
        }
    }

    //-----------------------------------------------------Player Counter Functionality---------------------------------------------------//
    public void updatePlayerCounter() {
        selectedPlayerCount = 0;

        for (Player player : playerList) {
            if (player.isSelected()) {
                selectedPlayerCount++;
            }
        }

        String counterText;
        if (GameModeChoice.isOnlineGame()) {
            if (selectedPlayerCount == 0) {
                counterText = "Please choose your character";
            } else if (selectedPlayerCount == 1) {
                counterText = "Character selected!";
            } else {
                counterText = "Please select only one character";
            }
        } else {
            int remainingPlayers = totalPlayerCount - selectedPlayerCount;

            if (remainingPlayers == 0) {
                counterText = "All Players Selected!";
            } else if (remainingPlayers == 1) {
                counterText = "Select 1 More Player!";
            } else if (remainingPlayers < 0) {
                int excessPlayers = Math.abs(remainingPlayers);
                if (excessPlayers == 1) {
                    counterText = "Please Remove 1 Player \uD83E\uDD13";
                } else {
                    counterText = "Please Remove " + excessPlayers + " Players \uD83E\uDD13";
                }
            } else {
                counterText = "Select " + remainingPlayers + " More Players!";
            }
        }
        playerCountTextView.setText(counterText);
    }


    //-----------------------------------------------------UI Decoration---------------------------------------------------//
    private void setupPlayerRecyclerView() {
        playerList = PlayerModelLocalStore.fromContext(this).loadSelectedPlayers();
        playerListAdapter = new PlayerListAdapter(this, playerList, this);

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
