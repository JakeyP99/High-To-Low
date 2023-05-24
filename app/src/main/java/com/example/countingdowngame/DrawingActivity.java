package com.example.countingdowngame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.ByteArrayOutputStream;

import yuku.ambilwarna.AmbilWarnaDialog;

public class DrawingActivity extends ButtonUtilsActivity {
    private DrawingView drawingView;
    private Bitmap drawnBitmap;
    private Button btnCancel;
    private Button btnSave;
    private Button colorButton;
    private Button eraserButton;
    private SeekBar penSizeSeekBar;
    private int currentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a3_drawing);

        btnCancel = findViewById(R.id.cancelButton);
        btnSave = findViewById(R.id.saveButton);
        colorButton = findViewById(R.id.colorButton);
        eraserButton = findViewById(R.id.eraserButton);
        penSizeSeekBar = findViewById(R.id.penSizeSeekBar);

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

        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create the color picker dialog
                AmbilWarnaDialog colorPickerDialog = new AmbilWarnaDialog(DrawingActivity.this, currentColor,
                        new AmbilWarnaDialog.OnAmbilWarnaListener() {
                            @Override
                            public void onOk(AmbilWarnaDialog dialog, int color) {
                                // Handle color selection
                                currentColor = color;
                                drawingView.setCurrentColor(currentColor);
                            }

                            @Override
                            public void onCancel(AmbilWarnaDialog dialog) {
                                // Cancelled
                            }
                        });
                colorPickerDialog.show();
            }
        });

        eraserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isEraserMode = !drawingView.isEraserMode();
                drawingView.setEraserMode(isEraserMode);
                eraserButton.setText(isEraserMode ? "Draw" : "Eraser");
            }
        });

        penSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float penSize = (float) progress;
                drawingView.setPenSize(penSize);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not used in this case
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not used in this case
            }
        });
    }

    private String convertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
