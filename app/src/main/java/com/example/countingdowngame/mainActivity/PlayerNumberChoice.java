package com.example.countingdowngame.mainActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.countingdowngame.R;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.utils.AudioManager;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import io.github.muddz.styleabletoast.StyleableToast;
import pl.droidsonroids.gif.GifImageView;

public class PlayerNumberChoice extends ButtonUtilsActivity {
    private EditText originalPlayerField;
    private GifImageView muteGif;
    private GifImageView soundGif;

    @Override
    protected void onResume() {
        super.onResume();
        originalPlayerField.setText("");
        originalPlayerField.setFocusableInTouchMode(true);
        originalPlayerField.setFocusable(true);
        boolean isMuted = getMuteSoundState();
        AudioManager.updateMuteSoundButtonsForBackgroundMusic(isMuted, muteGif, soundGif);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a2_player_number_choice);

        initializeViews();
        setupButtonControls();
        setupAudioManagerForMuteButtons(muteGif, soundGif);
    }

    private void initializeViews() {
        muteGif = findViewById(R.id.muteGif);
        soundGif = findViewById(R.id.soundGif);
        originalPlayerField = findViewById(R.id.EditTextViewplayernumber);
    }


    private void setupButtonControls() {
        Button btnSubmitPlayers = findViewById(R.id.btnSubmitPlayers);
        btnUtils.setButton(btnSubmitPlayers, this::submitPlayerNumber);
    }


    private void submitPlayerNumber() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            originalPlayerField.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO); // Disable Google autofill
        }

        String inputValue = originalPlayerField.getText().toString();
        if (inputValue.isEmpty()) {
            StyleableToast.makeText(getApplicationContext(), "You have to have some friends to play with!", R.style.newToast).show();
            return;
        }

        try {
            int inputNumber = Integer.parseInt(inputValue);
            if (inputNumber <= 0) {
                return;
            }

            if (inputValue.length() > 3) {
                StyleableToast.makeText(getApplicationContext(), "That's way too many players.... Unless you're that popular?", R.style.newToast).show();

                return;
            }


            YoYo.with(Techniques.RubberBand)
                    .duration(300)
                    .onEnd(animator -> {
                        // Animation has ended, start the MainActivity here
                        Intent intent = new Intent(this, PlayerChoice.class);
                        intent.putExtra("resetPlayers", true); // Do not reset the PlayerChoice activity
                        startActivity(getIntentForClass(PlayerChoice.class));

                    })
                    .playOn(originalPlayerField);

            originalPlayerField.setFocusable(false);
            Game.getInstance().setPlayers(this, inputNumber);

        } catch (NumberFormatException e) {
            StyleableToast.makeText(getApplicationContext(), "Invalid player count.", R.style.newToast).show();
        }
    }
}