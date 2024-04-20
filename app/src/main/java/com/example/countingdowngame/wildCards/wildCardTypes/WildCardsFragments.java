package com.example.countingdowngame.wildCards.wildCardTypes;

import android.content.Context;

import androidx.fragment.app.Fragment;

public class WildCardsFragments extends Fragment {

    Context mContext;
    WildCardsAdapter adapter;

    public WildCardsFragments(Context context, WildCardsAdapter a) {
        mContext = context;
        adapter = a;
    }


}
