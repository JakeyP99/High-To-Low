package com.example.countingdowngame;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

public class WildCardQuantity extends ButtonUtilsActivity {


    //-----------------------------------------------------Initialize---------------------------------------------------//
    private Button btnReturn;
    private EditText wildcardPerPlayerEditText;


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
                    showToast();
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    //-----------------------------------------------------Initialize Views---------------------------------------------------//

    private void initializeViews() {
        buttonHighlightDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.buttonhighlight, null);
        outlineForButton = ResourcesCompat.getDrawable(getResources(), R.drawable.outlineforbutton, null);
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


    private void setButtonListeners() {
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
                        showToast();
                    } else {
                        super.onBackPressed();
                    }
                }
            }
        });
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

    }

    private void showToast() {
        Toast toast = Toast.makeText(this, "Please enter a number between 0 and 100", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
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