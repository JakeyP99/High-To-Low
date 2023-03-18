package com.example.countingdowngame;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    // This sets the new game.
    static Game gameInstance = new Game();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final MediaPlayer bop = MediaPlayer.create(this, R.raw.bop);
        final TextView numberText = findViewById(R.id.numberText);
        final Button btnGenerate = findViewById(R.id.btnGenerate);
        final TextView nextPlayerText = findViewById(R.id.TextView_nextplayer);
        final Button btnSkip = findViewById(R.id.btnSkip);

        // This sets a new playerEventListener, which is linked to the skip button. So the app knows when that button is clicked, it provides a functionality to go to the next player (we made the functionality below)
        this.gameInstance.setPlayerEventListener(new PlayerEventListener() {
            @Override
            public void onPlayerEvent(PlayerEvent e) {
                switch (e.type) {
                    case SKIP: {
                        gameInstance.nextPlayer();
                        break;

                    }
                }
            }
        });

        //This is the functionality mentioned above. Note getCurrentPlayer is found in Game class. Note gameInstance means that it is set for a brand new game.
        this.gameInstance.setGameEventListener(new GameEventListener() {
            @Override
            public void onGameEvent(GameEvent e) {
                switch (e.type) {
                    case NEXT_PLAYER: {
                        nextPlayerText.setText("Player " + (gameInstance.getCurrentPlayer().getName()) + "'s" + " Turn");

                        if (gameInstance.getCurrentPlayer().getSkipAmount() > 0) {
                            btnSkip.setVisibility(View.VISIBLE);
                        } else {
                            btnSkip.setVisibility(View.INVISIBLE);
                        }

                        break;
                    }

                    case GAME_END: {
                        startActivity(new Intent(MainActivity.this, EndActivity.class));
                    }
                    case NEXT_NUMBER: {
                        numberText.setText(String.valueOf(gameInstance.currentNumber));
                    }
                    case GAME_START: {
                        numberText.setText(Integer.toString(gameInstance.currentNumber));

                        nextPlayerText.setText("Player " + (gameInstance.getCurrentPlayer().getName()) + "'s" + " Turn");
                    }
                }
            }
        });

        gameInstance.startGame(NumberChoice.startingNumber);


        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        vibrator.vibrate(500);
                    }
                }

                bop.start();

                gameInstance.nextNumber();

            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameInstance.getCurrentPlayer().useSkip();//
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(500);
                    }
                }

                bop.start();
            }
        });
    }
}
