package com.example.countingdowngame;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class ButtonUtils {

    public static void setButton(final Button button, final Class<?> activityClass, final AppCompatActivity context, final Runnable buttonAction) {
        final MediaPlayer bop = MediaPlayer.create(context, R.raw.bop);

        button.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                button.setBackground(context.getDrawable(R.drawable.buttonhighlight));
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                button.setBackground(context.getDrawable(R.drawable.outlineforbutton));
                if (activityClass != null) {
                    Intent intent = new Intent(context, activityClass);
                    context.startActivity(intent);
//                context.finish(); // Check if this line is needed
                }
                if (buttonAction != null) {
                    buttonAction.run();
                }
                vibrateDevice(context);
                bop.start();

            }
            return true;
        });
    }
       public static void setImageButton ( final ImageButton imagebutton, final Class<?> activityClass, final Context context, final Runnable buttonAction){
            imagebutton.setOnTouchListener((view, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    imagebutton.setBackground(context.getDrawable(R.drawable.buttonhighlight));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    imagebutton.setBackground(context.getDrawable(R.drawable.outlineforbutton));
                    if (activityClass != null) {
                        Intent intent = new Intent(context, activityClass);
                        context.startActivity(intent);
                    }
                    if (buttonAction != null) {
                        buttonAction.run();
                    }
                    vibrateDevice(context);
                }
                return true;
            });
        }


        public static void vibrateDevice(Context context) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
            }
        }}

