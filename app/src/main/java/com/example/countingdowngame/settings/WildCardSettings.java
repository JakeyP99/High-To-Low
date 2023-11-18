package com.example.countingdowngame.settings;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.res.ResourcesCompat;

import com.example.countingdowngame.R;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import io.github.muddz.styleabletoast.StyleableToast;

public class WildCardSettings extends ButtonUtilsActivity implements View.OnClickListener {

    //-----------------------------------------------------Initialize---------------------------------------------------//
    private EditText wildcardPerPlayerEditText;
    private Button button_multiChoice;
    private Button button_nonMultiChoice;
    private Drawable buttonHighlightDrawable;
    private Drawable outlineForButton;
    private Button btnReturn;


    //-----------------------------------------------------On Pause---------------------------------------------------//

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }


    @Override
    public void onBackPressed() {
        if (isValidInput()) {
            savePreferences();
            super.onBackPressed();
        } else {
            String wildCardAmountInput = wildcardPerPlayerEditText.getText().toString().trim();
            if (wildCardAmountInput.isEmpty()) {
                wildcardPerPlayerEditText.setText("0");
                savePreferences();
                super.onBackPressed();
            } else {
                int wildCardAmount = Integer.parseInt(wildCardAmountInput);
                if (wildCardAmount < 0 || wildCardAmount > 100) {
                    StyleableToast.makeText(getApplicationContext(), "Please enter a number between 0 and 100", R.style.newToast).show();
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    //-----------------------------------------------------On Create---------------------------------------------------//


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b2_settings_wildcard);
        initializeViews();
        loadPreferences();
        setButtonListeners();
    }

    //-----------------------------------------------------Initialize Views---------------------------------------------------//

    private void initializeViews() {
        buttonHighlightDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.buttonhighlight, null);
        outlineForButton = ResourcesCompat.getDrawable(getResources(), R.drawable.outlineforbutton, null);
        btnReturn = findViewById(R.id.buttonReturn);
        button_multiChoice = findViewById(R.id.button_multiChoice);
        button_nonMultiChoice = findViewById(R.id.button_nonMultiChoice);

        wildcardPerPlayerEditText = findViewById(R.id.edittext_wildcard_amount);

        wildcardPerPlayerEditText.addTextChangedListener(new TextWatcher() {
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
                validateWildCardAmount();
            }
        });

    }


    //-----------------------------------------------------Button Clicks---------------------------------------------------//
    private boolean isValidInput() {
        String wildCardAmountInput = wildcardPerPlayerEditText.getText().toString().trim();

        if (wildCardAmountInput.length() > 3) {
            wildCardAmountInput = wildCardAmountInput.substring(0, 3);
            wildcardPerPlayerEditText.setText(wildCardAmountInput);
        }

        try {
            int wildCardAmount = Integer.parseInt(wildCardAmountInput);
            return wildCardAmount >= 0 && wildCardAmount <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    private void validateWildCardAmount() {
        String wildCardAmountInput = wildcardPerPlayerEditText.getText().toString().trim();

        if (wildCardAmountInput.length() > 3) {
            wildCardAmountInput = wildCardAmountInput.substring(0, 3);
            wildcardPerPlayerEditText.setText(wildCardAmountInput);
            wildcardPerPlayerEditText.setSelection(wildCardAmountInput.length());
        }

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

        btnUtils.setButton(btnReturn, () -> {
            if (isValidInput()) {
                savePreferences();
                super.onBackPressed();
            } else {
                String wildCardAmountInput = wildcardPerPlayerEditText.getText().toString().trim();
                if (wildCardAmountInput.isEmpty()) {
                    wildcardPerPlayerEditText.setText("0");
                    savePreferences();
                    super.onBackPressed();
                } else {
                    int wildCardAmount = Integer.parseInt(wildCardAmountInput);
                    if (wildCardAmount < 0 || wildCardAmount > 100) {
                        StyleableToast.makeText(getApplicationContext(), "Please enter a number between 0 and 100", R.style.newToast).show();
                    } else {
                        super.onBackPressed();
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

    private void loadPreferences() {
        int loadWildCardAmount = GeneralSettingsLocalStore.fromContext(this).playerWildCardCount();
        wildcardPerPlayerEditText.setText(String.valueOf(loadWildCardAmount));

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

        GeneralSettingsLocalStore store = GeneralSettingsLocalStore.fromContext(this);
        store.setIsMultiChoice(button_multiChoice.isSelected());
    }
}