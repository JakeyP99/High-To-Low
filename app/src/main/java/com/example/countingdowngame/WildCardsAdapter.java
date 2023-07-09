package com.example.countingdowngame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countingdowngame.stores.WildCardSettingsLocalStore;

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
        loadWildCardProbabilitiesFromStorage(wildCards);
    }

    WildCardHeadings[] loadWildCardProbabilitiesFromStorage(WildCardHeadings[] defaultWildCards) {
        var prefs = WildCardSettingsLocalStore.fromContext(mContext, mSaveKey);
        int wildCardCount = prefs.getWildCardQuantity();

        if (wildCardCount == 0) {
            wildCardCount = defaultWildCards.length;
            wildCards = defaultWildCards;
        }

        WildCardHeadings[] loadedWildCards = new WildCardHeadings[wildCardCount];

        for (int i = 0; i < wildCardCount; i++) {
            WildCardHeadings card = null;

            boolean enabled;
            String activity;
            int probability;

            if (card != null) {
                enabled = prefs.isWildcardEnabled(i, card.isEnabled());
                activity = prefs.getWildcardActivityText(i, card.getText());
                probability = prefs.getWildcardProbability(i, card.getProbability());
            } else {
                enabled = prefs.isWildcardEnabled(i);
                activity = prefs.getWildcardActivityText(i);
                probability = prefs.getWildcardProbability(i);
            }

            loadedWildCards[i] = new WildCardHeadings(activity, probability, enabled, true);
        }

        wildCards = loadedWildCards;

        return loadedWildCards;
    }


    public void setWildCards(WildCardHeadings[] wildCards) {
        this.wildCards = wildCards;
    }

    public boolean areAllEnabled() {
        for (WildCardHeadings wildcard : wildCards) {
            if (!wildcard.isEnabled()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBindViewHolder(@NonNull WildCardViewHolder holder, int position) {
        WildCardHeadings wildcard = wildCards[position];
        holder.bind(wildcard);
    }


    @Override
    public int getItemCount() {
        return wildCards.length;
    }


    public WildCardHeadings[] getWildCards() {
        return wildCards;
    }

    void setProbabilitySizeBasedOnString(TextView textView, String text) {
        int textSize = 18;
        if (text.length() > 2) {
            textSize = 12;
        }
        textView.setTextSize(textSize);
    }

    public void saveWildCardProbabilitiesToStorage(WildCardHeadings[] probabilities) {
        wildCards = probabilities;

        var prefs = WildCardSettingsLocalStore.fromContext(mContext, mSaveKey);
        prefs.setWildCardQuantity(probabilities.length);

        for (int i = 0; i < probabilities.length; i++) {
            WildCardHeadings probability = probabilities[i];
            prefs.setWildcardState(i, probability.isEnabled(), probability.getText(), probability.getProbability());
        }

    }

    public class WildCardViewHolder extends RecyclerView.ViewHolder {
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
