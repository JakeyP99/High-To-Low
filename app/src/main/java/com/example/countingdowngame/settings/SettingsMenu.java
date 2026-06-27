package com.example.countingdowngame.settings;

import static com.example.countingdowngame.wildCards.wildCardTypes.WildCardData.QUIZ_WILD_CARDS;
import static com.example.countingdowngame.wildCards.wildCardTypes.WildCardData.TASK_WILD_CARDS;
import static com.example.countingdowngame.wildCards.wildCardTypes.WildCardData.TRUTH_WILD_CARDS;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;

import com.example.countingdowngame.R;
import com.example.countingdowngame.mainActivity.MainActivityGame;
import com.example.countingdowngame.utils.ButtonUtilsActivity;
import com.example.countingdowngame.wildCards.WildCardProperties;

import pl.droidsonroids.gif.GifImageView;

public class SettingsMenu extends ButtonUtilsActivity {

    private GifImageView muteGif, soundGif;
    private View btnLimits, btnQuiz, btnContent, btnEvents;
    private Button btnSave;

    @Override
    protected void onResume() {
        super.onResume();
        boolean isMuted = getMuteSoundState();
        com.example.countingdowngame.audio.AudioManager.updateMuteButton(isMuted, muteGif, soundGif);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main_activity);

        initializeViews();
        setupAudioManagerForMuteButtons(muteGif, soundGif);
        setButtonListeners();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                gotoHomeScreen();
            }
        });
    }

    private void initializeViews() {
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);

        btnLimits = findViewById(R.id.btn_setting_limits);
        btnQuiz = findViewById(R.id.btn_setting_quiz);
        btnContent = findViewById(R.id.btn_setting_content);
        btnEvents = findViewById(R.id.btn_setting_events);
        btnSave = findViewById(R.id.btnContinueToGame);
    }

    private void setButtonListeners() {
        btnUtils.setButton(btnLimits, this::showLimitsDialog);
        btnUtils.setButton(btnQuiz, this::showQuizDialog);
        btnUtils.setButton(btnContent, this::showContentDialog);
        btnUtils.setButton(btnEvents, this::showEventsDialog);
        btnUtils.setButton(btnSave, this::saveAndContinue);
    }

    // ---------------- POPUP DIALOGS ----------------

    private void showLimitsDialog() {
        View v = inflate(R.layout.game_settings_limits_dialog);
        EditText editWildcards = v.findViewById(R.id.edittext_wildcard_amount);
        EditText editDrinks = v.findViewById(R.id.edittext_drink_amount);
        Button btnDone = v.findViewById(R.id.btn_close);

        GeneralSettingsLocalStore store = GeneralSettingsLocalStore.fromContext(this);
        editWildcards.setText(String.valueOf(store.playerWildCardCount()));
        editDrinks.setText(String.valueOf(store.totalDrinkAmount()));

        setupTextWatcher(editWildcards, 3);
        setupTextWatcher(editDrinks, 2);

        AlertDialog dialog = createDialog(v);
        btnUtils.setButton(btnDone, () -> {
            store.setPlayerWildCardCount(safeParseInt(editWildcards));
            store.setTotalDrinkAmount(safeParseInt(editDrinks));
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showQuizDialog() {
        View v = inflate(R.layout.game_settings_quiz_dialog);
        Button btnMulti = v.findViewById(R.id.button_multiChoice);
        Button btnShort = v.findViewById(R.id.button_nonMultiChoice);
        Button btnDone = v.findViewById(R.id.btn_close);

        GeneralSettingsLocalStore store = GeneralSettingsLocalStore.fromContext(this);
        btnMulti.setSelected(store.isMultiChoice());
        btnShort.setSelected(!store.isMultiChoice());

        btnMulti.setOnClickListener(view -> {
            btnMulti.setSelected(true);
            btnShort.setSelected(false);
            store.setIsMultiChoice(true);
        });

        btnShort.setOnClickListener(view -> {
            btnShort.setSelected(true);
            btnMulti.setSelected(false);
            store.setIsMultiChoice(false);
        });

        AlertDialog dialog = createDialog(v);
        btnUtils.setButton(btnDone, dialog::dismiss);
        dialog.show();
    }

    private void showContentDialog() {
        View v = inflate(R.layout.game_settings_card_pool_dialog);
        Button btnQuiz = v.findViewById(R.id.button_quiz_toggle);
        Button btnTask = v.findViewById(R.id.button_task_toggle);
        Button btnTruth = v.findViewById(R.id.button_truth_toggle);
        Button btnPower = v.findViewById(R.id.button_powerup_toggle);
        Button btnDone = v.findViewById(R.id.btn_close);

        GeneralSettingsLocalStore store = GeneralSettingsLocalStore.fromContext(this);
        btnQuiz.setSelected(store.isQuizActivated());
        btnTask.setSelected(store.isTaskActivated());
        btnTruth.setSelected(store.isTruthActivated());
        btnPower.setSelected(store.arePowerupsActivated());

        btnQuiz.setOnClickListener(view -> {
            btnQuiz.setSelected(!btnQuiz.isSelected());
            store.setIsQuizActivated(btnQuiz.isSelected());
            toggleWildCards(QUIZ_WILD_CARDS, btnQuiz.isSelected());
        });
        btnTask.setOnClickListener(view -> {
            btnTask.setSelected(!btnTask.isSelected());
            store.setIsTaskActivated(btnTask.isSelected());
            toggleWildCards(TASK_WILD_CARDS, btnTask.isSelected());
        });
        btnTruth.setOnClickListener(view -> {
            btnTruth.setSelected(!btnTruth.isSelected());
            store.setIsTruthActivated(btnTruth.isSelected());
            toggleWildCards(TRUTH_WILD_CARDS, btnTruth.isSelected());
        });
        btnPower.setOnClickListener(view -> {
            btnPower.setSelected(!btnPower.isSelected());
            store.setIsPowerupsActivated(btnPower.isSelected());
        });

        AlertDialog dialog = createDialog(v);
        btnUtils.setButton(btnDone, dialog::dismiss);
        dialog.show();
    }

    private void showEventsDialog() {
        View v = inflate(R.layout.game_settings_events_dialog);
        Button btnCatastrophe = v.findViewById(R.id.button_catastrophe_toggle);
        Button btnDone = v.findViewById(R.id.btn_close);

        GeneralSettingsLocalStore store = GeneralSettingsLocalStore.fromContext(this);
        btnCatastrophe.setSelected(store.isCatastrophesActivated());
        updateToggleText(btnCatastrophe);

        btnCatastrophe.setOnClickListener(view -> {
            btnCatastrophe.setSelected(!btnCatastrophe.isSelected());
            store.setIsCatastrophesActivated(btnCatastrophe.isSelected());
            updateToggleText(btnCatastrophe);
        });

        AlertDialog dialog = createDialog(v);
        btnUtils.setButton(btnDone, dialog::dismiss);
        dialog.show();
    }

    // ---------------- HELPERS ----------------

    private View inflate(int layoutId) {
        return LayoutInflater.from(this).inflate(layoutId, null);
    }

    private AlertDialog createDialog(View v) {
        return new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                .setView(v)
                .create();
    }

    private void updateToggleText(Button b) {
        b.setText(b.isSelected() ? "Enabled" : "Disabled");
    }

    private void toggleWildCards(WildCardProperties[] cards, boolean enabled) {
        for (WildCardProperties card : cards) card.setEnabled(enabled);
    }

    private int safeParseInt(EditText et) {
        try {
            return Integer.parseInt(et.getText().toString().trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private void setupTextWatcher(EditText editText, int maxLength) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (s.length() > maxLength) {
                    editText.setText(s.subSequence(0, maxLength));
                    editText.setSelection(maxLength);
                }
            }
        });
    }

    private void saveAndContinue() {
        int startingNumber = getIntent().getIntExtra("startingNumber", 0);
        Intent intent = new Intent(this, MainActivityGame.class);
        intent.putExtra("startingNumber", startingNumber);
        startActivity(intent);
    }
}
