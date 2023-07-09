package com.example.countingdowngame;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ExtrasWildCardsFragment extends WildCardsFragments {
    static final WildCardHeadings[] defaultExtrasWildCards = {
            new WildCardHeadings("Double the current number and go again!", 3, true, true),
            new WildCardHeadings("Half the current number and go again!", 3, true, true),
            new WildCardHeadings("Reset the number!", 3, true, true),
            new WildCardHeadings("Reverse the turn order!", 3, true, true),
    };

    public ExtrasWildCardsFragment(Context context, WildCardsAdapter a) {
        super(context, a);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truth_wildcards, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_WildCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        WildCardType mode = WildCardType.EXTRAS;

        // Declare adapter as a field in the fragment
        adapter = new ExtrasWildCardsAdapter(defaultExtrasWildCards, requireContext(), mode);

        recyclerView.setAdapter(adapter);

        Button btnAddWildCard = view.findViewById(R.id.btnAddWildCard);
        btnAddWildCard.setOnClickListener(v -> addNewWildCard());

        Button btnToggleAll = view.findViewById(R.id.btnToggleAll);
        btnToggleAll.setOnClickListener(v -> toggleAllWildCards());
        return view;
    }

}