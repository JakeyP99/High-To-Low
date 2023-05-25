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
        Settings_WildCard_Adapter nonDeletableAdapter = new Settings_WildCard_Adapter(NON_DELETABLE, this, nonDeletableWildCards);

        listViewWildCard.setAdapter(deletableAdapter);
        listViewWildCardGameMode.setAdapter(nonDeletableAdapter);

        Button btnAddWildCard = findViewById(R.id.btnAddWildCard);
        btnAddWildCard.setOnClickListener(v -> addNewWildCard());
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


                Settings_WildCard_Probabilities newWildCard = new Settings_WildCard_Probabilities(text, probability, true, true);
                Settings_WildCard_Probabilities[][] probabilitiesArray = loadWildCardProbabilitiesFromStorage(
                        getApplicationContext());
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

    Settings_WildCard_Probabilities[][] loadWildCardProbabilitiesFromStorage(Context context) {
        SharedPreferences deletablePrefs = context.getSharedPreferences("DeletablePrefs", MODE_PRIVATE);
        SharedPreferences nonDeletablePrefs = context.getSharedPreferences("NonDeletablePrefs", MODE_PRIVATE);

        Settings_WildCard_Probabilities[] deletableProbabilities = new Settings_WildCard_Probabilities[]{
                new Settings_WildCard_Probabilities("Take 1 drink.", 10, true, true),
                new Settings_WildCard_Probabilities("Take 2 drinks.", 8, true, true),
                new Settings_WildCard_Probabilities("Take 3 drinks.", 5, true, true),
                new Settings_WildCard_Probabilities("Finish your drink.", 3, true, true),
                new Settings_WildCard_Probabilities("Give 1 drink.", 10, true, true),
                new Settings_WildCard_Probabilities("Give 2 drinks.", 8, true, true),
                new Settings_WildCard_Probabilities("Give 3 drinks.", 5, true, true),
                new Settings_WildCard_Probabilities("Choose a player to finish their drink.", 3, true, true),
                new Settings_WildCard_Probabilities("The player to the left takes a drink.", 10, true, true),
                new Settings_WildCard_Probabilities("The player to the right takes a drink.", 10, true, true),
                new Settings_WildCard_Probabilities("The oldest player takes 2 drinks.", 10, true, true),
                new Settings_WildCard_Probabilities("The youngest player takes 2 drinks.", 10, true, true),
                new Settings_WildCard_Probabilities("The player who last peed takes 3 drinks.", 10, true, true),
                new Settings_WildCard_Probabilities("The player with the oldest car takes 2 drinks.", 10, true, true),
                new Settings_WildCard_Probabilities("Whoever last rode on a train takes 2 drinks.", 10, true, true),
                new Settings_WildCard_Probabilities("Anyone who is standing takes 4 drinks, why are you standing? Sit down mate.", 10, true, true),
                new Settings_WildCard_Probabilities("Anyone who is sitting takes 2 drinks.", 10, true, true),
                new Settings_WildCard_Probabilities("Whoever has the longest hair takes 2 drinks.", 10, true, true),
                new Settings_WildCard_Probabilities("Whoever is wearing a watch takes 2 drinks.", 10, true, true),
                new Settings_WildCard_Probabilities("Whoever has a necklace on takes 2 drinks.", 10, true, true),
                new Settings_WildCard_Probabilities("Double the ending drink (whoever loses must now do double the consequence).", 5, true, true),
                new Settings_WildCard_Probabilities("Drink for courage then deliver a line from your favourite film making it as dramatic as possible!", 2, true, true),
                new Settings_WildCard_Probabilities("Give 1 drink for every cheese you can name in 10 seconds.", 2, true, true),
                new Settings_WildCard_Probabilities("The shortest person at the table must take 4 drinks then give 4 drinks.", 2, true, true),
                new Settings_WildCard_Probabilities("Bare your biceps and flex for everyone. The players next to you each drink 2 for the view.", 2, true, true),
                new Settings_WildCard_Probabilities("All females drink 3, and all males drink 3. Equality.", 5, true, true)
        };

        int deletableCount = deletablePrefs.getInt("wild_card_count", deletableProbabilities.length);
        if (deletableCount > deletableProbabilities.length) {
            deletableProbabilities = Arrays.copyOf(deletableProbabilities, deletableCount);
        }

        for (int i = 0; i < deletableCount; i++) {
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

        Settings_WildCard_Probabilities[] nonDeletableProbabilities = new Settings_WildCard_Probabilities[]{
                new Settings_WildCard_Probabilities("Get a skip button to use on any one of your turns!", 3, true, false)
        };

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

        return new Settings_WildCard_Probabilities[][]{deletableProbabilities, nonDeletableProbabilities};
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

