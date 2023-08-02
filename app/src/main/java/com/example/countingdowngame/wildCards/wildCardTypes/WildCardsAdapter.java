package com.example.countingdowngame.wildCards.wildCardTypes;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.stores.WildCardSettingsLocalStore;
import com.example.countingdowngame.wildCards.WildCardProperties;
import com.example.countingdowngame.wildCards.WildCardType;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class WildCardsAdapter extends RecyclerView.Adapter<WildCardsAdapter.WildCardViewHolder> {
    private final String mSaveKey;
    protected WildCardProperties[] wildCards;
    protected Context mContext;
    protected WildCardType mMode;

    public WildCardsAdapter(String saveKey, WildCardProperties[] wildCards, Context context, WildCardType mode) {
        this.wildCards = wildCards;
        this.mContext = context;
        this.mMode = mode;
        this.mSaveKey = saveKey;
        loadWildCardProbabilitiesFromStorage(wildCards);
    }

    public WildCardProperties[] loadWildCardProbabilitiesFromStorage(WildCardProperties[] defaultWildCards) {
        var prefs = WildCardSettingsLocalStore.fromContext(mContext, mSaveKey);
        int wildCardCount = prefs.getWildCardQuantity();

        if (wildCardCount == 0) {
            wildCardCount = defaultWildCards.length;
            wildCards = defaultWildCards;
        }

        WildCardProperties[] loadedWildCards = new WildCardProperties[wildCardCount];

        for (int i = 0; i < wildCardCount; i++) {
            boolean inBounds = (i >= 0) && (i < wildCards.length);

            WildCardProperties card = null;

            if (inBounds) {
                card = wildCards[i];
            }

            boolean enabled;
            String activity;
            int probability;
            String answer;
            boolean deletable;
            String category;

            if (card != null) {
                enabled = prefs.isWildcardEnabled(i, card.isEnabled());
                activity = prefs.getWildcardActivityText(i, card.getText());
                probability = prefs.getWildcardProbability(i, card.getProbability());
                deletable = prefs.getWildCardDeletable(i, card.isDeletable());
                answer = prefs.getWildcardAnswer(i, card.getAnswer());
                category = prefs.getWildCardCategory(i, card.getCategory());

            } else {
                enabled = prefs.isWildcardEnabled(i);
                activity = prefs.getWildcardActivityText(i);
                probability = prefs.getWildcardProbability(i);
                deletable = prefs.getWildCardDeletable(i);
                answer = prefs.getWildcardAnswer(i);
                category = prefs.getWildcardCategory(i);

            }

            loadedWildCards[i] = new WildCardProperties(activity, probability, enabled, deletable, answer, category);
        }

        wildCards = loadedWildCards;

        return loadedWildCards;
    }

    public boolean areAllEnabled() {
        for (WildCardProperties wildcard : wildCards) {
            if (!wildcard.isEnabled()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBindViewHolder(@NonNull WildCardViewHolder holder, int position) {
        WildCardProperties wildcard = wildCards[position];
        holder.bind(wildcard);
    }

    @Override
    public int getItemCount() {
        return wildCards.length;
    }

    public WildCardProperties[] getWildCards() {
        return wildCards;
    }

    public void setWildCards(WildCardProperties[] wildCards) {
        this.wildCards = wildCards;
    }

    public void saveWildCardProbabilitiesToStorage(WildCardProperties[] probabilities) {
        wildCards = probabilities;

        var prefs = WildCardSettingsLocalStore.fromContext(mContext, mSaveKey);
        prefs.setWildCardQuantity(probabilities.length);

        for (int i = 0; i < probabilities.length; i++) {
            WildCardProperties probability = probabilities[i];

            if (probability.hasAnswer()) {
                prefs.setWildcardState(i, probability.isEnabled(), probability.getText(), probability.getProbability(), probability.getAnswer(), probability.getCategory());
            } else {
                prefs.setWildcardState(i, probability.isEnabled(), probability.getText(), probability.getProbability());
            }
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

        public void bind(WildCardProperties wildcard) {
            textViewTitle.setText(wildcard.getText());
            switchEnabled.setChecked(wildcard.isEnabled());

            switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
                wildcard.setEnabled(isChecked);
                saveWildCardProbabilitiesToStorage(wildCards);
            });

            editButton.setOnClickListener(v -> {
                //Log the type of wildcard
                Log.d("WildCardDetails",
                        "WildCardName " + wildcard.getText() +
                                ", probability=" + wildcard.getProbability() +
                                ", isEnabled=" + wildcard.isEnabled() +
                                ", isDeletable=" + wildcard.isDeletable() +
                                ", answer=" + wildcard.getAnswer() +
                                ", category=" + wildcard.getCategory());

                if (!wildcard.isDeletable()) {
                    Toast.makeText(mContext, "Sorry, these wildcards cannot be edited!", Toast.LENGTH_SHORT).show();
                    return;
                }


                final EditText wildCardTextInput = new EditText(mContext);
                final EditText answerWildCardTextInput = new EditText(mContext);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                View customTitleView = LayoutInflater.from(mContext).inflate(R.layout.layout_dialog_title, null);


                builder.setCustomTitle(customTitleView);
                LinearLayout layout = new LinearLayout(mContext);
                layout.setOrientation(LinearLayout.VERTICAL);

                // Apply padding to the dialog content
                int paddingInDp = 16; // Adjust the padding size as needed
                float density = mContext.getResources().getDisplayMetrics().density;
                int paddingInPx = (int) (paddingInDp * density);
                layout.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);

                layout.addView(wildCardTextInput);

                if (wildcard.getAnswer() != null) {
                    answerWildCardTextInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    answerWildCardTextInput.setLines(6);
                    answerWildCardTextInput.setMinLines(2);
                    answerWildCardTextInput.setMaxLines(8);
                    answerWildCardTextInput.setVerticalScrollBarEnabled(true);
                    answerWildCardTextInput.setText(wildcard.getAnswer());
                    layout.addView(answerWildCardTextInput);
                }

                wildCardTextInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                wildCardTextInput.setLines(6); // Set the number of lines to accommodate the wildcard text
                wildCardTextInput.setMinLines(2); // Set the minimum number of lines
                wildCardTextInput.setMaxLines(8); // Set the maximum number of lines
                wildCardTextInput.setVerticalScrollBarEnabled(true);
                wildCardTextInput.setText(wildcard.getText());

                builder.setView(layout);

                builder.setNegativeButton(Html.fromHtml("<font color='" + R.color.bluedark + "'>Cancel</font>"), (dialog, which) -> dialog.cancel());

                ArrayList<WildCardProperties> wildCardList = new ArrayList<>(Arrays.asList(wildCards));
                if (wildcard.isDeletable()) {
                    builder.setNeutralButton(Html.fromHtml("<font color='" + R.color.bluedark + "'>Delete</font>"), (dialog, which) -> {
                        wildCardList.remove(getAdapterPosition());
                        wildCards = wildCardList.toArray(new WildCardProperties[0]);
                        notifyDataSetChanged();
                        saveWildCardProbabilitiesToStorage(wildCards);
                    });
                }

                builder.setPositiveButton(Html.fromHtml("<font color='" + R.color.bluedark + "'>OK</font>"), (dialog, which) -> {
                    String inputText = wildCardTextInput.getText().toString().trim();
                    String inputAnswer = answerWildCardTextInput.getText().toString().trim();

                    if (!wildcard.hasAnswer()) {
                        // Handling wildcards without an answer
                        if (inputText.isEmpty()) {
                            Toast.makeText(mContext, "The wildcard needs some text, please and thanks!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (wildCardTextInput.length() > 130) {
                            Toast.makeText(mContext, "Sorry, way too big of a wildcard boss man, limited to 130 characters.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Update the wildcard text and title
                        wildcard.setText(inputText);
                        textViewTitle.setText(wildcard.getText());
                    } else {
                        // Handling wildcards with an answer
                        if (inputText.isEmpty() || inputAnswer.isEmpty()) {
                            Toast.makeText(mContext, "The wildcard needs some text in both the question and answer, please and thanks!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (wildCardTextInput.length() > 130 || answerWildCardTextInput.length() > 130) {
                            Toast.makeText(mContext, "Sorry, way too big of a wildcard boss man, limited to 130 characters for the questions or answers.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Update the wildcard text, answer, and title
                        wildcard.setText(inputText);
                        wildcard.setAnswer(inputAnswer);
                        textViewTitle.setText(wildcard.getText());
                    }

                    // Save the updated wildcards to storage
                    saveWildCardProbabilitiesToStorage(wildCards);
                });


                builder.show();
            });
        }
    }
}
