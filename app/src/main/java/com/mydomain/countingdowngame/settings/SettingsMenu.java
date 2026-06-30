package com.mydomain.countingdowngame.settings;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;

import com.mydomain.countingdowngame.R;
import com.mydomain.countingdowngame.utils.ButtonUtilsActivity;
import com.mydomain.countingdowngame.wildCards.WildCardProperties;
import com.mydomain.countingdowngame.wildCards.wildCardTypes.WildCardRepository;

import pl.droidsonroids.gif.GifImageView;

public class SettingsMenu extends ButtonUtilsActivity {

    private GifImageView muteGif, soundGif;
    private View btnLimits, btnQuiz, btnContent, btnEvents;

    @Override
    protected void onResume() {
        super.onResume();
        boolean isMuted = getMuteSoundState();
        com.mydomain.countingdowngame.audio.AudioManager.updateMuteButton(isMuted, muteGif, soundGif);
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
    }

    private void setButtonListeners() {
        btnUtils.setButton(btnLimits, this::showLimitsDialog);
        btnUtils.setButton(btnQuiz, this::showQuizDialog);
        btnUtils.setButton(btnContent, this::showContentDialog);
        btnUtils.setButton(btnEvents, this::showEventsDialog);
    }

    // ---------------- POPUP DIALOGS ----------------

    private void showLimitsDialog() {
        View v = inflate(R.layout.game_settings_limits_dialog);
        EditText editWildcards = v.findViewById(R.id.edittext_wildcard_amount);
        EditText editDrinks = v.findViewById(R.id.edittext_drink_amount);

        GeneralSettingsLocalStore store = GeneralSettingsLocalStore.fromContext(this);
        editWildcards.setText(String.valueOf(store.playerWildCardCount()));
        editDrinks.setText(String.valueOf(store.totalDrinkAmount()));

        setupTextWatcher(editWildcards, 3);
        setupTextWatcher(editDrinks, 2);

        AlertDialog dialog = createDialog(v);
        dialog.setOnDismissListener(d -> {
            store.setPlayerWildCardCount(safeParseInt(editWildcards));
            store.setTotalDrinkAmount(safeParseInt(editDrinks));
        });
        dialog.show();
    }

    private void showQuizDialog() {
        View v = inflate(R.layout.game_settings_quiz_dialog);
        Button btnMulti = v.findViewById(R.id.button_multiChoice);
        Button btnShort = v.findViewById(R.id.button_nonMultiChoice);

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

        createDialog(v).show();
    }

    private void showContentDialog() {
        View v = inflate(R.layout.game_settings_card_pool_dialog);
        Button btnQuiz = v.findViewById(R.id.button_quiz_toggle);
        Button btnTask = v.findViewById(R.id.button_task_toggle);
        Button btnTruth = v.findViewById(R.id.button_truth_toggle);
        Button btnPower = v.findViewById(R.id.button_powerup_toggle);

        GeneralSettingsLocalStore store = GeneralSettingsLocalStore.fromContext(this);
        btnQuiz.setSelected(store.isQuizActivated());
        btnTask.setSelected(store.isTaskActivated());
        btnTruth.setSelected(store.isTruthActivated());
        btnPower.setSelected(store.arePowerupsActivated());

        btnQuiz.setOnClickListener(view -> {
            btnQuiz.setSelected(!btnQuiz.isSelected());
            store.setIsQuizActivated(btnQuiz.isSelected());
            toggleWildCards("Quiz", btnQuiz.isSelected());
        });
        btnTask.setOnClickListener(view -> {
            btnTask.setSelected(!btnTask.isSelected());
            store.setIsTaskActivated(btnTask.isSelected());
            toggleWildCards("Task", btnTask.isSelected());
        });
        btnTruth.setOnClickListener(view -> {
            btnTruth.setSelected(!btnTruth.isSelected());
            store.setIsTruthActivated(btnTruth.isSelected());
            toggleWildCards("Truth", btnTruth.isSelected());
        });
        btnPower.setOnClickListener(view -> {
            btnPower.setSelected(!btnPower.isSelected());
            store.setIsPowerupsActivated(btnPower.isSelected());
        });

        createDialog(v).show();
    }

    private void showEventsDialog() {
        View v = inflate(R.layout.game_settings_events_dialog);
        Button btnCatastrophe = v.findViewById(R.id.button_catastrophe_toggle);

        GeneralSettingsLocalStore store = GeneralSettingsLocalStore.fromContext(this);
        btnCatastrophe.setSelected(store.isCatastrophesActivated());
        updateToggleText(btnCatastrophe);

        btnCatastrophe.setOnClickListener(view -> {
            btnCatastrophe.setSelected(!btnCatastrophe.isSelected());
            store.setIsCatastrophesActivated(btnCatastrophe.isSelected());
            updateToggleText(btnCatastrophe);
        });

        createDialog(v).show();
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

    private void toggleWildCards(String type, boolean enabled) {
        WildCardRepository repository = new WildCardRepository(this);
        WildCardProperties[] cards;
        String key;

        switch (type) {
            case "Quiz":
                cards = repository.loadQuizCards();
                key = "QuizPrefs";
                break;
            case "Task":
                cards = repository.loadTaskCards();
                key = "TaskPrefs";
                break;
            case "Truth":
                cards = repository.loadTruthCards();
                key = "TruthPrefs";
                break;
            default:
                return;
        }

        WildCardSettingsLocalStore prefs = WildCardSettingsLocalStore.fromContext(this, key);
        for (int i = 0; i < cards.length; i++) {
            prefs.setWildcardEnabled(i, enabled);
        }
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
}
