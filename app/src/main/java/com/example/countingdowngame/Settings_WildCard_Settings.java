package com.example.countingdowngame;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Settings_WildCard_Settings extends ButtonUtilsActivity {


    //-----------------------------------------------------Initialize---------------------------------------------------//
    private Button btnReturn;
    private EditText wildcardPerPlayerEditText;
    private boolean isValidInput;


    //-----------------------------------------------------On Pause---------------------------------------------------//

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }

    //-----------------------------------------------------On Create---------------------------------------------------//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b2_settings_wildcard_settings);
        initializeViews();
        loadPreferences();
        setButtonListeners();
    }
    //-----------------------------------------------------Initialize Views---------------------------------------------------//

    private void initializeViews() {
        buttonHighlightDrawable = getResources().getDrawable(R.drawable.buttonhighlight);
        outlineForButton = getResources().getDrawable(R.drawable.outlineforbutton);
        btnReturn = findViewById(R.id.buttonReturn);

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


    private void setButtonListeners() {
        btnUtils.setButton(btnReturn, this::onBackPressed);
    }

    private void onReturnButtonClicked(View view) {
        if (isValidInput) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        if (isValidInput) {
            savePreferences();
            super.onBackPressed();
        }
    }
    //-----------------------------------------------------Get WildCard---------------------------------------------------//

    public static int getWildCardAmountFromSettings(Context context) {
        SharedPreferences wildcardPreferences = context.getSharedPreferences("wildcard_amount", MODE_PRIVATE);
        return wildcardPreferences.getInt("wildcardAmount", 1);
    }


    private void validateWildCardAmount() {
        String wildCardAmountInput = wildcardPerPlayerEditText.getText().toString().trim();

       if (wildCardAmountInput.length() > 3) {
            wildCardAmountInput = wildCardAmountInput.substring(0, 3);
            wildcardPerPlayerEditText.setText(wildCardAmountInput);
            wildcardPerPlayerEditText.setSelection(wildCardAmountInput.length());
        }

        int wildCardAmount;
        try {
            wildCardAmount = Integer.parseInt(wildCardAmountInput);
            if (wildCardAmount > 100) {
                wildcardPerPlayerEditText.setError("Please enter a number between 0 and 100");
            } else {
                wildcardPerPlayerEditText.setError(null);
            }
        } catch (NumberFormatException e) {
            wildcardPerPlayerEditText.setError("Please enter a valid number");
        }
    }




    //-----------------------------------------------------Load and Save Preferences---------------------------------------------------//

    private void loadPreferences() {
        SharedPreferences wildcardPreferences = getSharedPreferences("wildcard_amount", MODE_PRIVATE);
        int loadWildCardAmount = wildcardPreferences.getInt("wildcardAmount", 1);
        wildcardPerPlayerEditText.setText(String.valueOf(loadWildCardAmount));
    }

    private void savePreferences() {
        int wildCardAmountSetInSettings = Integer.parseInt(wildcardPerPlayerEditText.getText().toString());
        SharedPreferences wildcardPreferences = getSharedPreferences("wildcard_amount", MODE_PRIVATE);
        SharedPreferences.Editor wildcardEditor = wildcardPreferences.edit();
        wildcardEditor.putInt("wildcardAmount", wildCardAmountSetInSettings);
        wildcardEditor.apply();
    }

}