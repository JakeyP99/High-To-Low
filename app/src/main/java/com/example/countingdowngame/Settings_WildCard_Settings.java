package com.example.countingdowngame;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class Settings_WildCard_Settings extends ButtonUtilsActivity {


    //-----------------------------------------------------Initialize---------------------------------------------------//
    private Button btnReturn;
    private static int loadWildCardAmount;

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
    }

    private void setButtonListeners() {
        btnUtils.setButton(btnReturn, this::onBackPressed);
    }
    //-----------------------------------------------------Get WildCard---------------------------------------------------//

    public static int getWildCardAmountFromSettings(Context context) {
        SharedPreferences wildcardPreferences = context.getSharedPreferences("wildcard_amount", MODE_PRIVATE);
        int amount = wildcardPreferences.getInt("wildcardAmount", 1);
        return amount;
    }

    //-----------------------------------------------------Load and Save Preferences---------------------------------------------------//

    private void loadPreferences() {
        // Load wildcard amount
        SharedPreferences wildcardPreferences = getSharedPreferences("wildcard_amount", MODE_PRIVATE);
        loadWildCardAmount = wildcardPreferences.getInt("wildcardAmount", 1);
        EditText wildcardPerPlayerEditText = findViewById(R.id.edittext_wildcard_amount);
        wildcardPerPlayerEditText.setText(String.valueOf(loadWildCardAmount));

    }

    private void savePreferences() {
        // Save wildcard amount
        EditText wildcardPerPlayerEditText = findViewById(R.id.edittext_wildcard_amount);
        int wildCardAmountSetInSettings = Integer.parseInt(wildcardPerPlayerEditText.getText().toString());

        SharedPreferences wildcardPreferences = getSharedPreferences("wildcard_amount", MODE_PRIVATE);
        SharedPreferences.Editor wildcardEditor = wildcardPreferences.edit();
        wildcardEditor.putInt("wildcardAmount", wildCardAmountSetInSettings);
        wildcardEditor.apply();
    }



}