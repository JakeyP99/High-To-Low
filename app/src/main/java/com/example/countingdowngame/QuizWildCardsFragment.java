package com.example.countingdowngame;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class QuizWildCardsFragment extends WildCardsFragments {
    static final WildCardHeadings[] defaultQuizWildCards = {
            new WildCardHeadings("Quiz! Name two famous people with the same initials as yours.", 10, true, true),
            new WildCardHeadings("Quiz! Name five countries starting with the letter A.", 10, true, true),
            new WildCardHeadings("Quiz! Can you recite the first 7 digits of pi? If yes, go ahead.", 10, true, true),
            new WildCardHeadings("Quiz! In which year was the Great Fire of London?", 10, true, true),
            new WildCardHeadings("Quiz! Can you name all four fundamental forces in physics?", 10, true, true),
            new WildCardHeadings("Quiz! Who wrote the famous novel 'To Kill a Mockingbird'?", 10, true, true),
            new WildCardHeadings("Quiz! How many bones are there in the human body?", 10, true, true),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'The Starry Night'?", 10, true, true),
            new WildCardHeadings("Quiz! What is the scientific name for the common cold?", 10, true, true),
            new WildCardHeadings("Quiz! In which country is the ancient city of Machu Picchu located?", 10, true, true),
            new WildCardHeadings("Quiz! Who developed the theory of general relativity?", 10, true, true),
            new WildCardHeadings("Quiz! What is the chemical formula for glucose?", 10, true, true),
            new WildCardHeadings("Quiz! How many symphonies did Ludwig van Beethoven compose?", 10, true, true),
            new WildCardHeadings("Quiz! Who is credited with inventing the World Wide Web?", 10, true, true),
            new WildCardHeadings("Quiz! What is the largest planet in our solar system?", 10, true, true),
            new WildCardHeadings("Quiz! Can you name all seven colors of the rainbow in order?", 10, true, true),
            new WildCardHeadings("Quiz! What is the largest organ in the human body?", 10, true, true),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'The Last Supper'?", 10, true, true),
            new WildCardHeadings("Quiz! How many countries are members of the United Nations?", 10, true, true),
            new WildCardHeadings("Quiz! What is the capital of Australia?", 10, true, true),
            new WildCardHeadings("Quiz! Can you name the longest river in the world?", 10, true, true),
            new WildCardHeadings("Quiz! What is the formula for calculating the area of a circle?", 10, true, true),
            new WildCardHeadings("Quiz! Who wrote the famous novel '1984'?", 10, true, true),
            new WildCardHeadings("Quiz! How many strings does a standard violin have?", 10, true, true),
            new WildCardHeadings("Quiz! What is the chemical symbol for silver on the periodic table?", 10, true, true),
            new WildCardHeadings("Quiz! Who directed the movie 'Pulp Fiction'?", 10, true, true),
            new WildCardHeadings("Quiz! How many chromosomes are in a human body cell?", 10, true, true),
            new WildCardHeadings("Quiz! Who is the Greek god of the sea?", 10, true, true),
            new WildCardHeadings("Quiz! What is the national animal of Canada?", 10, true, true),
            new WildCardHeadings("Quiz! How many time zones are there in the world?", 10, true, true),
            new WildCardHeadings("Quiz! Who wrote the famous novel 'Moby-Dick'?", 10, true, true),
            new WildCardHeadings("Quiz! What is the largest species of shark?", 10, true, true),
            new WildCardHeadings("Quiz! Who composed the famous classical symphony 'Symphony No. 10'?", 10, true, true),
            new WildCardHeadings("Quiz! How many players are there on a basketball team?", 10, true, true),
            new WildCardHeadings("Quiz! What is the chemical symbol for iron on the periodic table?", 10, true, true),
            new WildCardHeadings("Quiz! Who directed the movie 'The Shawshank Redemption'?", 10, true, true),
            new WildCardHeadings("Quiz! How many colors are there in a rainbow?", 10, true, true),
            new WildCardHeadings("Quiz! Who is the author of the novel 'The Catcher in the Rye'?", 10, true, true),
            new WildCardHeadings("Quiz! What is the boiling point of water in Fahrenheit?", 10, true, true),
            new WildCardHeadings("Quiz! What is the deepest ocean on Earth?", 10, true, true),
            new WildCardHeadings("Quiz! Who is the author of the novel 'The Great Gatsby'?", 10, true, true),
            new WildCardHeadings("Quiz! How many legs does a spider have?", 10, true, true),
            new WildCardHeadings("Quiz! What is the chemical symbol for helium on the periodic table?", 10, true, true),
            new WildCardHeadings("Quiz! Who directed the movie 'The Godfather'?", 10, true, true),
            new WildCardHeadings("Quiz! How many sides does a heptagon have?", 10, true, true),
            new WildCardHeadings("Quiz! Who is the current Prime Minister of Japan?", 10, true, true),
            new WildCardHeadings("Quiz! What is the largest continent in the world?", 10, true, true),
            new WildCardHeadings("Quiz! Who is the author of the novel 'The Lord of the Rings'?", 10, true, true),
            new WildCardHeadings("Quiz! How many players are there on a soccer team?", 10, true, true),
    };

    public QuizWildCardsFragment(Context context, WildCardsAdapter a) {
        super(context, a);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truth_wildcards, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_WildCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        WildCardType mode = WildCardType.QUIZ;

        // Declare adapter as a field in the fragment
        adapter = new QuizWildCardsAdapter(defaultQuizWildCards, requireContext(), mode);

        recyclerView.setAdapter(adapter);

        Button btnAddWildCard = view.findViewById(R.id.btnAddWildCard);
        btnAddWildCard.setOnClickListener(v -> addNewWildCard());

        Button btnToggleAll = view.findViewById(R.id.btnToggleAll);
        btnToggleAll.setOnClickListener(v -> toggleAllWildCards());
        return view;
    }
}

