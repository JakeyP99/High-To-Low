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

import androidx.activity.OnBackPressedCallback;
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
import com.example.countingdowngame.mainActivity.classAbilities.AbilityComplimentary;
import com.example.countingdowngame.numberChoice.NumberChoice;
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.statistics.Statistics;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.github.muddz.styleabletoast.StyleableToast;


public class PlayerChoice extends playerChoiceComplimentary implements PlayerListAdapter.ClickListener {


    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_DRAW = 2;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private List<Player> playerList;
    private PlayerListAdapter playerListAdapter;
    private TextView playerCountTextView;
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
        updatePlayerCounter();
        playerListAdapter.notifyDataSetChanged();
        proceedButton.setEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_choice_main_activity);

        initializeViews();
        setupPlayerRecyclerView();
        setupDrawButton();
        setupProceedButton();
        loadPlayerData();
        updatePlayerCounter();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }


    public void onPlayerClick(int position) {
        Player player = playerList.get(position);
        player.setSelected(!player.isSelected());

        if (player.isSelected()) {
            player.setSelectionOrder(++selectedPlayerCount);
            if (!Game.getInstance().isPlayCards()) {
                chooseClass(position);
            }
        } else {
            selectedPlayerCount--;
        }

        playerListAdapter.notifyItemChanged(position);
        updatePlayerCounter();
    }

    @Override
    public void onPlayerLongClick(int position) {
        Player player = playerList.get(position);
        showEditNameDialog(player);
    }

    private void showEditNameDialog(Player player) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.player_choice_enter_name_item, null);
        EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        nameEditText.setText(player.getName());
        Button okayButton = dialogView.findViewById(R.id.okButton);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        okayButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();

            if (name.isEmpty()) {
                StyleableToast.makeText(PlayerChoice.this, "Please enter a name.", R.style.newToast).show();
                return;
            }

            if (name.length() >= 20) {
                StyleableToast.makeText(PlayerChoice.this, "Name must be less than 20 characters.", R.style.newToast).show();
                return;
            }

            boolean nameExists = false;
            for (Player p : playerList) {
                if (p != player && p.getName().equalsIgnoreCase(name)) {
                    nameExists = true;
                    break;
                }
            }

            if (nameExists) {
                StyleableToast.makeText(PlayerChoice.this, "Name already exists, please choose a unique name.", R.style.newToast).show();
                return;
            }

            player.setName(name);
            playerListAdapter.notifyItemChanged(playerList.indexOf(player));
            savePlayerData();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void initializeViews() {
        playerRecyclerView = findViewById(R.id.playerRecyclerView);
        playerCountTextView = findViewById(R.id.text_view_counter);
        playerList = new ArrayList<>();
        playerListAdapter = new PlayerListAdapter(this, playerList, this);
        proceedButton = findViewById(R.id.button_done);
        selectedPlayerCount = 0;
    }

    private void setupDrawButton() {
        Button drawButton = findViewById(R.id.createPlayerBtn);
        btnUtils.setButton(drawButton, this::chooseCharacterCreation);
    }

    private void chooseClass(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.player_choice_characterclass_selection, null);
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
                int maxProgress = pagerAdapter.getCount() - 1;
                int currentProgress = calculateProgress(position, maxProgress);
                progressBar.setProgress(currentProgress);
            }

            @Override public void onPageSelected(int position) {}
            @Override public void onPageScrollStateChanged(int state) {}
        });

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setOnCancelListener(dialogInterface -> handleCancelClick(position, dialog));
        dialog.show();
        confirmClass.setOnClickListener(v -> handleConfirmClick(position, dialog));
    }

    private int calculateProgress(int position, int maxProgress) {
        if (maxProgress <= 0) return 100;
        return (position * 100) / maxProgress;
    }

    private void handleCancelClick(int position, AlertDialog dialog) {
        Player player = playerList.get(position);
        dialog.dismiss();
        if (player.isSelected()) {
            player.setSelected(false);
            selectedPlayerCount--;
            playerListAdapter.notifyItemChanged(position);
            updatePlayerCounter();
        }
    }

    private void handleConfirmClick(int position, AlertDialog dialog) {
        Player selectedPlayer = playerList.get(position);
        ViewPager viewPager = dialog.findViewById(R.id.classRecyclerView);

        if (viewPager != null) {
            int selectedPage = viewPager.getCurrentItem();
            int selectedPageNumber = selectedPage + 1;
            CharacterClassStore selectedCharacterClass = findCharacterClassById(selectedPageNumber);

            if (selectedCharacterClass != null) {
                selectedPlayer.setClassChoice(selectedCharacterClass.getClassName());
                AbilityComplimentary.assignActiveAbilityCooldown(selectedPlayer);
                String message = selectedCharacterClass.getClassName().equals("No Class")
                        ? selectedPlayer.getName() + " chose no class!"
                        : selectedPlayer.getName() + " chose the " + selectedCharacterClass.getClassName() + " class!";

                StyleableToast.makeText(getApplicationContext(), message, R.style.newToast).show();
                dialog.dismiss();
            } else {
                selectedPlayer.setClassChoice(null);
                StyleableToast.makeText(this, selectedPlayer.getName() + " chose no class!", R.style.newToast).show();
                dialog.dismiss();
            }
        } else {
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
        return null;
    }

    private void chooseCharacterCreation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.player_choice_camera_draw_dialog, null);

        Button capturePhotoButton = dialogView.findViewById(R.id.capturePhotoButton);
        Button drawPhotoButton = dialogView.findViewById(R.id.drawPhotoButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        AlertDialog dialog = builder.setView(dialogView).create();

        capturePhotoButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                captureImage();
            }
            dialog.dismiss();
        });

        drawPhotoButton.setOnClickListener(v -> {
            startDrawingActivity();
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
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
            if (selectedPlayerCount < 2) {
                StyleableToast.makeText(this, "Please select at least 2 players.", R.style.newToast).show();
                return;
            }

            if (selectedPlayerCount >= 100) {
                StyleableToast.makeText(this, "Too many players! Max 99.", R.style.newToast).show();
                return;
            }

            List<Player> selectedPlayers = new ArrayList<>();
            for (Player player : playerList) {
                if (player.isSelected()) {
                    selectedPlayers.add(player);
                }
            }
            selectedPlayers.sort(Comparator.comparingInt(Player::getSelectionOrder));

            Game.getInstance().setPlayerList(selectedPlayers);
            PlayerModelLocalStore.fromContext(this).saveSelectedPlayers(selectedPlayers);

            ArrayList<String> selectedPlayerNames = new ArrayList<>();
            for (Player player : selectedPlayers) {
                selectedPlayerNames.add(player.getName());
            }
            Intent intent = new Intent(this, NumberChoice.class);
            intent.putStringArrayListExtra("playerNames", selectedPlayerNames);
            startActivity(intent);
        });
    }

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
            Bitmap rotatedBitmap = flipBitmap(bitmap);
            showNameInputDialog(rotatedBitmap);
        } else if (requestCode == REQUEST_DRAW && resultCode == RESULT_OK && data != null) {
            String drawnBitmapString = data.getStringExtra("drawnBitmap");
            Bitmap drawnBitmap = convertStringToBitmap(drawnBitmapString);
            showNameInputDialog(drawnBitmap);
        }
    }

    private void showNameInputDialog(Bitmap bitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        View dialogView = getLayoutInflater().inflate(R.layout.player_choice_enter_name_item, null);
        EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        Button okayButton = dialogView.findViewById(R.id.okButton);

        nameEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        int blueDarkColor = ContextCompat.getColor(this, R.color.bluedark);
        nameEditText.getBackground().mutate().setColorFilter(blueDarkColor, PorterDuff.Mode.SRC_ATOP);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        okayButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            if (name.isEmpty()) {
                StyleableToast.makeText(PlayerChoice.this, "Please enter a name.", R.style.newToast).show();
                return;
            }
            if (name.length() >= 20) {
                StyleableToast.makeText(PlayerChoice.this, "Name must be less than 20 characters.", R.style.newToast).show();
                return;
            }
            boolean nameExists = false;
            for (Player player : playerList) {
                if (player.getName().equalsIgnoreCase(name)) {
                    nameExists = true;
                    break;
                }
            }
            if (nameExists) {
                StyleableToast.makeText(PlayerChoice.this, "Name already exists, please choose a unique name.", R.style.newToast).show();
                return;
            }
            dialog.dismiss();
            createNewPlayer(bitmap, name);
        });
        dialog.show();
    }

    private void createNewPlayer(Bitmap bitmap, String name) {
        String photoString = convertBitmapToString(bitmap);
        String playerId = UUID.randomUUID().toString();
        Player newPlayer = new Player(this, playerId, photoString, name, null);
        newPlayer.setSelected(false);
        playerList.add(newPlayer);
        playerListAdapter.notifyItemInserted(playerList.size() - 1);
        Statistics.saveGlobalTotalDrinkStat(this, 0, name);
        Statistics.savePlayerPhoto(this, name, photoString);
        savePlayerData();
    }

    public void deletePlayer(int position) {
        Player player = playerList.get(position);
        if (player.isSelected()) {
            selectedPlayerCount--;
        }
        playerList.remove(position);
        playerListAdapter.notifyItemRemoved(position);
        savePlayerData();
        updatePlayerCounter();
    }

    private void savePlayerData() {
        Gson gson = new Gson();
        String json = gson.toJson(playerList);
        PlayerModelLocalStore.fromContext(this).setPlayersJSON(json);
    }

    private void loadPlayerData() {
        playerList.clear();
        List<Player> loadedPlayerList = PlayerModelLocalStore.fromContext(this).loadPlayerData();
        playerList.addAll(loadedPlayerList);
        selectedPlayerCount = 0;
        for (Player p : playerList) {
            if (p.isSelected()) selectedPlayerCount++;
        }
        playerListAdapter.notifyDataSetChanged();
    }

    public void updatePlayerCounter() {
        String counterText;
        if (selectedPlayerCount == 0) {
            counterText = "Select Players!";
        } else if (selectedPlayerCount == 1) {
            counterText = "Select 1 More Player!";
        } else {
            counterText = selectedPlayerCount + " Players Selected!";
        }
        playerCountTextView.setText(counterText);
    }

    private void setupPlayerRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        playerRecyclerView.setLayoutManager(layoutManager);
        playerRecyclerView.addItemDecoration(new SpaceItemDecoration(spacing));
        playerRecyclerView.setAdapter(playerListAdapter);
    }

    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int spacing;
        public SpaceItemDecoration(int spacing) { this.spacing = spacing; }
        @Override
        public void getItemOffsets(Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.left = spacing;
            outRect.right = spacing;
            outRect.bottom = spacing;
            outRect.top = (parent.getChildAdapterPosition(view) < 3) ? spacing : 0;
        }
    }
}
