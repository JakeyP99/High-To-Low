package com.example.countingdowngame.wildCards.wildCardTypes;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.example.countingdowngame.wildCards.WildCardProperties;

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


}
