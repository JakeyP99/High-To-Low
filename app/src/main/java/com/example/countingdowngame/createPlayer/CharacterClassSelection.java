package com.example.countingdowngame.createPlayer;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.util.ArrayList;
import java.util.List;

public class CharacterClassSelection extends ButtonUtilsActivity {

    Button btnProceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.characterclass_selection);


        RecyclerView recyclerView = findViewById(R.id.classRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create a list of character classes (Rabbit and Lion)
        List<CharacterClassStore> characterClasses = new ArrayList<>();
        characterClasses.add(new CharacterClassStore("Rabbit", "You can deny two drinks handed out to you."));
        characterClasses.add(new CharacterClassStore("Witch", "At any point in the game, you can change the current number to be whatever you want."));
        characterClasses.add(new CharacterClassStore("Scientist", "For every even number you land on, you can hand out two drinks, but for every odd you have to take a drink."));

        // Initialize the adapter and set it to the RecyclerView
        CharacterClassAdapter adapter = new CharacterClassAdapter(characterClasses);
        recyclerView.setAdapter(adapter);

        btnUtils.setButton(btnProceed, () -> {
            onBackPressed();
            Log.d("btnProceed", "Proceeded back to player select");
        });
    }
}
