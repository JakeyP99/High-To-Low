package com.example.countingdowngame.settings;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.example.countingdowngame.wildCards.wildCardTypes.WildCardData.QUIZ_WILD_CARDS;
import static com.example.countingdowngame.wildCards.wildCardTypes.WildCardData.TASK_WILD_CARDS;
import static com.example.countingdowngame.wildCards.wildCardTypes.WildCardData.TRUTH_WILD_CARDS;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;

import com.example.countingdowngame.R;
import com.example.countingdowngame.createPlayer.PlayerModelLocalStore;
import com.example.countingdowngame.mainActivity.MainActivityGame;
import com.example.countingdowngame.player.Player;
import com.example.countingdowngame.utils.ButtonUtilsActivity;
import com.example.countingdowngame.wildCards.WildCardProperties;
import com.example.countingdowngame.wildCards.WildCardType;
import com.example.countingdowngame.wildCards.wildCardTypes.WildCardsAdapter;

import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class SettingsMenu extends ButtonUtilsActivity implements View.OnClickListener {

    //-----------------------------------------------------Initialize---------------------------------------------------//
    private EditText wildcardPerPlayerEditText;
    private EditText totalDrinksEditText;
    private Button button_multiChoice;
    private Button button_nonMultiChoice;

    private Button button_quiz_toggle;
    private Button button_task_toggle;
    private Button button_truth_toggle;
    private Button btnProgressToGame;



    //-----------------------------------------------------On Pause---------------------------------------------------//


    public SettingsMenu() {
        // Default constructor with no arguments
    }

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


    //-----------------------------------------------------On Create---------------------------------------------------//

    //-----------------------------------------------------Initialize Views---------------------------------------------------//

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

        wildcardPerPlayerEditText = findViewById(R.id.edittext_wildcard_amount);
        totalDrinksEditText = findViewById(R.id.edittext_drink_amount);
    }

    private void setupTextWatcher(EditText editText, int maxLength, Runnable validationAction) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateInput(editText, maxLength);
                validationAction.run();
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

    private boolean isValidInput(String input, int maxLength, int minValue, int maxValue) {
        if (input.length() > maxLength) {
            input = input.substring(0, maxLength);
        }
        if (input.length() < minValue) {
            input = "";
        }
        try {
            int value = Integer.parseInt(input);
            return value >= minValue && value <= maxValue;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void isValidTotalDrinkAmount() {
        isValidInput(
                totalDrinksEditText.getText().toString().trim(),
                2,
                1,
                20
        );
    }

    private void isValidWildCardAmount() {
        isValidInput(
                wildcardPerPlayerEditText.getText().toString().trim(),
                3,
                0,
                100
        );
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.button_multiChoice) {
            boolean selected = !button_multiChoice.isSelected();
            button_multiChoice.setSelected(selected);
            button_nonMultiChoice.setSelected(!selected);
            return;
        }

        if (id == R.id.button_nonMultiChoice) {
            boolean selected = !button_nonMultiChoice.isSelected();
            button_nonMultiChoice.setSelected(selected);
            button_multiChoice.setSelected(!selected);
            return;
        }

        if (id == R.id.button_quiz_toggle) {
            boolean selected = !button_quiz_toggle.isSelected();
            button_quiz_toggle.setSelected(selected);
            toggleWildCards(QUIZ_WILD_CARDS, selected);
            return;
        }

        if (id == R.id.button_task_toggle) {
            boolean selected = !button_task_toggle.isSelected();
            button_task_toggle.setSelected(selected);
            toggleWildCards(TASK_WILD_CARDS, selected);
            return;
        }

        if (id == R.id.button_truth_toggle) {
            boolean selected = !button_truth_toggle.isSelected();
            button_truth_toggle.setSelected(selected);
            toggleWildCards(TRUTH_WILD_CARDS, selected);
        }

        savePreferences();
    }

    private void toggleWildCards(WildCardProperties[] cards, boolean enabled) {
        for (WildCardProperties card : cards) {
            card.setEnabled(enabled);
        }
    }

    //-----------------------------------------------------Wild Card Choices---------------------------------------------------//
    private void setButtonListeners() {
        button_multiChoice.setOnClickListener(this);
        button_nonMultiChoice.setOnClickListener(this);

        button_quiz_toggle.setOnClickListener(this);
        button_task_toggle.setOnClickListener(this);
        button_truth_toggle.setOnClickListener(this);

        btnUtils.setButton(btnProgressToGame, () -> {

            String wildCardAmountInput = wildcardPerPlayerEditText.getText().toString().trim();
            String totalDrinkAmountInput = totalDrinksEditText.getText().toString().trim();

            boolean isWildCardAmountValid = isValidInput(wildCardAmountInput, 3, 0, 100);
            boolean isTotalDrinkAmountValid = isValidInput(totalDrinkAmountInput, 2, 1, 20);

            if (isWildCardAmountValid && isTotalDrinkAmountValid) {
                savePreferences();
                goToClassicGameWithExtras(Integer.parseInt(totalDrinkAmountInput));
            } else {
                if (!isWildCardAmountValid) {
                    if (wildCardAmountInput.isEmpty()) {
                        wildcardPerPlayerEditText.setText("0");
                        savePreferences();
                        goToClassicGameWithExtras(Integer.parseInt(totalDrinkAmountInput));
                    } else {
                        StyleableToast.makeText(getApplicationContext(), "Please enter a wildcard quantity between 0 and 100", R.style.newToast).show();
                    }
                }

                if (!isTotalDrinkAmountValid) {
                    if (totalDrinkAmountInput.isEmpty()) {
                        totalDrinksEditText.setText("0");
                        savePreferences();
                        goToClassicGameWithExtras(Integer.parseInt(totalDrinkAmountInput));
                    } else {
                        StyleableToast.makeText(getApplicationContext(), "Please enter a total drink limit between 1 and 20", R.style.newToast).show();
                    }
                }
            }
        });
    }

    private void toggleQuizSettingsButtons(Button selectedButton, Button unselectedButton, boolean isSelected) {
        selectedButton.setSelected(isSelected);
        unselectedButton.setSelected(!isSelected);
    }



    private void toggleWildCardButton(Button button, WildCardsAdapter adapter, boolean isSelected) {
        button.setSelected(isSelected);

        if (adapter != null) {
            WildCardProperties[] wildCards = adapter.getWildCards();
            for (WildCardProperties wildcard : wildCards) {
                wildcard.setEnabled(isSelected);
            }
            adapter.setWildCards(wildCards);
            adapter.notifyDataSetChanged();
            adapter.saveWildCardProbabilitiesToStorage(wildCards);
        }
    }



    //-----------------------------------------------------Load and Save Preferences---------------------------------------------------//

    // Inside WildCardSettings or any other settings activity
    private void goToClassicGameWithExtras(int totalDrinkNumber) {
        savePreferences();
        GeneralSettingsLocalStore store = GeneralSettingsLocalStore.fromContext(this);
        int wildCardCount = store.playerWildCardCount();
        Log.d(TAG, "Wild Card Count: " + wildCardCount);


        // Retrieve the extras passed from NumberChoice activity
        int startingNumber = getIntent().getIntExtra("startingNumber", 0); // 0 is the default value if the extra is not found
        // Create an Intent to start the main game activity
        Intent intent = new Intent(this, MainActivityGame.class);

        // Pass the extras to the main game activity
        intent.putExtra("startingNumber", startingNumber);
        intent.putExtra("totalDrinkNumber", totalDrinkNumber);

        // Start the main game activity
        startActivity(intent);
    }


    private void loadPreferences() {

        GeneralSettingsLocalStore store = GeneralSettingsLocalStore.fromContext(this);

        wildcardPerPlayerEditText.setText(String.valueOf(store.playerWildCardCount()));
        totalDrinksEditText.setText(String.valueOf(store.totalDrinkAmount()));

        boolean multiChoice = store.isMultiChoice();
        button_multiChoice.setSelected(multiChoice);
        button_nonMultiChoice.setSelected(!multiChoice);

        button_quiz_toggle.setSelected(store.isQuizActivated());
        button_task_toggle.setSelected(store.isTaskActivated());
        button_truth_toggle.setSelected(store.isTruthActivated());

        toggleWildCards(QUIZ_WILD_CARDS, button_quiz_toggle.isSelected());
        toggleWildCards(TASK_WILD_CARDS, button_task_toggle.isSelected());
        toggleWildCards(TRUTH_WILD_CARDS, button_truth_toggle.isSelected());
    }


    private void savePreferences() {

        GeneralSettingsLocalStore store = GeneralSettingsLocalStore.fromContext(this);

        store.setPlayerWildCardCount(
                Integer.parseInt(wildcardPerPlayerEditText.getText().toString())
        );

        store.setTotalDrinkAmount(
                Integer.parseInt(totalDrinksEditText.getText().toString())
        );

        store.setIsMultiChoice(button_multiChoice.isSelected());

        store.setIsQuizActivated(button_quiz_toggle.isSelected());
        store.setIsTaskActivated(button_task_toggle.isSelected());
        store.setIsTruthActivated(button_truth_toggle.isSelected());
    }

}