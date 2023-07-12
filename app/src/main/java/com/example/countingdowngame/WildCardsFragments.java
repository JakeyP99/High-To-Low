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

        WildCardHeadings[] wildCards = adapter.getWildCards();
        for (WildCardHeadings wildcard : wildCards) {
            wildcard.setEnabled(!allEnabled);
        }

        adapter.setWildCards(wildCards);
        adapter.notifyDataSetChanged();
        adapter.saveWildCardProbabilitiesToStorage(wildCards);
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
            if (textInput.length() > 130) {
                Toast.makeText(mContext, "Sorry, way too big of a wildcard boss man, limited to 130 characters.", Toast.LENGTH_SHORT).show();
                return;
            }

            WildCardHeadings newWildCard = new WildCardHeadings(text, probability, true, true, text);

            saveNewWildCard(newWildCard);

            loadWildCardsFromSharedPreferences();

            adapter.notifyDataSetChanged();
        });

        builder.show();
    }


    protected void saveNewWildCard(WildCardHeadings wildcard) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int count = sharedPreferences.getInt("wildcard_count", 0);
        int newCount = count + 1;
        sharedPreferences.edit().putInt("wildcard_count", newCount).apply();
        sharedPreferences.edit()
                .putString("wildcard_text_" + newCount, wildcard.getText())
                .putInt("wildcard_probability_" + newCount, wildcard.getProbability())
                .apply();
    }

    protected void loadWildCardsFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        int count = sharedPreferences.getInt("wildcard_count", 0);

        WildCardHeadings[] newWildCards = new WildCardHeadings[count];

        for (int i = 0; i < count; i++) {
            String text = sharedPreferences.getString("wildcard_text_" + (i + 1), "");
            String answer = sharedPreferences.getString("wildcard_text_" + (i + 1), "");
            int probability = sharedPreferences.getInt("wildcard_probability_" + (i + 1), 10);
            newWildCards[i] = new WildCardHeadings(text, probability, true, true, answer);
        }

        adapter.setWildCards(newWildCards);
    }


}
