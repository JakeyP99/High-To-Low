package com.example.countingdowngame;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Html;
import android.text.InputType;
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

    public WildCardHeadings[] loadWildCardProbabilitiesFromStorage(WildCardHeadings[] defaultWildCards) {
        var prefs = WildCardSettingsLocalStore.fromContext(mContext, mSaveKey);
        int wildCardCount = prefs.getWildCardQuantity();

        if (wildCardCount == 0) {
            wildCardCount = defaultWildCards.length;
            wildCards = defaultWildCards;
        }

        WildCardHeadings[] loadedWildCards = new WildCardHeadings[wildCardCount];

        for (int i = 0; i < wildCardCount; i++) {
            boolean inBounds = (i >= 0) && (i < wildCards.length);

            WildCardHeadings card = null;

            if (inBounds) {
                card = wildCards[i];
            }

            boolean enabled;
            String activity;
            int probability;
            String answer;

            if (card != null) {
                enabled = prefs.isWildcardEnabled(i, card.isEnabled());
                activity = prefs.getWildcardActivityText(i, card.getText());
                probability = prefs.getWildcardProbability(i, card.getProbability());
                answer = prefs.getWildcardAnswer(i, card.getAnswer());
            } else {
                enabled = prefs.isWildcardEnabled(i);
                activity = prefs.getWildcardActivityText(i);
                probability = prefs.getWildcardProbability(i);
                answer = prefs.getWildcardAnswer(i);
            }

            loadedWildCards[i] = new WildCardHeadings(activity, probability, enabled, true, answer);
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


    public void saveWildCardProbabilitiesToStorage(WildCardHeadings[] probabilities) {
        wildCards = probabilities;

        var prefs = WildCardSettingsLocalStore.fromContext(mContext, mSaveKey);
        prefs.setWildCardQuantity(probabilities.length);

        for (int i = 0; i < probabilities.length; i++) {
            WildCardHeadings probability = probabilities[i];
            prefs.setWildcardState(i, probability.isEnabled(), probability.getText(), probability.getProbability(), probability.getAnswer());
        }

    }

    public class WildCardViewHolder extends RecyclerView.ViewHolder {
        protected TextView textViewTitle;
        protected Button editButton;
        protected Switch switchEnabled;

        public WildCardViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textview_wildcard);
            editButton = itemView.findViewById(R.id.button_edit_probability);
            switchEnabled = itemView.findViewById(R.id.switch_wildcard);
        }

        public void bind(WildCardHeadings wildcard) {
            textViewTitle.setText(wildcard.getText());
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



                builder.setView(layout);

                builder.setNegativeButton(Html.fromHtml("<font color='" + blueDarkColor + "'>Cancel</font>"), (dialog, which) -> dialog.cancel());

                ArrayList<WildCardHeadings> wildCardList = new ArrayList<>(Arrays.asList(wildCards));
                    if (wildcard.isDeletable()) {
                        builder.setNeutralButton(Html.fromHtml("<font color='" + blueDarkColor + "'>Delete</font>"), (dialog, which) -> {
                            wildCardList.remove(getAdapterPosition());
                            wildCards = wildCardList.toArray(new WildCardHeadings[0]);
                            notifyDataSetChanged();
                            saveWildCardProbabilitiesToStorage(wildCards);
                        });
                    }

                builder.setPositiveButton(Html.fromHtml("<font color='" + blueDarkColor + "'>OK</font>"), (dialog, which) -> {
                    String inputText = textInput.getText().toString().trim();
                    if (wildcard.isDeletable() && inputText.isEmpty()) {
                        Toast.makeText(mContext, "The wildcard needs some text, please and thanks!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (textInput.length() > 130) {
                        Toast.makeText(mContext, "Sorry, way too big of a wildcard boss man, limited to 130 characters.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (wildcard.isDeletable()) {
                        wildcard.setText(inputText);
                        textViewTitle.setText(wildcard.getText());
                    }

                    saveWildCardProbabilitiesToStorage(wildCards);
                });

                builder.show();
            });
        }
    }
}
