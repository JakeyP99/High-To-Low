package com.example.countingdowngame;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class ButtonUtils {
    public static void setButtonTouchListener(final Button button, final Class<?> activityClass, final Context context) {
        button.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                button.setBackground(context.getDrawable(R.drawable.highlightedbutton));
            }
            else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                button.setBackground(context.getDrawable(R.drawable.outlineforbutton));
                Intent intent = new Intent(context, activityClass);
                context.startActivity(intent);
                Vibrate.vibrateDevice(context);
            }
            return true;
        });
    }

    public static void setButtonTouchListener(final Button button, final View.OnClickListener onClickListener, final Context context) {
        button.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                button.setBackground(context.getDrawable(R.drawable.highlightedbutton));
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                button.setBackground(context.getDrawable(R.drawable.outlineforbutton));
                onClickListener.onClick(view);
                Vibrate.vibrateDevice(context);
            }
            return true;
        });
    }

    public static void setSubmitPlayerButton(final Button btnSubmitPlayers, final View.OnClickListener onClickListener, final Context context) {
        btnSubmitPlayers.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                btnSubmitPlayers.setBackground(context.getDrawable(R.drawable.highlightedbutton));
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                btnSubmitPlayers.setBackground(context.getDrawable(R.drawable.outlineforbutton));
                onClickListener.onClick(view);
                Vibrate.vibrateDevice(context);
            }
            return true;
        });
    }

    public static void setWildCardGenerate(final Button btnNext, final View.OnClickListener onClickListener, final Context context) {
        btnNext.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                btnNext.setBackground(context.getDrawable(R.drawable.highlightedbutton));
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                btnNext.setBackground(context.getDrawable(R.drawable.outlineforbutton));
                onClickListener.onClick(view);
                Vibrate.vibrateDevice(context);
            }
            return true;
        });
    }


}
