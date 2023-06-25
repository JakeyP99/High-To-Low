package com.example.countingdowngame;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TruthWildCardsFragment extends WildCardsFragments {
    WildCardHeadings[] truthWildCards = {
            new WildCardHeadings("Truth! Have you ever had a crush on a friend's significant other?", 10, true, true),
            new WildCardHeadings("Truth! What is your most regrettable romantic rejection?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever stolen something? If yes, what and why?", 10, true, true),
            new WildCardHeadings("Truth! What is your most crazy sex story?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever engaged in a naughty video chat or phone sex?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever watched adult content while in a public setting?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever had a sensual massage with a happy ending?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever betrayed a friend's trust and never apologized?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever knowingly ruined someone's relationship?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever stolen something valuable from a family member?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever caused someone to lose their job?", 10, true, true),
            new WildCardHeadings("Truth! Have you ever destroyed evidence to cover up your wrongdoing?", 10, true, true),
    };

    public TruthWildCardsFragment(Context context, WildCardsAdapter a) {
        super(context, a);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truth_wildcards, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_WildCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        WildCardType mode = WildCardType.DELETABLE;

        // Declare adapter as a field in the fragment
        TruthWildCardsAdapter adapter = new TruthWildCardsAdapter(truthWildCards, requireContext(), mode); // Assign the adapter to the field

        recyclerView.setAdapter(adapter);

        Button btnAddWildCard = view.findViewById(R.id.btnAddWildCard);
        btnAddWildCard.setOnClickListener(v -> addNewWildCard());

        Button btnToggleAll = view.findViewById(R.id.btnToggleAll);
        btnToggleAll.setOnClickListener(v -> toggleAllWildCards());
        return view;
    }

}
