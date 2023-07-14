package com.example.countingdowngame;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TaskWildCardsFragment extends WildCardsFragments {
    public static final WildCardHeadings[] defaultTaskWildCards = {
//            new WildCardHeadings("Task! Take 1 drink.", 10, true, true),
//            new WildCardHeadings("Task! Take 2 drinks.", 10, true, true),
//            new WildCardHeadings("Task! Take 3 drinks.", 10, true, true),
//            new WildCardHeadings("Task! Finish your drink.", 10, true, true),
//            new WildCardHeadings("Task! Give 1 drink.", 10, true, true),
//            new WildCardHeadings("Task! Give 2 drinks.", 10, true, true),
//            new WildCardHeadings("Task! Give 3 drinks.", 10, true, true),
//            new WildCardHeadings("Task! Choose a player to finish their drink.", 10, true, true),
//            new WildCardHeadings("Task! The player to the left takes a drink.", 10, true, true),
//            new WildCardHeadings("Task! The player to the right takes a drink.", 10, true, true),
//            new WildCardHeadings("Task! The oldest player takes 2 drinks.", 10, true, true),
//            new WildCardHeadings("Task! The youngest player takes 2 drinks.", 10, true, true),
//            new WildCardHeadings("Task! The player who last peed takes 3 drinks.", 10, true, true),
//            new WildCardHeadings("Task! The player with the oldest car takes 2 drinks.", 10, true, true),
//            new WildCardHeadings("Task! Whoever last rode on a train takes 2 drinks.", 10, true, true),
//            new WildCardHeadings("Task! Anyone who is standing takes 4 drinks, why are you standing? Sit down mate.", 10, true, true),
//            new WildCardHeadings("Task! Anyone who is sitting takes 2 drinks.", 10, true, true),
//            new WildCardHeadings("Task! Whoever has the longest hair takes 2 drinks.", 10, true, true),
//            new WildCardHeadings("Task! Whoever is wearing a watch takes 2 drinks.", 10, true, true),
//            new WildCardHeadings("Task! Whoever has a necklace on takes 2 drinks.", 10, true, true),
//            new WildCardHeadings("Task! Double the ending drink (whoever loses must now do double the consequence).", 10, true, true),
//            new WildCardHeadings("Task! Drink for courage then deliver a line from your favourite film making it as dramatic as possible!", 10, true, true),
//            new WildCardHeadings("Task! Give 1 drink for every cheese you can name in 10 seconds.", 10, true, true),
//            new WildCardHeadings("Task! The shortest person at the table must take 4 drinks then give 4 drinks.", 10, true, true),
//            new WildCardHeadings("Task! Bare your biceps and flex for everyone. The players next to you each drink 2 for the view.", 10, true, true),
//            new WildCardHeadings("Task! All females drink 3, and all males drink 3. Equality.", 10, true, true),
//            new WildCardHeadings("Task! Do a handstand and give out 3 drinks.", 10, true, true),
//            new WildCardHeadings("Task! Choose someone to drink with their non-dominant hand for the next round.", 10, true, true),
//            new WildCardHeadings("Task! The next person to laugh must take 2 drinks.", 10, true, true),
//            new WildCardHeadings("Task! Everyone must stand on one leg while drinking.", 10, true, true),
//            new WildCardHeadings("Task! Recite a tongue twister three times without making a mistake, if you do take 3 drinks.", 10, true, true),
//            new WildCardHeadings("Task! Drink your whole drink while making eye contact with someone for the entire duration.", 10, true, true),
//            new WildCardHeadings("Task! The person with the longest fingernails takes 2 drinks.", 10, true, true),
//            new WildCardHeadings("Task! The person with the most expensive item on them takes 3 drinks.", 10, true, true),
//            new WildCardHeadings("Task! The person with the most keys takes 3 drinks.", 10, true, true),
//            new WildCardHeadings("Task! The person with the longest beard takes 2 drinks.", 10, true, true),
//            new WildCardHeadings("Task! Choose someone to take 2 drinks and perform a breakdance move.", 10, true, true),
//            new WildCardHeadings("Task! Choose someone to take 2 drinks and recite a poem.", 10, true, true),
//            new WildCardHeadings("Task! Drink while doing a backbend.", 10, true, true),
//            new WildCardHeadings("Task! Take a drink while doing a split.", 10, true, true),
//            new WildCardHeadings("Task! Give a toast to the person who shares your zodiac sign and take a drink.", 10, true, true),
//            new WildCardHeadings("Task! The person wearing the most buttons on their shirt takes 3 drinks. Mr Fancy aye.", 10, true, true),
//            new WildCardHeadings("Task! The person with the most watches takes 2 drinks.", 10, true, true),
//            new WildCardHeadings("Task! Perform a dramatic reading of a nursery rhyme. Everyone takes a drink.", 10, true, true),
//            new WildCardHeadings("Task! Impersonate your favorite celebrity. Others guess who it is. If they fail, they drink.", 10, true, true),
//            new WildCardHeadings("Task! Everyone imitates a farm animal. The last one to start drinks.", 10, true, true),
//            new WildCardHeadings("Task! Create a new cocktail recipe using three random ingredients. Drink up baby!", 10, true, true),
//            new WildCardHeadings("Task! Tell a cheesy joke. If others laugh, you're safe. If not, take 2 drinks.", 10, true, true),
//            new WildCardHeadings("Task! Draw a picture blindfolded. Others rate your masterpiece. If the crowd does not like it, take 2 drinks.", 10, true, true),
//            new WildCardHeadings("Task! Name three things that should be illegal but aren't. If others agree, you're safe, if not, you take 3 drinks.", 10, true, true),
    };

    public TaskWildCardsFragment(Context context, WildCardsAdapter a) {
        super(context, a);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truth_wildcards, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_WildCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        WildCardType mode = WildCardType.TASK;

        // Declare adapter as a field in the fragment
        adapter = new TaskWildCardsAdapter(defaultTaskWildCards, requireContext(), mode);

        recyclerView.setAdapter(adapter);

        Button btnAddWildCard = view.findViewById(R.id.btnAddWildCard);
        btnAddWildCard.setOnClickListener(v -> addNewWildCard());

        Button btnToggleAll = view.findViewById(R.id.btnToggleAll);
        btnToggleAll.setOnClickListener(v -> toggleAllWildCards());
        return view;
    }
}