package com.example.countingdowngame;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ButtonUtilsActivity extends AppCompatActivity {
    protected ButtonUtils btnUtils;
    protected Drawable buttonHighlightDrawable;
    protected Drawable outlineForButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnUtils = new ButtonUtils(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (btnUtils != null) {
            btnUtils.onDestroy();
        }
    }
    //-----------------------------------------------------Activity Functionality---------------------------------------------------//
    protected Intent getIntentForClass(Class<?> targetClass) {
        return getIntentForClass(targetClass, false);
    }
    protected Intent getIntentForClass(Class<?> targetClass, boolean clearTop) {
        Intent i = new Intent(this, targetClass);
        if (clearTop) {
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        return i;
    }
    protected void gotoHomeScreen() {
        startActivity(getIntentForClass(HomeScreen.class, true));
    }

    protected void gotoGameEnd() {
        startActivity(getIntentForClass(EndActivityGame.class, true));
    }

    protected void gotoGameStart() {
        startActivity(getIntentForClass(NumberChoiceForGame.class, true));
    }

    protected void gotoGameSetup() {
        startActivity(getIntentForClass(PlayerNumberChoice.class, true));
    }

    protected void gotoInstructions() {
        startActivity(getIntentForClass(InstructionsToPlay.class, true));
    }

    protected void gotoSettings() {
        startActivity(getIntentForClass(SettingsContainer.class, true));
    }

    //-----------------------------------------------------Sound Functionality---------------------------------------------------//

    public void onMuteClicked(View view) {
        Button btnMute = (Button) view;
        boolean isMuted = !btnMute.isSelected();
        btnMute.setSelected(isMuted);
        Drawable selectedDrawable = isMuted ? buttonHighlightDrawable : outlineForButton;
        btnMute.setBackground(selectedDrawable);
        btnUtils.toggleMute();
    }

    public void onPayForWildCardClicked(View view) {
        Button btnPayForWildCard = (Button) view;
        boolean isMuted = !btnPayForWildCard.isSelected();
        btnPayForWildCard.setSelected(isMuted);
        Drawable selectedDrawable = isMuted ? buttonHighlightDrawable : outlineForButton;
        btnPayForWildCard.setBackground(selectedDrawable);
        btnUtils.togglePayForWildCard();
    }

}
