package com.example.countingdowngame.wildCards.wildCardTypes;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.countingdowngame.settings.WildCardSettingsLocalStore;
import com.example.countingdowngame.wildCards.WildCardProperties;
import com.example.countingdowngame.wildCards.WildCardType;

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
            boolean inBounds = i < wildCards.length;

            WildCardProperties card = null;

            if (inBounds) {
                card = wildCards[i];
            }

            boolean enabled;
            String activity;
            int probability;
            String answer;
            String wrongAnswer1;
            String wrongAnswer2;
            String wrongAnswer3;
            boolean deletable;
            String category;

            if (card != null) {
                enabled = prefs.isWildcardEnabled(i, card.isEnabled());
                activity = prefs.getWildcardActivityText(i, card.getText());
                probability = prefs.getWildcardProbability(i, card.getProbability());
                deletable = prefs.getWildCardDeletable(i, card.isDeletable());
                answer = prefs.getWildcardAnswer(i, card.getAnswer());
                wrongAnswer1 = prefs.getWildcardWrongAnswer(i, card.getWrongAnswer1());
                wrongAnswer2 = prefs.getWildcardWrongAnswer2(i, card.getWrongAnswer2());
                wrongAnswer3 = prefs.getWildcardWrongAnswer3(i, card.getWrongAnswer3());


                category = prefs.getWildCardCategory(i, card.getCategory());

            } else {
                enabled = prefs.isWildcardEnabled(i);
                activity = prefs.getWildcardActivityText(i);
                probability = prefs.getWildcardProbability(i);
                deletable = prefs.getWildCardDeletable(i);
                answer = prefs.getWildcardAnswer(i);
                wrongAnswer1 = prefs.getWildcardAnswer(i);
                wrongAnswer2 = prefs.getWildcardAnswer(i);
                wrongAnswer3 = prefs.getWildcardAnswer(i);


                category = prefs.getWildcardCategory(i);

            }

            loadedWildCards[i] = new WildCardProperties(activity, probability, enabled, deletable, answer, wrongAnswer1, wrongAnswer2, wrongAnswer3, category);
        }

        wildCards = loadedWildCards;

        return loadedWildCards;
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
                prefs.setWildcardState(i, probability.isEnabled(), probability.getText(), probability.getProbability(), probability.getAnswer(), probability.getWrongAnswer1(), probability.getWrongAnswer2(), probability.getWrongAnswer3(), probability.getCategory());
            } else {
                prefs.setWildcardState(i, probability.isEnabled(), probability.getText(), probability.getProbability());
            }

        }
    }


    public static class WildCardViewHolder extends RecyclerView.ViewHolder {
        public WildCardViewHolder(View itemView) {
            super(itemView);
        }

    }
}
