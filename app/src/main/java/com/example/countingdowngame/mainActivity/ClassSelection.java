package com.example.countingdowngame.mainActivity;

import android.os.Bundle;

import com.example.countingdowngame.R;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

public class ClassSelection extends ButtonUtilsActivity {

//    private CharacterSelection characterSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.character_selection);

//        // Retrieve the selected player from the previous screen
//        Player selectedPlayer = getIntent().getParcelableExtra("selectedPlayer");
//
//        // Create a CharacterSelection object with the selected player
//        characterSelection = new CharacterSelection(selectedPlayer);
//
//        // Initialize UI elements for character class selection
//        Button class1Button = findViewById(R.id.class1Button);
//        Button class2Button = findViewById(R.id.class2Button);
//
//        // Set click listeners to handle character class selection
//        class1Button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Set the selected character class (e.g., Class 1)
//                characterSelection.setSelectedCharacterClass(CharacterClass.CLASS1);
//
//                // Proceed to the game or the next screen
//                // You can start the game activity here with the characterSelection object
//            }
//        });
//
//        class2Button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Set the selected character class (e.g., Class 2)
//                characterSelection.setSelectedCharacterClass(CharacterClass.CLASS2);
//
//                // Proceed to the game or the next screen
//                // You can start the game activity here with the characterSelection object
//            }
//        });
    }
}
