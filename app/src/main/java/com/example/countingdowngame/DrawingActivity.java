package com.example.countingdowngame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
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
        initializeViews();
        setupButtonListeners();
        setupSeekBarListener();
    }

    private void initializeViews() {
        btnCancel = findViewById(R.id.cancelButton);
        btnSave = findViewById(R.id.saveButton);
        colorButton = findViewById(R.id.colorButton);
        eraserButton = findViewById(R.id.eraserButton);
        penSizeSeekBar = findViewById(R.id.penSizeSeekBar);
        drawingView = findViewById(R.id.drawingView);
        setDefaultPenSize();
    }

    private void setupButtonListeners() {
        btnUtils.setButton(btnSave, () -> {
            saveDrawing();
        });
        btnUtils.setButton(btnCancel, () -> {
            cancelDrawing();
        });
        btnUtils.setButton(colorButton, () -> {
            showColorPickerDialog();
            setDefaultPenSize();
        });
        btnUtils.setButton(eraserButton, () -> {
            toggleEraserMode();
        });
    }
    //-----------------------------------------------------Seekbar Functionality---------------------------------------------------//
    private void setupSeekBarListener () {
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
    //-----------------------------------------------------Button Functionality---------------------------------------------------//

        private void saveDrawing () {
            drawnBitmap = drawingView.getDrawingBitmap();
            String drawnBitmapString = convertBitmapToString(drawnBitmap);

            Intent intent = new Intent();
            intent.putExtra("drawnBitmap", drawnBitmapString);
            setResult(RESULT_OK, intent);
            finish();
        }

        private void cancelDrawing () {
            setResult(RESULT_CANCELED);
            finish();
        }

        private void showColorPickerDialog () {
            AmbilWarnaDialog colorPickerDialog = new AmbilWarnaDialog(DrawingActivity.this, currentColor,
                    new AmbilWarnaDialog.OnAmbilWarnaListener() {
                        @Override
                        public void onOk(AmbilWarnaDialog dialog, int color) {
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

        private void toggleEraserMode () {
            boolean isEraserMode = !drawingView.isEraserMode();
            drawingView.setEraserMode(isEraserMode);
            eraserButton.setText(isEraserMode ? "Draw\nMode" : "Eraser\nMode");
        }

    private void setDefaultPenSize() {
        int maxProgress = penSizeSeekBar.getMax();
        int defaultProgress = (int) (maxProgress * 0.8); // Calculate 80% of the maxProgress
        penSizeSeekBar.setProgress(defaultProgress);
        float penSize = calculatePenSizeFromProgress(defaultProgress);
        drawingView.setPenSize(penSize);
    }

    private float calculatePenSizeFromProgress(int progress) {
        float maxPenSize = 20.0f; // Maximum pen size
        float progressRatio = (float) progress / penSizeSeekBar.getMax();
        return maxPenSize * progressRatio;
    }


    //-----------------------------------------------------Convert to bitmap Functionality---------------------------------------------------//
        private String convertBitmapToString (Bitmap bitmap){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
    }
