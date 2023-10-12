package com.example.countingdowngame.game;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.example.countingdowngame.R;
import com.example.countingdowngame.stores.GeneralSettingsLocalStore;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

public class GeneralSettings extends ButtonUtilsActivity implements View.OnClickListener {

    //-----------------------------------------------------Initialize---------------------------------------------------//
    private EditText wildcardPerPlayerEditText;
    private Button button_gameModeOne;
    private Button button_gameModeTwo;
    private Drawable buttonHighlightDrawable;
    private Drawable outlineForButton;
    private Button btnReturn;
    private Button btnMute;

//copyout
    private Button button_regularSound;
    private Button button_burpSound;
    //

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
                    Toast toast = Toast.makeText(this, "Please enter a number between 0 and 100", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
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
        setContentView(R.layout.b2_settings_game_mode_choice);
        initializeViews();
        loadPreferences();
        setButtonListeners();
    }

    //-----------------------------------------------------Initialize Views---------------------------------------------------//

    private void initializeViews() {
        button_gameModeOne = findViewById(R.id.button_gameModeOne);
        button_gameModeTwo = findViewById(R.id.button_gameModeTwo);
        buttonHighlightDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.buttonhighlight, null);
        outlineForButton = ResourcesCompat.getDrawable(getResources(), R.drawable.outlineforbutton, null);
        btnReturn = findViewById(R.id.buttonReturn);
        btnMute = findViewById(R.id.button_mute);
//        //copyout
        button_regularSound = findViewById(R.id.button_normal_sound);
        button_burpSound = findViewById(R.id.button_burp_sound);


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

        if (viewId == R.id.button_gameModeOne) {
            toggleButton(button_gameModeOne, button_gameModeTwo);
        } else if (viewId == R.id.button_gameModeTwo) {
            toggleButton(button_gameModeTwo, button_gameModeOne);
        } else if (viewId == R.id.button_mute) {
            toggleMuteButton();

//            //CopyOut
        } else if (viewId == R.id.button_normal_sound) {
            toggleButton(button_regularSound, button_burpSound);
        } else if (viewId == R.id.button_burp_sound) {
            toggleButton(button_burpSound, button_regularSound);
         //
        }

        savePreferences();
    }


    private void setButtonListeners() {
        button_gameModeOne.setOnClickListener(this);
        button_gameModeTwo.setOnClickListener(this);

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
                        Toast toast = Toast.makeText(this, "Please enter a number between 0 and 100", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        super.onBackPressed();
                    }
                }
            }
        });

        btnMute.setOnClickListener(this);
        //copyout
        button_regularSound.setOnClickListener(this);
        button_burpSound.setOnClickListener(this);
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

    private void toggleMuteButton() {
        boolean isSelected = !btnMute.isSelected();
        btnMute.setSelected(isSelected);

        Drawable selectedDrawable = isSelected ? buttonHighlightDrawable : outlineForButton;
        btnMute.setBackground(selectedDrawable);

        btnUtils.toggleMute(); // Toggle the mute state
    }


    //-----------------------------------------------------Load and Save Preferences---------------------------------------------------//

    private void loadPreferences() {
        int loadWildCardAmount = GeneralSettingsLocalStore.fromContext(this).playerWildCardCount();
        wildcardPerPlayerEditText.setText(String.valueOf(loadWildCardAmount));


        //copyout
        boolean regularSoundSelected = GeneralSettingsLocalStore.fromContext(this).shouldPlayRegularSound();
        button_regularSound.setSelected(regularSoundSelected);
        button_burpSound.setSelected(!regularSoundSelected);

        if (regularSoundSelected) {
            button_regularSound.setBackground(buttonHighlightDrawable);
            button_burpSound.setBackground(outlineForButton);
        } else {
            button_regularSound.setBackground(outlineForButton);
            button_burpSound.setBackground(buttonHighlightDrawable);
        }
//
        boolean buttonOneSelected = GeneralSettingsLocalStore.fromContext(this).isSingleScreen();
        button_gameModeOne.setSelected(buttonOneSelected);
        button_gameModeTwo.setSelected(!buttonOneSelected);

        if (buttonOneSelected) {
            button_gameModeOne.setBackground(buttonHighlightDrawable);
            button_gameModeTwo.setBackground(outlineForButton);
        } else {
            button_gameModeOne.setBackground(outlineForButton);
            button_gameModeTwo.setBackground(buttonHighlightDrawable);
        }

        boolean isMuted = GeneralSettingsLocalStore.fromContext(this).isMuted();
        btnMute.setSelected(isMuted);

        if (isMuted) {
            btnMute.setBackground(buttonHighlightDrawable);
        } else {
            btnMute.setBackground(outlineForButton);
        }


    }


    private void savePreferences() {
        int wildCardAmountSetInSettings = Integer.parseInt(wildcardPerPlayerEditText.getText().toString());
        GeneralSettingsLocalStore.fromContext(this).setPlayerWildCardCount( wildCardAmountSetInSettings);


        GeneralSettingsLocalStore store = GeneralSettingsLocalStore.fromContext(this);
        store.setIsSingleScreen(button_gameModeOne.isSelected());
        store.setIsMuted(btnMute.isSelected());

        //copyout
        store.setShouldPlayRegularSound(button_regularSound.isSelected());
    }
}