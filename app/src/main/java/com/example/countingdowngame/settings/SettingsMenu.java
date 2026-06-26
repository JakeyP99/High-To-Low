package com.example.countingdowngame.settings;

import static com.example.countingdowngame.wildCards.wildCardTypes.WildCardData.QUIZ_WILD_CARDS;
import static com.example.countingdowngame.wildCards.wildCardTypes.WildCardData.TASK_WILD_CARDS;
import static com.example.countingdowngame.wildCards.wildCardTypes.WildCardData.TRUTH_WILD_CARDS;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;

import com.example.countingdowngame.R;
import com.example.countingdowngame.mainActivity.MainActivityGame;
import com.example.countingdowngame.utils.ButtonUtilsActivity;
import com.example.countingdowngame.wildCards.WildCardProperties;

import io.github.muddz.styleabletoast.StyleableToast;

public class SettingsMenu extends ButtonUtilsActivity implements View.OnClickListener {

    private EditText wildcardPerPlayerEditText;
    private EditText totalDrinksEditText;

    private Button button_multiChoice;
    private Button button_nonMultiChoice;

    private Button button_quiz_toggle;
    private Button button_task_toggle;
    private Button button_truth_toggle;
    private Button button_powerup_toggle;
    private Button btnProgressToGame;

    private boolean isLoading = false;

    public SettingsMenu() {}

    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences();
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main_activity);

        initializeViews();
        loadPreferences();
        setButtonListeners();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                savePreferences();
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void initializeViews() {

        btnProgressToGame = findViewById(R.id.btnContinueToGame);

        button_multiChoice = findViewById(R.id.button_multiChoice);
        button_nonMultiChoice = findViewById(R.id.button_nonMultiChoice);

        button_quiz_toggle = findViewById(R.id.button_quiz_toggle);
        button_task_toggle = findViewById(R.id.button_task_toggle);
        button_truth_toggle = findViewById(R.id.button_truth_toggle);
        button_powerup_toggle = findViewById(R.id.button_powerup_toggle);

        wildcardPerPlayerEditText = findViewById(R.id.edittext_wildcard_amount);
        totalDrinksEditText = findViewById(R.id.edittext_drink_amount);

        setupTextWatcher(wildcardPerPlayerEditText, 3);
        setupTextWatcher(totalDrinksEditText, 2);
    }

    private void setupTextWatcher(EditText editText, int maxLength) {
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if (isLoading) return;

                validateInput(editText, maxLength);
            }
        });
    }

    private void validateInput(EditText editText, int maxLength) {
        String input = editText.getText().toString().trim();

        if (input.length() > maxLength) {
            input = input.substring(0, maxLength);
            editText.setText(input);
            editText.setSelection(input.length());
        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.button_multiChoice) {
            boolean selected = !button_multiChoice.isSelected();
            button_multiChoice.setSelected(selected);
            button_nonMultiChoice.setSelected(!selected);

        } else if (id == R.id.button_nonMultiChoice) {
            boolean selected = !button_nonMultiChoice.isSelected();
            button_nonMultiChoice.setSelected(selected);
            button_multiChoice.setSelected(!selected);

        } else if (id == R.id.button_quiz_toggle) {
            button_quiz_toggle.setSelected(!button_quiz_toggle.isSelected());
            toggleWildCards(QUIZ_WILD_CARDS, button_quiz_toggle.isSelected());

        } else if (id == R.id.button_task_toggle) {
            button_task_toggle.setSelected(!button_task_toggle.isSelected());
            toggleWildCards(TASK_WILD_CARDS, button_task_toggle.isSelected());

        } else if (id == R.id.button_truth_toggle) {
            button_truth_toggle.setSelected(!button_truth_toggle.isSelected());
            toggleWildCards(TRUTH_WILD_CARDS, button_truth_toggle.isSelected());

        } else if (id == R.id.button_powerup_toggle) {
            button_powerup_toggle.setSelected(!button_powerup_toggle.isSelected());
        }

        savePreferences();
    }

    private void toggleWildCards(WildCardProperties[] cards, boolean enabled) {
        for (WildCardProperties card : cards) {
            card.setEnabled(enabled);
        }
    }

    private void setButtonListeners() {

        button_multiChoice.setOnClickListener(this);
        button_nonMultiChoice.setOnClickListener(this);

        button_quiz_toggle.setOnClickListener(this);
        button_task_toggle.setOnClickListener(this);
        button_truth_toggle.setOnClickListener(this);
        button_powerup_toggle.setOnClickListener(this);

        btnUtils.setButton(btnProgressToGame, () -> {

            int wildCardAmount = safeParseInt(wildcardPerPlayerEditText);
            int totalDrinkAmount = safeParseInt(totalDrinksEditText);

            if (totalDrinkAmount < 1 || totalDrinkAmount > 20) {
                StyleableToast.makeText(this,
                        "Total drinks must be 1–20",
                        R.style.newToast).show();
                return;
            }

            if (wildCardAmount < 0 || wildCardAmount > 100) {
                StyleableToast.makeText(this,
                        "Wildcards must be 0–100",
                        R.style.newToast).show();
                return;
            }

            savePreferences();
            goToClassicGameWithExtras(totalDrinkAmount);
        });
    }

    private void goToClassicGameWithExtras(int totalDrinkNumber) {

        savePreferences();

        int startingNumber = getIntent().getIntExtra("startingNumber", 0);

        Intent intent = new Intent(this, MainActivityGame.class);
        intent.putExtra("startingNumber", startingNumber);
        intent.putExtra("totalDrinkNumber", totalDrinkNumber);

        startActivity(intent);
    }

    private void loadPreferences() {

        isLoading = true;

        GeneralSettingsLocalStore store = GeneralSettingsLocalStore.fromContext(this);

        wildcardPerPlayerEditText.setText(String.valueOf(store.playerWildCardCount()));
        totalDrinksEditText.setText(String.valueOf(store.totalDrinkAmount()));

        boolean multiChoice = store.isMultiChoice();
        button_multiChoice.setSelected(multiChoice);
        button_nonMultiChoice.setSelected(!multiChoice);

        button_quiz_toggle.setSelected(store.isQuizActivated());
        button_task_toggle.setSelected(store.isTaskActivated());
        button_truth_toggle.setSelected(store.isTruthActivated());
        button_powerup_toggle.setSelected(store.arePowerupsActivated());

        toggleWildCards(QUIZ_WILD_CARDS, button_quiz_toggle.isSelected());
        toggleWildCards(TASK_WILD_CARDS, button_task_toggle.isSelected());
        toggleWildCards(TRUTH_WILD_CARDS, button_truth_toggle.isSelected());

        isLoading = false;
    }

    private int safeParseInt(EditText editText) {
        String value = editText.getText().toString().trim();
        if (value.isEmpty()) return 0;

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void savePreferences() {

        GeneralSettingsLocalStore store = GeneralSettingsLocalStore.fromContext(this);

        store.setPlayerWildCardCount(safeParseInt(wildcardPerPlayerEditText));
        store.setTotalDrinkAmount(safeParseInt(totalDrinksEditText));

        store.setIsMultiChoice(button_multiChoice.isSelected());
        store.setIsQuizActivated(button_quiz_toggle.isSelected());
        store.setIsTaskActivated(button_task_toggle.isSelected());
        store.setIsTruthActivated(button_truth_toggle.isSelected());
        store.setIsPowerupsActivated(button_powerup_toggle.isSelected());
    }
}