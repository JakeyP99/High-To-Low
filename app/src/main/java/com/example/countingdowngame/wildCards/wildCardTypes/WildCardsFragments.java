package com.example.countingdowngame.wildCards.wildCardTypes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.countingdowngame.R;
import com.example.countingdowngame.wildCards.WildCardProperties;

import io.github.muddz.styleabletoast.StyleableToast;

public class WildCardsFragments extends Fragment {

    Context mContext;
    WildCardsAdapter adapter;

    public WildCardsFragments(Context context, WildCardsAdapter a) {
        mContext = context;
        adapter = a;
    }


    protected void toggleAllWildCards() {
        boolean allEnabled = adapter.areAllEnabled();

        WildCardProperties[] wildCards = adapter.getWildCards();
        for (WildCardProperties wildcard : wildCards) {
            wildcard.setEnabled(!allEnabled);
        }

        adapter.setWildCards(wildCards);
        adapter.notifyDataSetChanged();
        adapter.saveWildCardProbabilitiesToStorage(wildCards);
    }

    //Fixme Add new wildcards do not actually work, they will be disabled until they work
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

        builder.setPositiveButton(Html.fromHtml("<font color='" + blueDarkColor + "'>OK</font>"), (dialog, which) -> {
            int probability = 10; // Invalid input, set to a default value
            String text = textInput.getText().toString().trim();
            if (text.isEmpty()) {
                StyleableToast.makeText(mContext, "The wildcard needs some text, please and thanks!", R.style.newToast).show();
                return;
            }
            if (textInput.length() <= 0) {
                StyleableToast.makeText(mContext, "The wildcard needs some text, please and thanks!", R.style.newToast).show();
                return;
            }
            if (textInput.length() > 130) {
                StyleableToast.makeText(mContext, "Sorry, way too big of a wildcard boss man, limited to 130 characters.", R.style.newToast).show();
                return;
            }

            WildCardProperties newWildCard = new WildCardProperties(text, probability, true, true, null, null, null, null, null);

            saveNewWildCard(newWildCard);

            //Log the type of wildcard
            Log.d("WildCardDetails",
                    "WildCardName " + newWildCard.getText() +
                            ", probability=" + newWildCard.getProbability() +
                            ", isEnabled=" + newWildCard.isEnabled() +
                            ", isDeletable=" + newWildCard.isDeletable() +
                            ", answer=" + newWildCard.getAnswer() +
                            ", category=" + newWildCard.getCategory());

            loadWildCardsFromSharedPreferences();

            adapter.notifyDataSetChanged();
        });

        builder.show();
    }


    protected void saveNewWildCard(WildCardProperties wildcard) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int count = sharedPreferences.getInt("wildcard_count", 0);
        int newCount = count + 1;
        sharedPreferences.edit().putInt("wildcard_count", newCount).apply();
        sharedPreferences.edit()
                .putString("wildcard_text_" + newCount, wildcard.getText())
                .putInt("wildcard_probability_" + newCount, wildcard.getProbability())
                .putString("wildcard_answer_" + newCount, wildcard.getAnswer()) // Use a different key for answer
                .putString("wildcard_category_" + newCount, wildcard.getCategory()) // Use a different key for category
                .apply();
    }

    protected void loadWildCardsFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        int count = sharedPreferences.getInt("wildcard_count", 0);

        WildCardProperties[] newWildCards = new WildCardProperties[count];

        for (int i = 0; i < count; i++) {
            String text = sharedPreferences.getString("wildcard_text_" + (i + 1), "");
            String answer = sharedPreferences.getString("wildcard_answer_" + (i + 1), ""); // Use the correct key for answer
            String wrongAnswer1 = sharedPreferences.getString("wildcard_wronganswer1_" + (i + 1), "");
            String wrongAnswer2 = sharedPreferences.getString("wildcard_wronganswer2_" + (i + 1), "");
            String wrongAnswer3 = sharedPreferences.getString("wildcard_wronganswer3_" + (i + 1), "");

            int probability = sharedPreferences.getInt("wildcard_probability_" + (i + 1), 10);
            String category = sharedPreferences.getString("wildcard_category_" + (i + 1), ""); // Use the correct key for category
            newWildCards[i] = new WildCardProperties(text, probability, true, true, answer, wrongAnswer1, wrongAnswer2, wrongAnswer3, category);
        }

        adapter.setWildCards(newWildCards);
    }


}
