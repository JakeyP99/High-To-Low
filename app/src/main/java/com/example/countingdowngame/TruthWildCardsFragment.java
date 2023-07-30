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

public class TruthWildCardsFragment extends WildCardsFragments {
    public static final WildCardHeadings[] defaultTruthWildCards = {new WildCardHeadings("Give out a drink if you have ever had a threesome.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a crush on a friend's significant other.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a one-night stand.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever kissed someone of the same gender.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever sent a naughty picture or video to someone.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever used a dating app for casual encounters.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a friends-with-benefits arrangement.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever engaged in a video call of a more... intimate nature.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever flirted with a stranger in a bar or club.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever skinny-dipped in the ocean or a natural body of water.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a romantic dream about someone in this room.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a crush on a celebrity.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever used a dating app while already in a relationship.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a secret admirer.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a steamy hookup in a public place.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a crush on someone in this room.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever kissed someone within minutes of meeting them.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever engaged in role-playing with a partner.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a wardrobe malfunction in public.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a regrettable tattoo.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever tried a dating app while drunk.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a crush on a co-worker.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever lied about your age to someone.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a romantic encounter on a beach.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a crush on a cartoon character.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever made a booty call.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a surprise visit from a lover.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a crush on a friend's sibling.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever flirted with a teacher or boss.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a romantic encounter during a vacation.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever watched adult content in a public place.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a one-night stand while traveling.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a crush on a fictional character.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever lied about your relationship status.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a romantic date on a rooftop.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a crush on a neighbor.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a spontaneous make-out session.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever flirted with someone much older or younger than you.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a romantic encounter in a car.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a crush on a social media influencer.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a steamy chat with a stranger online.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a romantic encounter in a public restroom.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a crush on a friend's ex-partner.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever received a risqué text from the wrong person.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a crush on your boss.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a passionate kiss in the rain.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever fantasized about a co-worker.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a romantic encounter during a festival.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a crush on a teacher.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a provocative photoshoot.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a crush on a best friend's sibling.", 10, true, true),
            new WildCardHeadings("Give out a drink if you have ever had a scandalous encounter in an elevator.", 10, true, true),


    };

    public TruthWildCardsFragment(Context context, WildCardsAdapter a) {
        super(context, a);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truth_wildcards, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_WildCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        WildCardType mode = WildCardType.TRUTH;

        // Declare adapter as a field in the fragment
        adapter = new TruthWildCardsAdapter(defaultTruthWildCards, requireContext(), mode);

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
