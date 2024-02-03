package com.example.countingdowngame.mainActivity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.example.countingdowngame.wildCards.wildCardTypes.WildCardData.EXTRA_WILD_CARDS;
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

import androidx.core.content.res.ResourcesCompat;

import com.example.countingdowngame.R;
import com.example.countingdowngame.settings.GeneralSettingsLocalStore;
import com.example.countingdowngame.utils.ButtonUtilsActivity;
import com.example.countingdowngame.wildCards.WildCardProperties;
import com.example.countingdowngame.wildCards.WildCardType;
import com.example.countingdowngame.wildCards.wildCardTypes.ExtrasWildCardsAdapter;
import com.example.countingdowngame.wildCards.wildCardTypes.QuizWildCardsAdapter;
import com.example.countingdowngame.wildCards.wildCardTypes.TaskWildCardsAdapter;
import com.example.countingdowngame.wildCards.wildCardTypes.TruthWildCardsAdapter;
import com.example.countingdowngame.wildCards.wildCardTypes.WildCardsAdapter;

import io.github.muddz.styleabletoast.StyleableToast;

public class inGameSettings extends ButtonUtilsActivity implements View.OnClickListener {

    //-----------------------------------------------------Initialize---------------------------------------------------//
    private EditText wildcardPerPlayerEditText;
    private EditText totalDrinksEditText;
    private Button button_multiChoice;
    private Button button_nonMultiChoice;

    private Button button_quiz_toggle;
    private Button button_task_toggle;
    private Button button_truth_toggle;
    private Button button_extras_toggle;

    private Drawable buttonHighlightDrawable;
    private Drawable outlineForButton;
    private Button btnProgressToGame;

    private QuizWildCardsAdapter quizWildCardsAdapter;
    private TaskWildCardsAdapter taskWildCardsAdapter;
    private TruthWildCardsAdapter truthWildCardsAdapter;
    private ExtrasWildCardsAdapter extrasWildCardsAdapter;


    //-----------------------------------------------------On Pause---------------------------------------------------//

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }

    public inGameSettings() {
        // Default constructor with no arguments
    }

    @Override
    public void onBackPressed() {
        String wildCardAmountInput = wildcardPerPlayerEditText.getText().toString().trim();
        String totalDrinkAmountInput = totalDrinksEditText.getText().toString().trim();

        boolean isWildCardValid = isValidInput(wildCardAmountInput, 3, 0, 100);
        boolean isTotalDrinkValid = isValidInput(totalDrinkAmountInput, 2, 1, 20);

        if (!isWildCardValid || !isTotalDrinkValid) {
            if (!isWildCardValid) {
                if (wildCardAmountInput.isEmpty()) {
                    wildcardPerPlayerEditText.setText("1");
                } else {
                    StyleableToast.makeText(getApplicationContext(), "Please enter a number between 0 and 100", R.style.newToast).show();
                }
            }

            if (!isTotalDrinkValid) {
                totalDrinksEditText.setText("1");

            }

        }
        savePreferences();
        super.onBackPressed();
    }



    //-----------------------------------------------------On Create---------------------------------------------------//


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_ingame);
        initializeViews();
        loadPreferences();
        setButtonListeners();
    }


    //-----------------------------------------------------Initialize Views---------------------------------------------------//

    private void initializeViews() {
        // Initialize views
        buttonHighlightDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.buttonhighlight, null);
        outlineForButton = ResourcesCompat.getDrawable(getResources(), R.drawable.outlineforbutton, null);
        btnProgressToGame = findViewById(R.id.btnContinueToGame);
        button_multiChoice = findViewById(R.id.button_multiChoice);
        button_nonMultiChoice = findViewById(R.id.button_nonMultiChoice);

        quizWildCardsAdapter = new QuizWildCardsAdapter(QUIZ_WILD_CARDS, this, WildCardType.QUIZ);
        taskWildCardsAdapter = new TaskWildCardsAdapter(TASK_WILD_CARDS, this, WildCardType.TASK);
        truthWildCardsAdapter = new TruthWildCardsAdapter(TRUTH_WILD_CARDS, this, WildCardType.TRUTH);
        extrasWildCardsAdapter = new ExtrasWildCardsAdapter(EXTRA_WILD_CARDS, this, WildCardType.EXTRAS);

        button_quiz_toggle = findViewById(R.id.button_quiz_toggle);
        button_truth_toggle = findViewById(R.id.button_truth_toggle);
        button_task_toggle = findViewById(R.id.button_task_toggle);
        button_extras_toggle = findViewById(R.id.button_extras_toggle);

        // Find and set up EditTexts
        wildcardPerPlayerEditText = findViewById(R.id.edittext_wildcard_amount);
        totalDrinksEditText = findViewById(R.id.edittext_drink_amount);

        // Set up TextWatchers for EditTexts
        setupTextWatcher(wildcardPerPlayerEditText, 3, this::isValidWildCardAmount);
        setupTextWatcher(totalDrinksEditText, 2, this::isValidTotalDrinkAmount);
    }

    private void setupTextWatcher(EditText editText, int maxLength, Runnable validationAction) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed
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
            // Handle the scenario where input is empty or shorter than minValue
            input = ""; // Set input to an empty string or handle as per your logic
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
                2, // Max length
                1, // Minimum value
                20 // Maximum value
        );// Handle invalid total drink amount
    }

    private void isValidWildCardAmount() {
        isValidInput(
                wildcardPerPlayerEditText.getText().toString().trim(),
                3, // Max length
                0,  // Minimum value
                100 // Maximum value
        );// Handle invalid wild card amount
    }


    @Override
    public void onClick(View view) {
        int viewId = view.getId(); // Store the view ID in a variable

        switch (viewId) {
            case R.id.button_multiChoice:
                boolean isMultiChoiceSelected = !button_multiChoice.isSelected();
                toggleButton(button_multiChoice, button_nonMultiChoice, isMultiChoiceSelected);
                break;

            case R.id.button_quiz_toggle:
                toggleWildCardButton(button_quiz_toggle, "Quiz", quizWildCardsAdapter);
                break;

            case R.id.button_task_toggle:
                toggleWildCardButton(button_task_toggle, "Task", taskWildCardsAdapter);
                break;

            case R.id.button_truth_toggle:
                toggleWildCardButton(button_truth_toggle, "Truth", truthWildCardsAdapter);
                break;

            case R.id.button_extras_toggle:
                toggleWildCardButton(button_extras_toggle, "Extras", extrasWildCardsAdapter);
                break;
        }

        savePreferences();
    }



    private void setButtonListeners() {
        button_multiChoice.setOnClickListener(this);
        button_nonMultiChoice.setOnClickListener(this);

        button_quiz_toggle.setOnClickListener(this);
        button_task_toggle.setOnClickListener(this);
        button_truth_toggle.setOnClickListener(this);
        button_extras_toggle.setOnClickListener(this);

        btnUtils.setButton(btnProgressToGame, () -> {

            String wildCardAmountInput = wildcardPerPlayerEditText.getText().toString().trim();
            String totalDrinkAmountInput = totalDrinksEditText.getText().toString().trim();

            boolean isWildCardAmountValid = isValidInput(wildCardAmountInput, 3, 0, 100);
            boolean isTotalDrinkAmountValid = isValidInput(totalDrinkAmountInput, 2, 1, 20);

            if (isWildCardAmountValid && isTotalDrinkAmountValid) {
                savePreferences();
                goToMainGameWithExtra(Integer.parseInt(totalDrinkAmountInput));
            } else {
                if (!isWildCardAmountValid) {
                    if (wildCardAmountInput.isEmpty()) {
                        wildcardPerPlayerEditText.setText("0");
                        savePreferences();
                        goToMainGameWithExtra(Integer.parseInt(totalDrinkAmountInput));
                    } else {
                        StyleableToast.makeText(getApplicationContext(), "Please enter a wildcard quantity between 0 and 100", R.style.newToast).show();
                    }
                }

                if (!isTotalDrinkAmountValid) {
                    if (totalDrinkAmountInput.isEmpty()) {
                        totalDrinksEditText.setText("0");
                        savePreferences();
                        goToMainGameWithExtra(Integer.parseInt(totalDrinkAmountInput));
                    } else {
                        StyleableToast.makeText(getApplicationContext(), "Please enter a total drink limit between 1 and 20", R.style.newToast).show();
                    }
                }
            }
        });
    }


    //-----------------------------------------------------Wild Card Choices---------------------------------------------------//


    private void toggleWildCards(WildCardsAdapter adapter, boolean isSelected) {
        if (adapter != null) {
            WildCardProperties[] wildCards = adapter.getWildCards();
            for (WildCardProperties wildcard : wildCards) {
                wildcard.setEnabled(isSelected);
            }
            adapter.setWildCards(wildCards);
            adapter.notifyDataSetChanged();
            adapter.saveWildCardProbabilitiesToStorage(wildCards);
        } else {
            Log.e("Adapter", "Adapter is null");
        }
    }

    private void toggleButton(Button selectedButton, Button unselectedButton, boolean isSelected) {
        selectedButton.setSelected(isSelected);
        selectedButton.setBackground(isSelected ? buttonHighlightDrawable : outlineForButton);

        unselectedButton.setSelected(!isSelected);
        unselectedButton.setBackground(!isSelected ? buttonHighlightDrawable : outlineForButton);

        // Set the toggle state for the selected button
        selectedButton.setSelected(isSelected);
        selectedButton.setBackground(isSelected ? buttonHighlightDrawable : outlineForButton);
    }


    private void toggleWildCardButton(Button button, String targetActivity, WildCardsAdapter adapter) {
        Log.d(TAG, "toggleWildCardButton: " + targetActivity + " is toggled");
        boolean isSelected = !button.isSelected();
        button.setSelected(isSelected);
        button.setBackground(isSelected ? buttonHighlightDrawable : outlineForButton);

        toggleWildCards(adapter, isSelected);
    }

    private void toggleButtonActivation(Button button, boolean isActivated) {
        button.setBackground(isActivated ? buttonHighlightDrawable : outlineForButton);
    }

    //-----------------------------------------------------Load and Save Preferences---------------------------------------------------//

    // Inside WildCardSettings or any other settings activity
    private void goToMainGameWithExtra(int totalDrinkNumber) {

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
        // Load wild card and total drink amounts
        int loadWildCardAmount = GeneralSettingsLocalStore.fromContext(this).playerWildCardCount();
        wildcardPerPlayerEditText.setText(String.valueOf(loadWildCardAmount));

        int loadTotalDrinkAmount = GeneralSettingsLocalStore.fromContext(this).totalDrinkAmount();
        totalDrinksEditText.setText(String.valueOf(loadTotalDrinkAmount));

        // Load multi-choice status and toggle buttons accordingly
        boolean isMultiChoiceSelected = GeneralSettingsLocalStore.fromContext(this).isMultiChoice();
        toggleButton(button_multiChoice, button_nonMultiChoice, isMultiChoiceSelected);


        // Load activation status for each wild card type and toggle buttons accordingly

        toggleButtonActivation(button_quiz_toggle, GeneralSettingsLocalStore.fromContext(this).isQuizActivated());
        toggleButtonActivation(button_task_toggle, GeneralSettingsLocalStore.fromContext(this).isTaskActivated());
        toggleButtonActivation(button_truth_toggle, GeneralSettingsLocalStore.fromContext(this).isTruthActivated());
        toggleButtonActivation(button_extras_toggle, GeneralSettingsLocalStore.fromContext(this).isExtrasActivated());
    }


    private void savePreferences() {
        int wildCardAmountSetInSettings = Integer.parseInt(wildcardPerPlayerEditText.getText().toString());
        GeneralSettingsLocalStore.fromContext(this).setPlayerWildCardCount(wildCardAmountSetInSettings);

        int totalDrinkAmountSetInSettings = Integer.parseInt(totalDrinksEditText.getText().toString());
        GeneralSettingsLocalStore.fromContext(this).setTotalDrinkAmount(totalDrinkAmountSetInSettings);

        GeneralSettingsLocalStore store = GeneralSettingsLocalStore.fromContext(this);
        store.setIsMultiChoice(button_multiChoice.isSelected());

        // Save the selected state of other buttons
        store.setIsQuizActivated(button_quiz_toggle.isSelected());
        store.setIsTaskActivated(button_task_toggle.isSelected());
        store.setIsTruthActivated(button_truth_toggle.isSelected());
        store.setIsExtrasActivated(button_extras_toggle.isSelected());
    }

}