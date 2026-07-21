package com.mydomain.countingdowngame.playerChoice;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.mydomain.countingdowngame.R;
import com.mydomain.countingdowngame.audio.AudioManager;
import com.mydomain.countingdowngame.game.Game;
import com.mydomain.countingdowngame.mainActivity.classAbilities.AbilityComplimentary;
import com.mydomain.countingdowngame.numberChoice.NumberChoice;
import com.mydomain.countingdowngame.player.Player;
import com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions;
import com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassPagerAdapter;
import com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassStore;
import com.mydomain.countingdowngame.playerChoice.createPlayer.PlayerModelLocalStore;
import com.mydomain.countingdowngame.playerChoice.drawing.DrawingPlayerModels;
import com.mydomain.countingdowngame.statistics.Statistics;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

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
    private RecyclerView playerRecyclerView;
    private int selectedPlayerCount;
    private Button proceedButton;
    private Player editingPlayer;

    @Override
    protected void onResume() {
        super.onResume();
        boolean isMuted = getMuteSoundState();
        AudioManager.updateMuteStateWithoutButtons(isMuted);

        for (Player existingPlayer : playerList) {
            if (existingPlayer != null) {
                existingPlayer.setName(existingPlayer.getName());
                existingPlayer.setClassChoice(existingPlayer.getClassChoice());
                existingPlayer.setSelected(existingPlayer.isSelected());
            }
        }
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
            player.setClassChoice(null); // Clear any previous class choice when selecting
            if (!Game.getInstance().isPlayCards() && Game.getInstance().getGameMode() == Game.GameMode.CLASSIC) {
                chooseClass(position);
            } else if (Game.getInstance().getGameMode() == Game.GameMode.CLASS_HUNT) {
                player.setClassChoice(CharacterClassDescriptions.NO_CLASS);
            }
        } else {
            selectedPlayerCount--;
            player.setClassChoice(null); // Clear class choice when deselecting
        }

        playerListAdapter.notifyItemChanged(position);
    }

    @Override
    public void onEditPlayerClick(int position) {
        Player player = playerList.get(position);
        showEditPlayerOptionsDialog(player);
    }

    private void showEditPlayerOptionsDialog(Player player) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        View dialogView = getLayoutInflater().inflate(R.layout.player_choice_edit_options_dialog, null);

        Button capturePhotoButton = dialogView.findViewById(R.id.capturePhotoButton);
        Button drawPhotoButton = dialogView.findViewById(R.id.drawPhotoButton);
        Button editNameButton = dialogView.findViewById(R.id.editNameButton);

        AlertDialog dialog = builder.setView(dialogView).create();

        btnUtils.setButton(capturePhotoButton, () -> {
            editingPlayer = player;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                captureImage();
            }
            dialog.dismiss();
        });

        btnUtils.setButton(drawPhotoButton, () -> {
            editingPlayer = player;
            startDrawingActivity();
            dialog.dismiss();
        });

        btnUtils.setButton(editNameButton, () -> showEditNameDialog(player, dialog));

        dialog.show();
    }

    private void showEditNameDialog(Player player, AlertDialog currentDialog) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.player_choice_enter_name_item, null);
        EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        nameEditText.setText(player.getName());
        nameEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        disableSelectionMenu(nameEditText);
        Button okayButton = dialogView.findViewById(R.id.okButton);

        currentDialog.setContentView(dialogView);

        nameEditText.requestFocus();
        nameEditText.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(nameEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);

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

            boolean nameExistsDuplicate = false;
            for (Player p : playerList) {
                if (p != player && p.getName().equalsIgnoreCase(name)) {
                    nameExistsDuplicate = true;
                    break;
                }
            }

            if (nameExistsDuplicate) {
                StyleableToast.makeText(PlayerChoice.this, "Name already exists, please choose a unique name.", R.style.newToast).show();
                return;
            }

            player.setName(name);
            playerListAdapter.notifyItemChanged(playerList.indexOf(player));
            savePlayerData();
            currentDialog.dismiss();
        });
    }

    private void initializeViews() {
        playerRecyclerView = findViewById(R.id.playerRecyclerView);
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
        DotsIndicator dotsIndicator = dialogView.findViewById(R.id.dots_indicator);

        CharacterClassPagerAdapter pagerAdapter = new CharacterClassPagerAdapter(pages);
        pagerAdapter.setInfinite(true);
        viewPager.setAdapter(pagerAdapter);

        // Setup a fake ViewPager to trick the DotsIndicator into only showing real pages
        ViewPager fakeViewPager = new ViewPager(this);
        CharacterClassPagerAdapter dotsAdapter = new CharacterClassPagerAdapter(pages);
        dotsAdapter.setInfinite(false);
        fakeViewPager.setAdapter(dotsAdapter);
        dotsIndicator.setViewPager(fakeViewPager);

        // Add fakeViewPager to the hierarchy so it can animate and sync the dots
        fakeViewPager.setVisibility(View.INVISIBLE);
        ViewGroup root = (ViewGroup) dialogView;
        root.addView(fakeViewPager, new ViewGroup.LayoutParams(100, 100));

        int realCount = pages.size();
        int initialPosition = (Integer.MAX_VALUE / 2) - ((Integer.MAX_VALUE / 2) % realCount);
        viewPager.setCurrentItem(initialPosition, false);
        fakeViewPager.setCurrentItem(0, false);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Sync the fake ViewPager for the dots to show smooth movement
                // We use fake drag to trigger the indicator's scroll listeners
                if (fakeViewPager.isFakeDragging() || fakeViewPager.beginFakeDrag()) {
                    float totalScroll = (position % realCount + positionOffset);
                    float fakeViewPagerScroll = fakeViewPager.getScrollX();
                    float targetScroll = totalScroll * fakeViewPager.getWidth();
                    float delta = targetScroll - fakeViewPagerScroll;
                    fakeViewPager.fakeDragBy(-delta); // ViewPager scroll is inverted relative to drag
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (fakeViewPager.isFakeDragging()) {
                    fakeViewPager.endFakeDrag();
                }
                fakeViewPager.setCurrentItem(position % realCount, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE && fakeViewPager.isFakeDragging()) {
                    fakeViewPager.endFakeDrag();
                }
            }
        });

        ImageView btnNext = dialogView.findViewById(R.id.btnNext);
        ImageView btnPrevious = dialogView.findViewById(R.id.btnPrevious);

        btnNext.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true));
        btnPrevious.setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true));

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setOnCancelListener(dialogInterface -> handleCancelClick(position, dialog));
        dialog.show();
        btnUtils.setButton(confirmClass, () -> handleConfirmClick(position, dialog));

    }

    private void handleCancelClick(int position, AlertDialog dialog) {
        Player player = playerList.get(position);
        dialog.dismiss();
        if (player.isSelected()) {
            player.setSelected(false);
            selectedPlayerCount--;
            playerListAdapter.notifyItemChanged(position);
        }
    }

    private void handleConfirmClick(int position, AlertDialog dialog) {
        Player selectedPlayer = playerList.get(position);
        ViewPager viewPager = dialog.findViewById(R.id.classRecyclerView);

        if (viewPager != null) {
            List<CharacterClassStore> characterClasses = generateCharacterClasses();
            int realCount = characterClasses.size();
            int selectedPage = viewPager.getCurrentItem();
            int actualIndex = selectedPage % realCount;
            CharacterClassStore selectedCharacterClass = characterClasses.get(actualIndex);

            if (selectedCharacterClass != null) {
                selectedPlayer.setClassChoice(selectedCharacterClass.getClassName());
                AbilityComplimentary.assignActiveAbilityCooldown(selectedPlayer);
                playerListAdapter.notifyItemChanged(position);
                dialog.dismiss();
            } else {
                selectedPlayer.setClassChoice(null);
                playerListAdapter.notifyItemChanged(position);
                StyleableToast.makeText(this, selectedPlayer.getName() + " chose no class!", R.style.newToast).show();
                dialog.dismiss();
            }
        } else {
            dialog.dismiss();
        }
    }

    private void chooseCharacterCreation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        AlertDialog dialog = builder.create();
        editingPlayer = null;
        dialog.show();
        showChooseCharacterCreationDialog(dialog);
    }

    private void showChooseCharacterCreationDialog(AlertDialog currentDialog) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.player_choice_camera_draw_dialog, null);

        Button capturePhotoButton = dialogView.findViewById(R.id.capturePhotoButton);
        Button drawPhotoButton = dialogView.findViewById(R.id.drawPhotoButton);

        currentDialog.setContentView(dialogView);

        btnUtils.setButton(capturePhotoButton, () -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                captureImage();
            }
            currentDialog.dismiss();
        });

        btnUtils.setButton(drawPhotoButton, () -> {
            startDrawingActivity();
            currentDialog.dismiss();
        });
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
            if (editingPlayer != null) {
                updatePlayerPhoto(editingPlayer, rotatedBitmap);
            } else {
                showNameInputDialog(rotatedBitmap);
            }
        } else if (requestCode == REQUEST_DRAW && resultCode == RESULT_OK && data != null) {
            String drawnBitmapString = data.getStringExtra("drawnBitmap");
            Bitmap drawnBitmap = convertStringToBitmap(drawnBitmapString);
            if (editingPlayer != null) {
                updatePlayerPhoto(editingPlayer, drawnBitmap);
            } else {
                showNameInputDialog(drawnBitmap);
            }
        }
    }

    private void updatePlayerPhoto(Player player, Bitmap bitmap) {
        String photoString = convertBitmapToString(bitmap);
        player.setPhoto(photoString);
        playerListAdapter.notifyItemChanged(playerList.indexOf(player));
        Statistics.savePlayerPhoto(this, player.getName(), photoString);
        savePlayerData();
        editingPlayer = null;
    }

    private void showNameInputDialog(Bitmap bitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme);
        View dialogView = getLayoutInflater().inflate(R.layout.player_choice_enter_name_item, null);
        EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        Button okayButton = dialogView.findViewById(R.id.okButton);

        nameEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        nameEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        disableSelectionMenu(nameEditText);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        nameEditText.requestFocus();
        nameEditText.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(nameEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);

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

    private void disableSelectionMenu(EditText editText) {
        ActionMode.Callback callback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        };

        editText.setCustomSelectionActionModeCallback(callback);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            editText.setCustomInsertionActionModeCallback(callback);
        }
        editText.setLongClickable(false);
        editText.setTextIsSelectable(false);
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


    private void setupPlayerRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        playerRecyclerView.setLayoutManager(layoutManager);
        playerRecyclerView.addItemDecoration(new SpaceItemDecoration(spacing));
        playerRecyclerView.setAdapter(playerListAdapter);
    }

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
            outRect.top = (parent.getChildAdapterPosition(view) < 3) ? spacing : 0;
        }
    }
}
