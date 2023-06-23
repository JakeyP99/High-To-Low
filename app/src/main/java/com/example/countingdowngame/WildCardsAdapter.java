package com.example.countingdowngame;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class WildCardsAdapter extends RecyclerView.Adapter<WildCardsAdapter.WildCardViewHolder> {
    protected WildCardHeadings[] wildCards;
    protected Context mContext;
    protected WildCardType mMode;
    private final String mSaveKey;

    public WildCardsAdapter(String saveKey, WildCardHeadings[] wildCards, Context context, WildCardType mode) {
        this.wildCards = wildCards;
        this.mContext = context;
        this.mMode = mode;
        this.mSaveKey = saveKey;
        loadWildCardProbabilitiesFromStorage();
    }



     WildCardHeadings[] loadWildCardProbabilitiesFromStorage() {
        SharedPreferences prefs = mContext.getSharedPreferences(mSaveKey, MODE_PRIVATE);

        int wildCardCount = prefs.getInt("wild_card_count", 0);
        WildCardHeadings[] loadedWildCards = new WildCardHeadings[wildCardCount];

        for (int i = 0; i < wildCardCount; i++) {
            boolean enabled = prefs.getBoolean("wild_card_enabled_" + i, false);
            String activity = prefs.getString("wild_card_activity_" + i, "");
            int probability = prefs.getInt("wild_card_probability_" + i, 0);

            loadedWildCards[i] = new WildCardHeadings(activity, probability, enabled, true);
        }
        wildCards = loadedWildCards;
        return loadedWildCards;
    }


    void setProbabilitySizeBasedOnString(TextView textView, String text) {
        int textSize = 18;
        if (text.length() > 2) {
            textSize = 12;
        }
        textView.setTextSize(textSize);
    }

    public void saveWildCardProbabilitiesToStorage(WildCardHeadings[] probabilities) {
        SharedPreferences prefs = mContext.getSharedPreferences(mSaveKey, MODE_PRIVATE);
        wildCards= probabilities;

        SharedPreferences.Editor editor = prefs.edit();

        editor.clear();
        editor.putInt("wild_card_count", probabilities.length);

        for (int i = 0; i < probabilities.length; i++) {
            WildCardHeadings probability = probabilities[i];
            editor.putBoolean("wild_card_enabled_" + i, probability.isEnabled());
            editor.putString("wild_card_activity_" + i, probability.getText());
            editor.putInt("wild_card_probability_" + i, probability.getProbability());
        }

        editor.apply();
    }

    public abstract class WildCardViewHolder extends RecyclerView.ViewHolder {
        protected TextView textViewTitle;
        protected TextView textViewProbabilities;
        protected Button editButton;
        protected Switch switchEnabled;

        public WildCardViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textview_wildcard);
            editButton = itemView.findViewById(R.id.button_edit_probability);
            textViewProbabilities = itemView.findViewById(R.id.textview_probability);
            switchEnabled = itemView.findViewById(R.id.switch_wildcard);
        }

        public void bind(WildCardHeadings wildcard) {
            textViewTitle.setText(wildcard.getText());
            textViewProbabilities.setText(String.valueOf(wildcard.getProbability()));
            switchEnabled.setChecked(wildcard.isEnabled());

            switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
                wildcard.setEnabled(isChecked);
                saveWildCardProbabilitiesToStorage(wildCards);
            });

            editButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Edit Wildcard Properties");
                int blueDarkColor = mContext.getResources().getColor(R.color.bluedark);

                LinearLayout layout = new LinearLayout(mContext);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText textInput = new EditText(mContext);

                if (wildcard.isDeletable()) {
                    textInput.setInputType(InputType.TYPE_CLASS_TEXT);
                    textInput.setText(wildcard.getText());
                    layout.addView(textInput);
                } else {
                    textInput.setEnabled(false);
                }

                final EditText probabilityInput = new EditText(mContext);
                probabilityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                probabilityInput.setText(String.valueOf(wildcard.getProbability()));
                layout.addView(probabilityInput);

                builder.setView(layout);

                builder.setNegativeButton(Html.fromHtml("<font color='" + blueDarkColor + "'>Cancel</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                ArrayList<WildCardHeadings> wildCardList = new ArrayList<>(Arrays.asList(wildCards));

                if (wildcard.isDeletable()) {
                    builder.setNeutralButton(Html.fromHtml("<font color='" + blueDarkColor + "'>Delete</font>"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            wildCardList.remove(getAdapterPosition());
                            wildCards = wildCardList.toArray(new WildCardHeadings[0]);
                            notifyDataSetChanged();
                            saveWildCardProbabilitiesToStorage(wildCards);
                        }
                    });
                }

                builder.setPositiveButton(Html.fromHtml("<font color='" + blueDarkColor + "'>OK</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputText = textInput.getText().toString().trim();
                        if (wildcard.isDeletable() && inputText.isEmpty()) {
                            Toast.makeText(mContext, "The wildcard needs some text, please and thanks!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int probability;
                        try {
                            probability = Integer.parseInt(probabilityInput.getText().toString());
                        } catch (NumberFormatException e) {
                            probability = 0; // Invalid input, set to 0
                        }

                        if (probabilityInput.getText().length() > 4) {
                            Toast.makeText(mContext, "Please enter a probability with 4 or fewer digits.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (textInput.length() > 100) {
                            Toast.makeText(mContext, "Sorry, way too big of a wildcard boss man, limited to 100 characters.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        wildcard.setProbability(probability);

                        if (wildcard.isDeletable()) {
                            wildcard.setText(inputText);
                            textViewTitle.setText(wildcard.getText());
                        }

                        textViewProbabilities.setText(String.valueOf(wildcard.getProbability()));
                        setProbabilitySizeBasedOnString(textViewProbabilities, String.valueOf(wildcard.getProbability()));

                        saveWildCardProbabilitiesToStorage(wildCards);
                    }
                });

                builder.show();
            });
        }
    }
}
