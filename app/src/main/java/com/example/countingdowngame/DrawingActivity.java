package com.example.countingdowngame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;

import java.io.ByteArrayOutputStream;

public class DrawingActivity extends ButtonUtilsActivity {

    private DrawingView drawingView;
    private Bitmap drawnBitmap;

    private Button btnCancel;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a3_drawing);

        btnCancel = findViewById(R.id.cancelButton);
        btnSave = findViewById(R.id.saveButton);

        drawingView = findViewById(R.id.drawingView);

        btnSave.setOnClickListener(view -> {
            drawnBitmap = drawingView.getDrawingBitmap();
            String drawnBitmapString = convertBitmapToString(drawnBitmap);

            Intent intent = new Intent();
            intent.putExtra("drawnBitmap", drawnBitmapString);
            setResult(RESULT_OK, intent);
            finish();
        });

        btnCancel.setOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private String convertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
