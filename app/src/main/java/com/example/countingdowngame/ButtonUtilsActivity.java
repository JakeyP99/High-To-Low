package com.example.countingdowngame;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ButtonUtilsActivity extends AppCompatActivity {
    protected ButtonUtils btnUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnUtils = ButtonUtils.create(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (btnUtils != null) {
            btnUtils.onDestroy();
        }
    }
}
