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
    public static final WildCardHeadings[] defaultQuizWildCards = {

             // Science Quiz Questions
            new WildCardHeadings("Quiz! What is the deepest ocean on Earth?", 10, true, true, "Pacific Ocean"),
            new WildCardHeadings("Quiz! How many legs does a spider have?", 10, true, true, "8"),
            new WildCardHeadings("Quiz! How many sides does a heptagon have?", 10, true, true, "7"),
            new WildCardHeadings("Quiz! What is the largest planet in our solar system?", 10, true, true, "Jupiter"),
            new WildCardHeadings("Quiz! Can you name all seven colors of the rainbow in order?", 10, true, true, "Red, Orange, Yellow, Green, Blue, Indigo, Violet"),
            new WildCardHeadings("Quiz! What is the formula for calculating the area of a circle?", 10, true, true, "πr^2"),
            new WildCardHeadings("Quiz! What is the boiling point of water in Fahrenheit?", 10, true, true, "212"),
            new WildCardHeadings("Quiz! What is the chemical symbol for silver on the periodic table?", 10, true, true, "Ag"),
            new WildCardHeadings("Quiz! What is the chemical formula for glucose?", 10, true, true, "C6H12O6"),
            new WildCardHeadings("Quiz! How many chromosomes are in a human body cell?", 10, true, true, "46"),
            new WildCardHeadings("Quiz! What is the chemical symbol for helium on the periodic table?", 10, true, true, "He"),
            new WildCardHeadings("Quiz! What is the chemical symbol for oxygen on the periodic table?", 10, true, true, "O"),
            new WildCardHeadings("Quiz! What is the boiling point of water in Celsius?", 10, true, true, "100"),
            new WildCardHeadings("Quiz! Who developed the theory of general relativity?", 10, true, true, "Albert Einstein"),
            new WildCardHeadings("Quiz! What is the atomic number of hydrogen?", 10, true, true, "1"),
            new WildCardHeadings("Quiz! What is the largest organ in the human body?", 10, true, true, "Skin"),
            new WildCardHeadings("Quiz! How many chromosomes are in a human body cell?", 10, true, true, "46"),
            new WildCardHeadings("Quiz! What is the chemical symbol for helium on the periodic table?", 10, true, true, "He"),
            new WildCardHeadings("Quiz! Who discovered penicillin?", 10, true, true, "Alexander Fleming"),
            new WildCardHeadings("Quiz! What is the formula for calculating the area of a circle?", 10, true, true, "πr^2"),
            new WildCardHeadings("Quiz! What is the largest species of shark?", 10, true, true, "Whale Shark"),
            new WildCardHeadings("Quiz! What is the boiling point of water in Fahrenheit?", 10, true, true, "212"),
            new WildCardHeadings("Quiz! Who is known as the 'father of modern chemistry'?", 10, true, true, "Antoine Lavoisier"),
            new WildCardHeadings("Quiz! What is the formula for calculating density?", 10, true, true, "Density = Mass / Volume"),
            new WildCardHeadings("Quiz! What is the chemical symbol for iron on the periodic table?", 10, true, true, "Fe"),
            new WildCardHeadings("Quiz! Who proposed the theory of evolution by natural selection?", 10, true, true, "Charles Darwin"),
            new WildCardHeadings("Quiz! What is the largest moon in the solar system?", 10, true, true, "Ganymede"),
            new WildCardHeadings("Quiz! What is the freezing point of water in Celsius?", 10, true, true, "0"),
            new WildCardHeadings("Quiz! Who discovered the law of gravity?", 10, true, true, "Isaac Newton"),
            new WildCardHeadings("Quiz! What is the chemical formula for glucose?", 10, true, true, "C6H12O6"),
            new WildCardHeadings("Quiz! Who invented the first practical telephone?", 10, true, true, "Alexander Graham Bell"),

// Geography Quiz Questions
            new WildCardHeadings("Quiz! What is the national animal of Canada?", 10, true, true, "Beaver"),
            new WildCardHeadings("Quiz! How many time zones are there in the world?", 10, true, true, "24"),
            new WildCardHeadings("Quiz! What is the capital of Brazil?", 10, true, true, "Brasília"),
            new WildCardHeadings("Quiz! Which country is known as the 'Land of the Rising Sun'?", 10, true, true, "Japan"),
            new WildCardHeadings("Quiz! In which continent is the Sahara Desert located?", 10, true, true, "Africa"),
            new WildCardHeadings("Quiz! What is the largest country by land area?", 10, true, true, "Russia"),
            new WildCardHeadings("Quiz! What is the highest mountain in the world?", 10, true, true, "Mount Everest"),
            new WildCardHeadings("Quiz! What is the largest ocean?", 10, true, true, "Pacific Ocean"),
            new WildCardHeadings("Quiz! What is the capital of Australia?", 10, true, true, "Canberra"),
            new WildCardHeadings("Quiz! Which river is the longest in the world?", 10, true, true, "Nile"),
            new WildCardHeadings("Quiz! What is the official language of Japan?", 10, true, true, "Japanese"),
            new WildCardHeadings("Quiz! Which city is known as the 'Eternal City'?", 10, true, true, "Rome"),
            new WildCardHeadings("Quiz! What is the largest desert in the world?", 10, true, true, "Antarctica"),
            new WildCardHeadings("Quiz! What is the capital of Canada?", 10, true, true, "Ottawa"),
            new WildCardHeadings("Quiz! Which country is famous for the Great Barrier Reef?", 10, true, true, "Australia"),
            new WildCardHeadings("Quiz! What is the official language of Brazil?", 10, true, true, "Portuguese"),
            new WildCardHeadings("Quiz! In which country is the Taj Mahal located?", 10, true, true, "India"),
            new WildCardHeadings("Quiz! What is the largest lake in Africa?", 10, true, true, "Lake Victoria"),
            new WildCardHeadings("Quiz! What is the currency of Japan?", 10, true, true, "Japanese yen"),
            new WildCardHeadings("Quiz! In which country is Mount Kilimanjaro located?", 10, true, true, "Tanzania"),
            new WildCardHeadings("Quiz! What is the smallest country in the world?", 10, true, true, "Vatican City"),
            new WildCardHeadings("Quiz! Which country is known for the Pyramids of Giza?", 10, true, true, "Egypt"),

// History Quiz Questions
            new WildCardHeadings("Quiz! Who is the current Prime Minister of Japan?", 10, true, true, "Yoshihide Suga"),
            new WildCardHeadings("Quiz! In which year did World War I begin?", 10, true, true, "1914"),
            new WildCardHeadings("Quiz! Who was the first President of the United States?", 10, true, true, "George Washington"),
            new WildCardHeadings("Quiz! In which year did the United States declare independence from Great Britain?", 10, true, true, "1776"),
            new WildCardHeadings("Quiz! Who was the leader of the Soviet Union during World War II?", 10, true, true, "Joseph Stalin"),
            new WildCardHeadings("Quiz! Who painted the Mona Lisa?", 10, true, true, "Leonardo da Vinci"),
            new WildCardHeadings("Quiz! In which year did the Titanic sink?", 10, true, true, "1912"),
            new WildCardHeadings("Quiz! Who wrote the play 'Romeo and Juliet'?", 10, true, true, "William Shakespeare"),
            new WildCardHeadings("Quiz! In which year did the American Civil War end?", 10, true, true, "1865"),
            new WildCardHeadings("Quiz! Who was the first female Prime Minister of the United Kingdom?", 10, true, true, "Margaret Thatcher"),
            new WildCardHeadings("Quiz! In which country was Adolf Hitler born?", 10, true, true, "Austria"),
            new WildCardHeadings("Quiz! What is the significance of the Magna Carta?", 10, true, true, "It established the principle of the rule of law"),
            new WildCardHeadings("Quiz! Who was the last pharaoh of ancient Egypt?", 10, true, true, "Cleopatra"),
            new WildCardHeadings("Quiz! In which year did the Berlin Wall fall?", 10, true, true, "1989"),
            new WildCardHeadings("Quiz! Who was the first man to walk on the moon?", 10, true, true, "Neil Armstrong"),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'The Starry Night'?", 10, true, true, "Vincent van Gogh"),
            new WildCardHeadings("Quiz! In which year did World War II end?", 10, true, true, "1945"),
            new WildCardHeadings("Quiz! Who wrote the novel 'Pride and Prejudice'?", 10, true, true, "Jane Austen"),
            new WildCardHeadings("Quiz! What is the significance of the Declaration of Independence?", 10, true, true, "It announced the United States' independence from Great Britain"),
            new WildCardHeadings("Quiz! Who was the first Emperor of Rome?", 10, true, true, "Augustus"),
            new WildCardHeadings("Quiz! In which year did the Renaissance begin?", 10, true, true, "14th century"),

// Art/Music Quiz Questions
            new WildCardHeadings("Quiz! Name two famous people with the same initials as yours.", 10, true, true, "Hehe you're a cutie"),
            new WildCardHeadings("Quiz! How many symphonies did Ludwig van Beethoven compose?", 10, true, true, "9"),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'The Mona Lisa'?", 10, true, true, "Leonardo da Vinci"),
            new WildCardHeadings("Quiz! Who composed the famous symphony 'Symphony No. 9'?", 10, true, true, "Ludwig van Beethoven"),
            new WildCardHeadings("Quiz! Who is the lead vocalist of the band Queen?", 10, true, true, "Freddie Mercury"),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'The Starry Night'?", 10, true, true, "Vincent van Gogh"),
            new WildCardHeadings("Quiz! Who wrote the play 'Hamlet'?", 10, true, true, "William Shakespeare"),
            new WildCardHeadings("Quiz! Who composed the famous piano composition 'Für Elise'?", 10, true, true, "Ludwig van Beethoven"),
            new WildCardHeadings("Quiz! Who is the author of the novel 'The Picture of Dorian Gray'?", 10, true, true, "Oscar Wilde"),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'The Last Supper'?", 10, true, true, "Leonardo da Vinci"),
            new WildCardHeadings("Quiz! Who composed the famous ballet 'Swan Lake'?", 10, true, true, "Pyotr Ilyich Tchaikovsky"),
            new WildCardHeadings("Quiz! Who is the lead guitarist of the band Led Zeppelin?", 10, true, true, "Jimmy Page"),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'The Scream'?", 10, true, true, "Edvard Munch"),
            new WildCardHeadings("Quiz! Who composed the famous opera 'The Magic Flute'?", 10, true, true, "Wolfgang Amadeus Mozart"),
            new WildCardHeadings("Quiz! Who is the author of the novel 'War and Peace'?", 10, true, true, "Leo Tolstoy"),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'Guernica'?", 10, true, true, "Pablo Picasso"),
            new WildCardHeadings("Quiz! Who composed the famous symphony 'Symphony No. 5'?", 10, true, true, "Ludwig van Beethoven"),
            new WildCardHeadings("Quiz! Who is the lead singer of the band The Rolling Stones?", 10, true, true, "Mick Jagger"),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'The Birth of Venus'?", 10, true, true, "Sandro Botticelli"),
            new WildCardHeadings("Quiz! Who composed the famous piano composition 'Clair de Lune'?", 10, true, true, "Claude Debussy"),
            new WildCardHeadings("Quiz! Who is the author of the novel 'The Great Gatsby'?", 10, true, true, "F. Scott Fitzgerald"),
            new WildCardHeadings("Quiz! Who painted the famous artwork 'The Persistence of Memory'?", 10, true, true, "Salvador Dalí"),
            new WildCardHeadings("Quiz! How many players are there on a soccer team?", 10, true, true, "11"),
            new WildCardHeadings("Quiz! Who is the Greek god of the sea?", 10, true, true, "Poseidon"),
            new WildCardHeadings("Quiz! How many strings does a standard violin have?", 10, true, true, "4"),


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

