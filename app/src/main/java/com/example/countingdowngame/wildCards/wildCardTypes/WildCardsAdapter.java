package com.example.countingdowngame.wildCards.wildCardTypes;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.countingdowngame.settings.WildCardSettingsLocalStore;
import com.example.countingdowngame.wildCards.WildCardProperties;
import com.example.countingdowngame.wildCards.WildCardType;

public abstract class WildCardsAdapter extends RecyclerView.Adapter<WildCardsAdapter.WildCardViewHolder> {

    private final String mSaveKey;
    protected Context mContext;
    protected WildCardType mMode;
    protected WildCardProperties[] wildCards;

    public WildCardsAdapter(String saveKey, Context context, WildCardType mode) {
        this.mSaveKey = saveKey;
        this.mContext = context;
        this.mMode = mode;

        // IMPORTANT: always load from source of truth
        this.wildCards = loadWildCardsFromAdapter(getDefaultWildCards());
    }

    /**
     * Each subclass MUST provide clean static data (WildCardData.XYZ)
     */
    protected abstract WildCardProperties[] getDefaultWildCards();

    public WildCardProperties[] loadWildCardsFromAdapter(WildCardProperties[] defaultWildCards) {

        var prefs = WildCardSettingsLocalStore.fromContext(mContext, mSaveKey);
        int count = defaultWildCards.length;

        WildCardProperties[] loaded = new WildCardProperties[count];

        for (int i = 0; i < count; i++) {

            WildCardProperties base = defaultWildCards[i];

            String activity = base.getWildCard();
            boolean enabled = base.isEnabled();
            boolean used = base.isUsedWildCard();

            String answer = base.getAnswer();
            String w1 = base.getWrongAnswer1();
            String w2 = base.getWrongAnswer2();
            String w3 = base.getWrongAnswer3();
            String category = base.getCategory();

            // ---- SAFE PREF OVERRIDES ----
            String pActivity = safe(prefs.getWildcardActivityText(i, activity), activity);
            String pAnswer = safe(prefs.getWildcardAnswer(i, answer), answer);
            String pW1 = safe(prefs.getWildcardWrongAnswer(i, w1), w1);
            String pW2 = safe(prefs.getWildcardWrongAnswer2(i, w2), w2);
            String pW3 = safe(prefs.getWildcardWrongAnswer3(i, w3), w3);
            String pCategory = safe(prefs.getWildCardCategory(i, category), category);

            boolean pEnabled = prefs.isWildcardEnabled(i, enabled);
            boolean pUsed = prefs.getWildCardDeletable(i, used);

            loaded[i] = new WildCardProperties(
                    pActivity,
                    pEnabled,
                    pUsed,
                    pAnswer,
                    pW1,
                    pW2,
                    pW3,
                    pCategory
            );

            Log.d(TAG, "CARD " + i +
                    " A=" + pAnswer +
                    " W1=" + pW1 +
                    " W2=" + pW2 +
                    " W3=" + pW3);
        }

        wildCards = loaded;
        return loaded;
    }

    /**
     * Prevents empty SharedPreferences from overwriting real data
     */
    private String safe(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value;
    }

    public WildCardProperties[] getWildCards() {
        return wildCards;
    }

    public void setWildCards(WildCardProperties[] wildCards) {
        this.wildCards = wildCards;
    }

    public void saveWildCardProbabilitiesToStorage(WildCardProperties[] wildcard) {

        var prefs = WildCardSettingsLocalStore.fromContext(mContext, mSaveKey);
        prefs.setWildCardQuantity(wildcard.length);

        for (int i = 0; i < wildcard.length; i++) {

            WildCardProperties c = wildcard[i];

            if (c.hasAnswer()) {
                prefs.setWildcardState(
                        i,
                        c.isEnabled(),
                        c.getWildCard(),
                        c.getAnswer(),
                        c.getWrongAnswer1(),
                        c.getWrongAnswer2(),
                        c.getWrongAnswer3(),
                        c.getCategory()
                );
            } else {
                prefs.setWildcardState(i, c.isEnabled(), c.getWildCard());
            }
        }
    }

    public static class WildCardViewHolder extends RecyclerView.ViewHolder {
        public WildCardViewHolder(View itemView) {
            super(itemView);
        }
    }
}