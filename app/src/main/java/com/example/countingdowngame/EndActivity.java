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
        startActivity(new Intent(EndActivity.this, HomeScreen.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lose_layout);

        final ListView previousNumbersList = findViewById(R.id.previousNumbers);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(EndActivity.this, R.layout.custom_list_item, R.id.previousNumbers, MainActivity.gameInstance.getPreviousNumbersFormatted());
        previousNumbersList.setAdapter(adapter);

        final Button btnPlayAgain = findViewById(R.id.btnplayAgain);
        final Button btnNewPlayer = findViewById(R.id.btnNewPlayer);

        ButtonUtils.setButton(btnNewPlayer,null, PlayerNumber.class, this, null);

        ButtonUtils.setButton(btnPlayAgain,null, NumberChoice.class, this, () -> {
            MainActivity.gameInstance.playAgain();
        });


    }
}
