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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a2_player_number_choice);

        initializeViews();
        setupButtonControls();
        setupAudioManagerForMuteButtons(muteGif, soundGif);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetOriginalPlayerField();
        boolean isMuted = getMuteSoundState();
        AudioManager.updateMuteSoundButtonsForBackgroundMusic(isMuted, muteGif, soundGif);
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
        disableAutofill();
        String inputValue = getInputValue();
        if (inputValue.isEmpty()) {
            showToast("You have to have some friends to play with!");
            return;
        }

        try {
            int inputNumber = parseInputValue(inputValue);
            if (inputNumber <= 0) {
                showToast("You have to have some friends to play with!");
                return;
            }

            if (inputValue.length() > 3) {
                showToast("That's way too many players.... Unless you're that popular!");
                return;
            }

            animateAndStartPlayerChoiceActivity(inputNumber);

        } catch (NumberFormatException e) {
            showToast("Invalid player count.");
        }
    }

    private void disableAutofill() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            originalPlayerField.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
        }
    }

    private String getInputValue() {
        return originalPlayerField.getText().toString();
    }

    private int parseInputValue(String inputValue) throws NumberFormatException {
        return Integer.parseInt(inputValue);
    }

    private void animateAndStartPlayerChoiceActivity(int inputNumber) {
        YoYo.with(Techniques.RubberBand)
                .duration(300)
                .onEnd(animator -> {
                    Intent intent = new Intent(this, PlayerChoice.class);
                    intent.putExtra("resetPlayers", true);
                    startActivity(intent);
                })
                .playOn(originalPlayerField);

        originalPlayerField.setFocusable(false);
        Game.getInstance().setPlayers(this, inputNumber);
    }

    private void resetOriginalPlayerField() {
        originalPlayerField.setText("");
        originalPlayerField.setFocusableInTouchMode(true);
        originalPlayerField.setFocusable(true);
    }

    private void showToast(String message) {
        StyleableToast.makeText(getApplicationContext(), message, R.style.newToast).show();
    }
}
