package com.example.countingdowngame.createPlayer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.game.Player;
import com.example.countingdowngame.mainActivity.NumberChoice;
import com.example.countingdowngame.stores.PlayerModelLocalStore;
import com.example.countingdowngame.utils.ButtonUtilsActivity;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PlayerChoice extends ButtonUtilsActivity implements PlayerListAdapter.ClickListener {

    public static final String CLASS_ARCHER = "Archer";
    public static final String CLASS_BARBARIAN = "Barbarian";
    public static final String CLASS_WITCH = "Witch";
    public static final String CLASS_SCIENTIST = "Scientist";
    public static final String CLASS_SOLDIER = "Soldier";
    public static final String CLASS_JIM = "Jim";

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_DRAW = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private List<Player> playerList;
    private PlayerListAdapter playerListAdapter;
    private TextView playerCountTextView;
    private int totalPlayerCount;
    private RecyclerView playerRecyclerView;
    private int selectedPlayerCount;

    @Override
    protected void onResume() {
        super.onResume();
        playerList.clear();
        List<Player> loadedPlayerList = PlayerModelLocalStore.fromContext(this).loadPlayerData();
        playerList.addAll(loadedPlayerList);
        selectedPlayerCount = 0;
        playerListAdapter.notifyDataSetChanged();
        updatePlayerCounter();
    }


    @Override
    public void onPlayerClick(int position) {
        Player player = playerList.get(position);
        if (!player.isSelected()) {
            player.setSelected(false);
            playerListAdapter.notifyItemChanged(position);
            updatePlayerCounter();
            chooseClass(position);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_choice);

        totalPlayerCount = Game.getInstance().getPlayerAmount();
        playerList = new ArrayList<>();
        playerListAdapter = new PlayerListAdapter(this, playerList, this);

        selectedPlayerCount = 0; // Initialize selectedPlayerCount to 0
        initializeViews();
        setupPlayerRecyclerView();
        setupDrawButton();
        updatePlayerCounter();
        setupProceedButton();

        // Clear the selection of players
        for (Player player : playerList) {
            player.setSelected(false);
        }
        playerListAdapter.notifyDataSetChanged();

        List<Player> loadedPlayerList = PlayerModelLocalStore.fromContext(this).loadPlayerData();
        int startPosition = playerList.size();

        playerList.addAll(loadedPlayerList);
        int newItemCount = playerList.size() - startPosition;

        if (newItemCount > 0) {
            playerListAdapter.notifyItemRangeInserted(startPosition, newItemCount);
        }
    }


    private void initializeViews() {
        playerRecyclerView = findViewById(R.id.playerRecyclerView);
        playerCountTextView = findViewById(R.id.text_view_counter);

        totalPlayerCount = Game.getInstance().getPlayerAmount();
        playerList = new ArrayList<>();
    }

    //-----------------------------------------------------Buttons---------------------------------------------------//

    private void setupDrawButton() {
        Button drawButton = findViewById(R.id.createPlayerBtn);
        btnUtils.setButton(drawButton, this::chooseCharacterCreation);
    }

    //-----------------------------------------------------Choose the player class---------------------------------------------------//

    private void chooseClass(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.characterclass_selection, null);
        Button confirmClass = dialogView.findViewById(R.id.btnConfirmClass);

        RecyclerView classRecyclerView = dialogView.findViewById(R.id.classRecyclerView);
        classRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        List<CharacterClassStore> characterClasses = new ArrayList<>();
        characterClasses.add(new CharacterClassStore(CLASS_ARCHER, "Active Ability: You can take two drinks off the total amount, and hand them out to players. \n\nPassive Ability: For every third turn, there is a 50% chance the drinking number will increase or decrease by 2."));
//        characterClasses.add(new CharacterClassStore(CLASS_BARBARIAN, "You can deny three drinks handed out to you."));
        characterClasses.add(new CharacterClassStore(CLASS_WITCH, "Active Ability: You can skip your turn. \n\nPassive Ability: For every even number your player lands on, you can hand out two drinks, but for every odd number you have to take a drink."));
        characterClasses.add(new CharacterClassStore(CLASS_SCIENTIST, "Active Ability: On your turn, you can change the current number to be whatever you want. You must roll again! \n\nPassive Ability: There's a 10% chance your turn is skipped."));
        characterClasses.add(new CharacterClassStore(CLASS_SOLDIER, "Passive Ability: If you land between 10 and 15, you escape the game and are not longer in it. \n\nPassive Ability: For every third turn, you gain one wildcard."));
        characterClasses.add(new CharacterClassStore(CLASS_JIM, "Regular Jim does nothing crazy, he can yo-yo pretty good though."));


        CharacterClassAdapter adapter = new CharacterClassAdapter(characterClasses);
        classRecyclerView.setAdapter(adapter);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Set a cancel listener for the dialog
        dialog.setCancelable(false);

        dialog.show();

        confirmClass.setOnClickListener(v -> {
            int selectedPosition = adapter.getSelectedItemPosition();
            Player selectedPlayer = playerList.get(position);
            if (selectedPosition != -1) {
                CharacterClassStore selectedCharacterClass = characterClasses.get(selectedPosition);
                selectedPlayer.setClassChoice(selectedCharacterClass.getClassName());
                dialog.dismiss();
            } else {
                // Show the message when no class is selected
                Toast.makeText(getApplicationContext(), "Please confirm a class.", Toast.LENGTH_SHORT).show();
            }
            Log.d("Confirm Button", "Confirm Button Clicked and " + selectedPlayer.getName() + " choose " + selectedPlayer.getClassChoice());
        });
    }

    //-----------------------------------------------------Choose the player creation---------------------------------------------------//

    private void chooseCharacterCreation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_choose_option, null);
        Button capturePhotoButton = dialogView.findViewById(R.id.capturePhotoButton);
        Button drawPhotoButton = dialogView.findViewById(R.id.drawPhotoButton);

        capturePhotoButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                captureImage();
            }
            builder.create().dismiss();
        });

        drawPhotoButton.setOnClickListener(v -> {
            startDrawingActivity();
            builder.create().dismiss();
        });

        AlertDialog dialog = builder.setView(dialogView)
                .setNegativeButton(Html.fromHtml("<font color='" + R.color.bluedark + "'>Cancel</font>"), (dialogInterface, which) -> dialogInterface.dismiss())
                .create();

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
        Intent intent = new Intent(this, DrawingPlayerModels.class);
        startActivityForResult(intent, REQUEST_DRAW);
    }

    private void setupProceedButton() {
        Button proceedButton = findViewById(R.id.button_done);

        btnUtils.setButton(proceedButton, () -> {
            ArrayList<String> selectedPlayerNames = new ArrayList<>();
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

                PlayerModelLocalStore.fromContext(this).saveSelectedPlayers(selectedPlayers);
                Intent intent = new Intent(this, NumberChoice.class);
                intent.putStringArrayListExtra("playerNames", selectedPlayerNames);
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
        }
    }

    private void showNameInputDialog(Bitmap bitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = "Enter Your Name";

        View dialogView = getLayoutInflater().inflate(R.layout.player_enter_name, null);
        EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        nameEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        int blueDarkColor = getResources().getColor(R.color.bluedark);
        nameEditText.getBackground().mutate().setColorFilter(blueDarkColor, PorterDuff.Mode.SRC_ATOP);
        nameEditText.setHighlightColor(blueDarkColor);

        SpannableString spannableTitle = new SpannableString(title);
        spannableTitle.setSpan(new ForegroundColorSpan(blueDarkColor), 0, spannableTitle.length(), 0);
        builder.setTitle(spannableTitle);


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
                });


        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        positiveButton.setTextColor(blueDarkColor);
        negativeButton.setTextColor(blueDarkColor);
    }


    private void createNewCharacter(Bitmap bitmap, String name) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());

        float scale = (float) size / size;

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        Bitmap zoomedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        String photoString = convertBitmapToString(zoomedBitmap);
        Player newPlayer = new Player(this, photoString, name, null);
        newPlayer.setSelected(true); // Set isSelected to false initially
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

    //-----------------------------------------------------Player Counter Functionality---------------------------------------------------//
    public void updatePlayerCounter() {
        selectedPlayerCount = 0;

        for (Player player : playerList) {
            if (player.isSelected()) {
                selectedPlayerCount++;
            }
        }

        int remainingPlayers = totalPlayerCount - selectedPlayerCount;

        String counterText;
        if (remainingPlayers == 0) {
            counterText = "❤️ All Players Selected ❤️";
        } else if (remainingPlayers == 1) {
            counterText = "Select 1 More Player ❤️";
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
