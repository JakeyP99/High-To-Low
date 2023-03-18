package com.example.countingdowngame;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class EndActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        // Do nothing
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lose_layout);


        final ListView previousNumbersList = findViewById(R.id.previousNumbers);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(EndActivity.this,
                android.R.layout.simple_list_item_1, MainActivity.gameInstance.getPreviousNumbersFormatted());
        previousNumbersList.setAdapter(adapter);

        final Button btnPlayAgain = findViewById(R.id.btnplayAgain);
        final Button btnNewPlayer = (Button) findViewById(R.id.btnNewPlayer);

        btnNewPlayer.setOnClickListener(v -> startActivity(new Intent(EndActivity.this, PlayerNumber.class)));


        btnPlayAgain.setOnClickListener(v -> {
            MainActivity.gameInstance.playAgain();

            startActivity(new Intent(EndActivity.this, NumberChoice.class));
        });


    }
}
