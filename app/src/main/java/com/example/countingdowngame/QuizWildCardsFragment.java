package com.example.countingdowngame;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class QuizWildCardsFragment extends Fragment {
    Settings_WildCard_Probabilities[] quizWildCards = {
            new Settings_WildCard_Probabilities("Quiz! Name two famous people with the same initials as yours. If you can't, take 2 drinks, if you can everyone takes 3 drinks.", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Name five countries starting with the letter A. If you can't, take 3 drinks.", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Can you recite the first 7 digits of pi? If yes, go ahead.", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! In which year was the Great Fire of London?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Can you name all four fundamental forces in physics?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who wrote the famous novel 'To Kill a Mockingbird'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many bones are there in the human body?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who painted the famous artwork 'The Starry Night'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the scientific name for the common cold?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! In which country is the ancient city of Machu Picchu located?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who developed the theory of general relativity?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the chemical formula for glucose?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many symphonies did Ludwig van Beethoven compose?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who is credited with inventing the World Wide Web?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the largest planet in our solar system?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Can you name all seven colors of the rainbow in order?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the largest organ in the human body?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who painted the famous artwork 'The Last Supper'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many countries are members of the United Nations?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the capital of Australia?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Can you name the longest river in the world?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the formula for calculating the area of a circle?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who wrote the famous novel '1984'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many strings does a standard violin have?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the chemical symbol for silver on the periodic table?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who directed the movie 'Pulp Fiction'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many chromosomes are in a human body cell?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who is the Greek god of the sea?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the national animal of Canada?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many time zones are there in the world?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who wrote the famous novel 'Moby-Dick'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the largest species of shark?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who composed the famous classical symphony 'Symphony No. 10'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many players are there on a basketball team?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the chemical symbol for iron on the periodic table?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who directed the movie 'The Shawshank Redemption'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many colors are there in a rainbow?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who is the author of the novel 'The Catcher in the Rye'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the boiling point of water in Fahrenheit?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the deepest ocean on Earth?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who is the author of the novel 'The Great Gatsby'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many legs does a spider have?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the chemical symbol for helium on the periodic table?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who directed the movie 'The Godfather'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many sides does a heptagon have?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who is the current Prime Minister of Japan?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! What is the largest continent in the world?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! Who is the author of the novel 'The Lord of the Rings'?", 10, true, true),
            new Settings_WildCard_Probabilities("Quiz! How many players are there on a soccer team?", 10, true, true),

    };

    private RecyclerView recyclerView;
    private QuizWildCardsAdapter adapter; // Declare adapter as a field in the fragment

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truth_wildcards, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_WildCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Settings_WildCard_Mode mode = Settings_WildCard_Mode.DELETABLE;

        adapter = new QuizWildCardsAdapter(quizWildCards, requireContext(), mode); // Assign the adapter to the field

        recyclerView.setAdapter(adapter);

        Button btnAddWildCard = view.findViewById(R.id.btnAddWildCard);
        btnAddWildCard.setOnClickListener(v -> {
            addNewWildCard();
        });

        Button btnToggleAll = view.findViewById(R.id.btnToggleAll);
        btnToggleAll.setOnClickListener(v -> toggleAllWildCards());

        return view;
    }


    private void toggleAllWildCards() {
        boolean allEnabled = adapter.areAllEnabled(); // Check if all wildcards are currently enabled

        for (Settings_WildCard_Probabilities wildcard : quizWildCards) {
            wildcard.setEnabled(!allEnabled); // Toggle the enabled state of each wildcard
        }

        adapter.notifyDataSetChanged(); // Notify the adapter about the data change
    }


    private void addNewWildCard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Wildcard");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText textInput = new EditText(requireContext());
        textInput.setInputType(InputType.TYPE_CLASS_TEXT);
        textInput.setHint("Wildcard Title");
        layout.addView(textInput);

        final EditText probabilityInput = new EditText(requireContext());
        probabilityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        probabilityInput.setHint("Probability (0-9999)");
        layout.addView(probabilityInput);

        builder.setView(layout);

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.setPositiveButton("OK", (dialog, which) -> {
            int probability;
            try {
                probability = Integer.parseInt(probabilityInput.getText().toString());
            } catch (NumberFormatException e) {
                probability = 10; // Invalid input, set to a default value
            }

            String text = textInput.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(requireContext(), "The wildcard needs some text, please and thanks!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add "Quiz!" to the start of the wildcard text
            String wildcardText = "Quiz! " + text;

            Settings_WildCard_Probabilities newWildCard = new Settings_WildCard_Probabilities(wildcardText, probability, true, true);

            // Create a new array with increased size
            Settings_WildCard_Probabilities[] newQuizWildCards = new Settings_WildCard_Probabilities[quizWildCards.length + 1];
            System.arraycopy(quizWildCards, 0, newQuizWildCards, 0, quizWildCards.length);
            newQuizWildCards[quizWildCards.length] = newWildCard;

            // Update the quizWildCards array with the new array
            quizWildCards = newQuizWildCards;

            adapter.setWildCards(quizWildCards); // Update the adapter's dataset with the new array
            adapter.notifyDataSetChanged(); // Notify the adapter about the data change
        });

        builder.show();
    }
}

