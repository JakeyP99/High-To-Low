package com.example.countingdowngame;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.widget.Button;

public class ButtonUtils {
    public static void setButtonNoClass(final Button button, final Class<?> activityClass, final Context context, final Runnable buttonAction) {
        button.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                button.setBackground(context.getDrawable(R.drawable.buttonhighlight));
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                button.setBackground(context.getDrawable(R.drawable.outlineforbutton));
                if (activityClass != null) {
                    Intent intent = new Intent(context, activityClass);
                    context.startActivity(intent);
                }
                if (buttonAction != null) {
                    buttonAction.run();
                }
                Vibrate.vibrateDevice(context);
            }
            return true;
        });
    }
}

