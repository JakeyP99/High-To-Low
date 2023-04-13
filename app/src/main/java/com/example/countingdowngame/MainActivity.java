package com.example.countingdowngame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // This means you can't go back
    @Override
    public void onBackPressed() {
        // Do nothing
    }

    // This sets the new game.
    static Game gameInstance = new Game();
    private TextView numberText;
    private TextView nextPlayerText;
    private Button btnSkip;
    private Button btnWild;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View wildText = findViewById(R.id.wild_textview);
        final MediaPlayer bop = MediaPlayer.create(this, R.raw.bop);

        numberText = findViewById(R.id.numberText);
        Button btnGenerate = findViewById(R.id.btnGenerate);
        nextPlayerText = findViewById(R.id.textView_Number_Turn);
        btnSkip = findViewById(R.id.btnSkip);
        btnWild = findViewById(R.id.btnWild);

        //These are the button controls
        ButtonUtils.setButtonNoClass(btnGenerate, null, this, () -> {
            gameInstance.nextNumber();
            bop.start();
            wildText.setVisibility(View.INVISIBLE);
            numberText.setVisibility(View.VISIBLE);
            nextPlayerText.setVisibility(View.VISIBLE);
            numberText.setVisibility(View.VISIBLE);
        });

        ButtonUtils.setButtonNoClass(btnSkip, null, this, () -> {
            gameInstance.getCurrentPlayer().useSkip();
            bop.start();
        });

        ButtonUtils.setButtonNoClass(btnWild, null, this, () -> {
            btnWild.setVisibility(View.INVISIBLE);
            wildText.setVisibility(View.VISIBLE);
            nextPlayerText.setVisibility(View.INVISIBLE);
            btnSkip.setVisibility(View.INVISIBLE);
            numberText.setVisibility(View.INVISIBLE);
            bop.start();

            wildCardActivate();
        });


        // This sets a new playerEventListener, which is linked to the skip button. So the app knows when that button is clicked, it provides a functionality to go to the next player (we made the functionality below)
        gameInstance.setPlayerEventListener(e -> {
            if (e.type == PlayerEventType.SKIP) {
                gameInstance.nextPlayer();
            }
        });

        //This is the functionality mentioned above. Note getCurrentPlayer is found in Game class. Note gameInstance means that it is set for a brand new game.
        gameInstance.setGameEventListener(e -> {
            switch (e.type) {
                case NEXT_PLAYER: {
                    renderPlayer();
                    break;
                }

                case GAME_END: {
                    gameInstance.endGame();
                    startActivity(new Intent(MainActivity.this, EndActivity.class));
                    break;
                }

                case NEXT_NUMBER: {
                    numberText.setText(String.valueOf(gameInstance.currentNumber));
                    break;
                }
                case GAME_START: {
                    break;
                }
            }
        });

        gameInstance.startGame(NumberChoice.startingNumber);
        numberText.setText(Integer.toString(gameInstance.currentNumber));
        renderPlayer();
    }

    private void renderPlayer() {
        TextView playerTurnTextView = findViewById(R.id.textView_Number_Turn);
        nextPlayerText.setText("Player " + (gameInstance.getCurrentPlayer().getName()) + "'s" + " Turn");

        if (gameInstance.getCurrentPlayer().getSkipAmount() > 0) {
            btnSkip.setVisibility(View.VISIBLE);
        } else {
            btnSkip.setVisibility(View.INVISIBLE);
        }

        if (gameInstance.getCurrentPlayer().getWildCardAmount() > 0) {
            btnWild.setVisibility(View.VISIBLE);
        } else {
            btnWild.setVisibility(View.INVISIBLE);
        }
    }


    public void wildCardActivate() {
        final TextView wildActivityTextView = findViewById(R.id.wild_textview);
        gameInstance.getCurrentPlayer().useWildCard();

        String[] wildActivities = {
                "Take 1 drink.", "Take 2 drinks.", "Take 3 drinks.", "Finish your drink.",
                "Give 1 drink.", "Give 2 drinks.", "Give 3 drinks.", "Choose a player to finish their drink.",
                "The player to the left takes a drink.",
                "The player to the right takes a drink.",
                "The oldest player takes 2 drinks.",
                "The youngest player takes 2 drinks.",
                "The player who last peed takes 3 drinks.",
                "The player with the oldest car takes 2 drinks.",
                "Whoever last rode on a train takes 2 drinks.",
                "Anyone who is standing takes 4 drinks, why are you standing? Sit down mate.",
                "Anyone who is sitting takes 2 drinks.",
                "Whoever has the longest hair takes 2 drinks.",
                "Whoever is wearing a watch takes 2 drinks.",
                "Whoever has a necklace on takes 2 drinks.",
                "Double the ending drink, whoever loses must now do double the consequence."
        };
        Random random = new Random();
        int index = random.nextInt(wildActivities.length);
        String selectedActivity = wildActivities[index];
        wildActivityTextView.setText(selectedActivity);
    }
}
