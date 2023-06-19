package com.example.countingdowngame;

import static com.example.countingdowngame.Settings_WildCard_Mode.DELETABLE;
import static com.example.countingdowngame.Settings_WildCard_Mode.NON_DELETABLE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Arrays;

public class Settings_WildCard_Choice extends ButtonUtilsActivity {
        private ListView listViewWildCard;
        private Settings_WildCard_Adapter deletableAdapter;
        private Settings_WildCard_Adapter nonDeletableAdapter;
        private Button btnToggleAll; // Declare the button at the class level

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.b2_settings_wildcard_edit);

            listViewWildCard = findViewById(R.id.listView_WildCard);
            ListView listViewWildCardGameMode = findViewById(R.id.listView_GameModeWildCards);

            Settings_WildCard_Probabilities[][] wildCardArrays = loadWildCardProbabilitiesFromStorage(getApplicationContext());
            Settings_WildCard_Probabilities[] deletableWildCards = wildCardArrays[0];
            Settings_WildCard_Probabilities[] nonDeletableWildCards = wildCardArrays[1];

            deletableAdapter = new Settings_WildCard_Adapter(DELETABLE, this, deletableWildCards);
            nonDeletableAdapter = new Settings_WildCard_Adapter(NON_DELETABLE, this, nonDeletableWildCards);

            listViewWildCard.setAdapter(deletableAdapter);
            listViewWildCardGameMode.setAdapter(nonDeletableAdapter);

            Button btnAddWildCard = findViewById(R.id.btnAddWildCard);

            btnUtils.setButton(btnAddWildCard, () -> {

                addNewWildCard();
            });
            btnToggleAll = findViewById(R.id.btnToggleAll); // Initialize the button here

            btnUtils.setButton(btnToggleAll, () -> {
                toggleAllWildCards();
            });
        }

    private void addNewWildCard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Wildcard");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText textInput = new EditText(this);
        textInput.setInputType(InputType.TYPE_CLASS_TEXT);
        textInput.setHint("Wildcard Title");
        layout.addView(textInput);

        final EditText probabilityInput = new EditText(this);
        probabilityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        probabilityInput.setHint("Probability (0-9999)");
        layout.addView(probabilityInput);

        builder.setView(layout);


        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.setPositiveButton("OK", (dialog, which) -> {
            int probability;
            try {
                probability = Integer.parseInt(probabilityInput.getText().toString());
            } catch (NumberFormatException e) {
                probability = 10; // Invalid input, set to a negative value
            }
            String inputText = probabilityInput.getText().toString().trim();
            String text = textInput.getText().toString();

            if (inputText.length() > 4) {
                Toast.makeText(Settings_WildCard_Choice.this, "Please enter a probability with 4 or fewer digits.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (textInput.length() <=0 ) {
                Toast.makeText(Settings_WildCard_Choice.this, "The wildcard needs some text, please and thanks!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (textInput.length() > 100 ) {
                Toast.makeText(Settings_WildCard_Choice.this, "Sorry, way too big of a wildcard boss man, limited to 100 characters.", Toast.LENGTH_SHORT).show();
                return;
            }

                Settings_WildCard_Probabilities newWildCard = new Settings_WildCard_Probabilities(text, probability, true, true);
                Settings_WildCard_Probabilities[][] probabilitiesArray = loadWildCardProbabilitiesFromStorage(getApplicationContext());
                Settings_WildCard_Probabilities[] deletableProbabilities = probabilitiesArray[0];

                ArrayList<Settings_WildCard_Probabilities> wildCardList = new ArrayList<>(Arrays.asList(deletableProbabilities));
                wildCardList.add(newWildCard);

                deletableProbabilities = wildCardList.toArray(new Settings_WildCard_Probabilities[0]);

                deletableAdapter = new Settings_WildCard_Adapter(DELETABLE, Settings_WildCard_Choice.this, deletableProbabilities);
                listViewWildCard.setAdapter(deletableAdapter);

                saveWildCardProbabilitiesToStorage(DELETABLE, deletableProbabilities);

                probabilitiesArray[0] = deletableProbabilities;

                deletableAdapter.notifyDataSetChanged();

        });

        builder.show();

    }



    private void toggleAllWildCards() {
        boolean isEnabled = !deletableAdapter.areAllEnabled();
        deletableAdapter.setAllEnabled(isEnabled);

        Settings_WildCard_Probabilities[] deletableProbabilities = deletableAdapter.getWildCardProbabilities();
        saveWildCardProbabilitiesToStorage(DELETABLE, deletableProbabilities);

        deletableAdapter.notifyDataSetChanged();

        // Toggle the enabled state for non-deletable wild cards
        Settings_WildCard_Probabilities[] nonDeletableProbabilities = nonDeletableAdapter.getWildCardProbabilities();
        for (Settings_WildCard_Probabilities probability : nonDeletableProbabilities) {
            probability.setEnabled(isEnabled);
        }

        saveWildCardProbabilitiesToStorage(NON_DELETABLE, nonDeletableProbabilities);

        nonDeletableAdapter.notifyDataSetChanged(); // Notify the non-deletable adapter that the data has changed
    }

    Settings_WildCard_Probabilities[][] loadWildCardProbabilitiesFromStorage(Context context) {
        SharedPreferences deletablePrefs = context.getSharedPreferences("DeletablePrefs", MODE_PRIVATE);
        SharedPreferences nonDeletablePrefs = context.getSharedPreferences("NonDeletablePrefs", MODE_PRIVATE);

        // Load deletable wild card probabilities
        Settings_WildCard_Probabilities[] deletableProbabilities = WildCards.getDeletableWildCards();

        int deletableCount = deletablePrefs.getInt("wild_card_count", deletableProbabilities.length);

        if (deletableCount > deletableProbabilities.length) {
            deletableProbabilities = Arrays.copyOf(deletableProbabilities, deletableCount);
        }

        for (int i = 10; i < deletableCount; i++) {
            Settings_WildCard_Probabilities p = deletableProbabilities[i];
            boolean enabled;
            String activity;
            int probability;

            if (p != null) {
                enabled = deletablePrefs.getBoolean("wild_card_enabled_" + i, p.isEnabled());
                activity = deletablePrefs.getString("wild_card_activity_" + i, p.getText());
                probability = deletablePrefs.getInt("wild_card_probability_" + i, p.getProbability());
            } else {
                enabled = deletablePrefs.getBoolean("wild_card_enabled_" + i, false);
                activity = deletablePrefs.getString("wild_card_activity_" + i, "");
                probability = deletablePrefs.getInt("wild_card_probability_" + i, 0);
            }

            deletableProbabilities[i] = new Settings_WildCard_Probabilities(activity, probability, enabled, true);
        }

        // Load non-deletable wild card probabilities
        Settings_WildCard_Probabilities[] nonDeletableProbabilities = WildCards.getNonDeletableWildcards();
        int nonDeletableCount = nonDeletablePrefs.getInt("wild_card_count", nonDeletableProbabilities.length);

        if (nonDeletableCount > nonDeletableProbabilities.length) {
            nonDeletableProbabilities = Arrays.copyOf(nonDeletableProbabilities, nonDeletableCount);
        }

        for (int i = 0; i < nonDeletableProbabilities.length; i++) {
            Settings_WildCard_Probabilities p = nonDeletableProbabilities[i];
            boolean enabled = nonDeletablePrefs.getBoolean("wild_card_enabled_" + i, p.isEnabled());
            int probability = nonDeletablePrefs.getInt("wild_card_probability_" + i, p.getProbability());
            nonDeletableProbabilities[i] = new Settings_WildCard_Probabilities(p.getText(), probability, enabled, false);
        }

        return new Settings_WildCard_Probabilities[][] { deletableProbabilities, nonDeletableProbabilities };
    }



    public void saveWildCardProbabilitiesToStorage(Settings_WildCard_Mode mode, Settings_WildCard_Probabilities[] probabilities) {
        switch (mode) {
            case DELETABLE: {
                SharedPreferences deletablePrefs = getSharedPreferences("DeletablePrefs", MODE_PRIVATE);
                SharedPreferences.Editor deletableEditor = deletablePrefs.edit();

                deletableEditor.clear();

                for (int i = 0; i < probabilities.length; i++) {
                    Settings_WildCard_Probabilities probability = probabilities[i];
                    deletableEditor.putBoolean("wild_card_enabled_" + i, probability.isEnabled());
                    deletableEditor.putString("wild_card_activity_" + i, probability.getText());
                    deletableEditor.putInt("wild_card_probability_" + i, probability.getProbability());
                }

                deletableEditor.putInt("wild_card_count", probabilities.length);
                deletableEditor.apply();

                break;
            }
            case NON_DELETABLE: {
                SharedPreferences nonDeletablePrefs = getSharedPreferences("NonDeletablePrefs", MODE_PRIVATE);
                SharedPreferences.Editor nonDeletableEditor = nonDeletablePrefs.edit();

                nonDeletableEditor.clear(); // Clear previous data

                for (int i = 0; i < probabilities.length; i++) {
                    Settings_WildCard_Probabilities probability = probabilities[i];
                    nonDeletableEditor.putBoolean("wild_card_enabled_" + i, probability.isEnabled());
                    nonDeletableEditor.putInt("wild_card_probability_" + i, probability.getProbability());
                }
                nonDeletableEditor.apply();

                break;
            }
        }
    }
}

