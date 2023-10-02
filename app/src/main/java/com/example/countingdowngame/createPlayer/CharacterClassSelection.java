package com.example.countingdowngame.createPlayer;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countingdowngame.R;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.util.ArrayList;
import java.util.List;

public class CharacterClassSelection extends ButtonUtilsActivity {

    private RecyclerView recyclerView;
    private CharacterClassAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.characterclass_selection);

        recyclerView = findViewById(R.id.playerRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create a list of character classes (Rabbit and Lion)
        List<CharacterClassStore> characterClasses = new ArrayList<>();
        characterClasses.add(new CharacterClassStore("Rabbit", "Special Ability 1"));
        characterClasses.add(new CharacterClassStore("Lion", "Special Ability 2"));

        // Initialize the adapter and set it to the RecyclerView
        adapter = new CharacterClassAdapter(characterClasses);
        recyclerView.setAdapter(adapter);
    }
}
