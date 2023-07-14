package com.example.countingdowngame;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ExtrasWildCardsFragment extends WildCardsFragments {
    public static final WildCardHeadings[] defaultExtrasWildCards = {
//            new WildCardHeadings("Double the current number!", 10, true, false),
//            new WildCardHeadings("Half the current number!", 10, true, false),
//            new WildCardHeadings("Reset the number!", 10, true, false),
//            new WildCardHeadings("Reverse the turn order!", 10, true, false),
    };

    public ExtrasWildCardsFragment(Context context, WildCardsAdapter a) {
        super(context, a);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truth_wildcards, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_WildCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        WildCardType mode = WildCardType.EXTRAS;

        adapter = new ExtrasWildCardsAdapter(defaultExtrasWildCards, requireContext(), mode);

        recyclerView.setAdapter(adapter);

        Button btnAddWildCard = view.findViewById(R.id.btnAddWildCard);
        btnAddWildCard.setVisibility(View.GONE);

        Button btnToggleAll = view.findViewById(R.id.btnToggleAll);



        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) btnToggleAll.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        layoutParams.leftMargin = 0;
        layoutParams.rightMargin = 0;
        btnToggleAll.setLayoutParams(layoutParams);

        btnToggleAll.setOnClickListener(v -> toggleAllWildCards());
        return view;
    }

}