package com.example.countingdowngame.settings;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.res.ResourcesCompat;

import com.example.countingdowngame.R;
import com.example.countingdowngame.mainActivity.MainActivityGame;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import io.github.muddz.styleabletoast.StyleableToast;

public class WildCardSettings extends ButtonUtilsActivity implements View.OnClickListener {

    //-----------------------------------------------------Initialize---------------------------------------------------//
    private EditText wildcardPerPlayerEditText;
    private EditText totalDrinksEditText;
    private Button button_multiChoice;
    private Button button_nonMultiChoice;
    private Drawable buttonHighlightDrawable;
    private Drawable outlineForButton;
    private Button btnProgressToGame;


    //-----------------------------------------------------On Pause---------------------------------------------------//

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }


    @Override
    public void onBackPressed() {
        String wildCardAmountInput = wildcardPerPlayerEditText.getText().toString().trim();
        String totalDrinkAmountInput = totalDrinksEditText.getText().toString().trim();

        boolean isWildCardValid = isValidInput(wildCardAmountInput, 3, 100);
        boolean isTotalDrinkValid = isValidInput(totalDrinkAmountInput, 2, 20);

        if (isWildCardValid && isTotalDrinkValid) {
            savePreferences();
            super.onBackPressed();
        } else {
            if (!isWildCardValid) {
                if (wildCardAmountInput.isEmpty()) {
                    wildcardPerPlayerEditText.setText("0");
                    savePreferences();
                    super.onBackPressed();
                } else {
                    StyleableToast.makeText(getApplicationContext(), "Please enter a number between 0 and 100", R.style.newToast).show();
                }
            }

            if (!isTotalDrinkValid) {
                if (totalDrinkAmountInput.isEmpty()) {
                    totalDrinksEditText.setText("0");
                    savePreferences();
                    super.onBackPressed();
                } else {
                    StyleableToast.makeText(getApplicationContext(), "Please enter a number between 0 and 20", R.style.newToast).show();
                }
            }
        }
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

    private boolean isValidInput(String input, int maxLength, int maxValue) {
        if (input.length() > maxLength) {
            input = input.substring(0, maxLength);
        }

        try {
            int value = Integer.parseInt(input);
            return value >= 0 && value <= maxValue;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void isValidTotalDrinkAmount() {
        if (!isValidInput(
                totalDrinksEditText.getText().toString().trim(),
                2, // Max length
                // Minimum value
                20 // Maximum value
        )) {
            // Handle invalid total drink amount
        }
    }

    private void isValidWildCardAmount() {
        isValidInput(
                wildcardPerPlayerEditText.getText().toString().trim(),
                3, // Max length
                // Minimum value
                100 // Maximum value
        );// Handle invalid wild card amount
    }


    @Override
    public void onClick(View view) {
        int viewId = view.getId(); // Store the view ID in a variable

        if (viewId == R.id.button_multiChoice) {
            toggleButton(button_multiChoice, button_nonMultiChoice);
        } else if (viewId == R.id.button_nonMultiChoice) {
            toggleButton(button_nonMultiChoice, button_multiChoice);
        }
        savePreferences();
    }


    private void setButtonListeners() {
        button_multiChoice.setOnClickListener(this);
        button_nonMultiChoice.setOnClickListener(this);

        btnUtils.setButton(btnProgressToGame, () -> {

            String wildCardAmountInput = wildcardPerPlayerEditText.getText().toString().trim();
            String totalDrinkAmountInput = totalDrinksEditText.getText().toString().trim();

            boolean isWildCardAmountValid = isValidInput(wildCardAmountInput, 3, 100);
            boolean isTotalDrinkAmountValid = isValidInput(totalDrinkAmountInput, 2, 20);

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
                        StyleableToast.makeText(getApplicationContext(), "Please enter a number between 0 and 100", R.style.newToast).show();
                    }
                }

                if (!isTotalDrinkAmountValid) {
                    if (totalDrinkAmountInput.isEmpty()) {
                        totalDrinksEditText.setText("0");
                        savePreferences();
                        goToMainGameWithExtra(Integer.parseInt(totalDrinkAmountInput));
                    } else {
                        StyleableToast.makeText(getApplicationContext(), "Please enter a number between 0 and 20", R.style.newToast).show();
                    }
                }
            }
        });
    }

    private void toggleButton(Button selectedButton, Button unselectedButton) {
        boolean isSelected = !selectedButton.isSelected();
        selectedButton.setSelected(isSelected);
        unselectedButton.setSelected(!isSelected);

        if (isSelected) {
            selectedButton.setBackground(buttonHighlightDrawable);
            unselectedButton.setBackground(outlineForButton);
        } else {
            selectedButton.setBackground(outlineForButton);
            unselectedButton.setBackground(buttonHighlightDrawable);
        }
    }


    //-----------------------------------------------------Load and Save Preferences---------------------------------------------------//

    // Inside WildCardSettings or any other settings activity
    private void goToMainGameWithExtra(int totalDrinkNumber) {
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
        int loadWildCardAmount = GeneralSettingsLocalStore.fromContext(this).playerWildCardCount();
        wildcardPerPlayerEditText.setText(String.valueOf(loadWildCardAmount));

        int loadTotalDrinkAmount = GeneralSettingsLocalStore.fromContext(this).totalDrinkAmount();
        totalDrinksEditText.setText(String.valueOf(loadTotalDrinkAmount));


        boolean multiChoiceSelected = GeneralSettingsLocalStore.fromContext(this).isMultiChoice();
        button_multiChoice.setSelected(multiChoiceSelected);
        button_nonMultiChoice.setSelected(!multiChoiceSelected);

        if (multiChoiceSelected) {
            button_multiChoice.setBackground(buttonHighlightDrawable);
            button_nonMultiChoice.setBackground(outlineForButton);
        } else {
            button_multiChoice.setBackground(outlineForButton);
            button_nonMultiChoice.setBackground(buttonHighlightDrawable);
        }

    }


    private void savePreferences() {
        int wildCardAmountSetInSettings = Integer.parseInt(wildcardPerPlayerEditText.getText().toString());
        GeneralSettingsLocalStore.fromContext(this).setPlayerWildCardCount(wildCardAmountSetInSettings);


        int totalDrinkAmountSetInSettings = Integer.parseInt(totalDrinksEditText.getText().toString());
        GeneralSettingsLocalStore.fromContext(this).setTotalDrinkAmount(totalDrinkAmountSetInSettings);


        GeneralSettingsLocalStore store = GeneralSettingsLocalStore.fromContext(this);
        store.setIsMultiChoice(button_multiChoice.isSelected());
    }
}