package com.example.countingdowngame.wildCards.wildCardTypes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.countingdowngame.R;
import com.example.countingdowngame.wildCards.WildCardHeadings;

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

    //TODO Add new wildcards do not actually work, they will be disabled until they work
    protected void addNewWildCard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View customTitleView = LayoutInflater.from(mContext).inflate(R.layout.layout_dialog_title, null);
        builder.setCustomTitle(customTitleView);
        int blueDarkColor = mContext.getResources().getColor(R.color.bluedark);

        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText textInput = new EditText(mContext);
        textInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        textInput.setHint("Wildcard Title");
        textInput.setLines(6); // Set the number of lines to accommodate the wildcard text
        textInput.setMinLines(2); // Set the minimum number of lines
        textInput.setMaxLines(8); // Set the maximum number of lines
        textInput.setVerticalScrollBarEnabled(true);
        layout.addView(textInput);

        builder.setView(layout);

        builder.setNegativeButton(Html.fromHtml("<font color='" + blueDarkColor + "'>Cancel</font>"), (dialog, which) -> dialog.cancel());

        builder.setPositiveButton(Html.fromHtml("<font color='" + blueDarkColor + "'>OK</font>"),(dialog, which) -> {
            int   probability = 10; // Invalid input, set to a default value
            String text = textInput.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(mContext, "The wildcard needs some text, please and thanks!", Toast.LENGTH_SHORT).show();
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

            WildCardHeadings newWildCard = new WildCardHeadings(text, probability, true, true, text, text);

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
            String category = sharedPreferences.getString("wildcard_text_" + (i + 1), "");
            newWildCards[i] = new WildCardHeadings(text, probability, true, true, answer, category);
        }

        adapter.setWildCards(newWildCards);
    }


}
