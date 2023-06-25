package com.example.countingdowngame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class WildCardsFragments extends Fragment {

    Context mContext;
    WildCardsAdapter adapter;

    public WildCardsFragments(Context context, WildCardsAdapter a) {
        mContext = context;
        adapter = a;
    }


    protected void toggleAllWildCards() {
        boolean allEnabled = adapter.areAllEnabled();

        for (WildCardHeadings wildcard : adapter.getWildCards()) {
            wildcard.setEnabled(!allEnabled);
        }

        adapter.notifyDataSetChanged();
    }


    protected void addNewWildCard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Add New Wildcard");

        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText textInput = new EditText(mContext);
        textInput.setInputType(InputType.TYPE_CLASS_TEXT);
        textInput.setHint("Wildcard Title");
        layout.addView(textInput);

        final EditText probabilityInput = new EditText(mContext);
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
                probability = 10; // Invalid input, set to a default value
            }

            String inputText = probabilityInput.getText().toString().trim();
            String text = textInput.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(mContext, "The wildcard needs some text, please and thanks!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (inputText.length() > 4) {
                Toast.makeText(mContext, "Please enter a probability with 4 or fewer digits.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (textInput.length() <= 0) {
                Toast.makeText(mContext, "The wildcard needs some text, please and thanks!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (textInput.length() > 100) {
                Toast.makeText(mContext, "Sorry, way too big of a wildcard boss man, limited to 100 characters.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add "Truth!" to the start of the wildcard text
            String wildcardText = "Truth! " + text;

            WildCardHeadings newWildCard = new WildCardHeadings(wildcardText, probability, true, true);

            // Add the new wildcard to SharedPreferences
            saveNewWildCard(newWildCard);

            // Update the adapter's dataset by loading the wildcards from SharedPreferences
            loadWildCardsFromSharedPreferences();

            adapter.notifyDataSetChanged(); // Notify the adapter about the data change
        });

        builder.show();
    }


    protected void saveNewWildCard(WildCardHeadings wildcard) {
        // Get the SharedPreferences instance
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        // Get the current wildcard count
        int count = sharedPreferences.getInt("wildcard_count", 0);

        // Increment the count and save it back
        int newCount = count + 1;
        sharedPreferences.edit().putInt("wildcard_count", newCount).apply();

        // Save the new wildcard data
        sharedPreferences.edit()
                .putString("wildcard_text_" + newCount, wildcard.getText())
                .putInt("wildcard_probability_" + newCount, wildcard.getProbability())
                .apply();
    }

    protected void loadWildCardsFromSharedPreferences() {
        // Get the SharedPreferences instance
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        // Get the wildcard count
        int count = sharedPreferences.getInt("wildcard_count", 0);

        // Create a new array to store the wildcards
        WildCardHeadings[] newWildCards = new WildCardHeadings[count];

        // Load the wildcard data from SharedPreferences
        for (int i = 0; i < count; i++) {
            String text = sharedPreferences.getString("wildcard_text_" + (i + 1), "");
            int probability = sharedPreferences.getInt("wildcard_probability_" + (i + 1), 10);
            newWildCards[i] = new WildCardHeadings(text, probability, true, true);
        }

        // Update the dataset of the adapter
        adapter.setWildCards(newWildCards);
    }


}
