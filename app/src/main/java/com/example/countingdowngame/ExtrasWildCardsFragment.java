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
    WildCardHeadings[] extrasWildCards = {
            new WildCardHeadings("Quiz! Name two famous people with the same initials as yours. If you can't, take 2 drinks, if you can everyone takes 3 drinks.", 10, true, true),
    };

    ExtrasWildCardsAdapter adapter;

    public ExtrasWildCardsFragment(Context context, WildCardsAdapter a) {
        super(context, a);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truth_wildcards, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_WildCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        WildCardType mode = WildCardType.DELETABLE;
        adapter = new ExtrasWildCardsAdapter(extrasWildCards, requireContext(), mode);
        recyclerView.setAdapter(adapter);


        Button btnAddWildCard = view.findViewById(R.id.btnAddWildCard);
        btnAddWildCard.setOnClickListener(v -> addNewWildCard());

        Button btnToggleAll = view.findViewById(R.id.btnToggleAll);
        btnToggleAll.setOnClickListener(v -> toggleAllWildCards());


        return view;
    }

}