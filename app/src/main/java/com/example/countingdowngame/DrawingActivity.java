package com.example.countingdowngame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;

public class DrawingActivity extends ButtonUtilsActivity {

    private DrawingView drawingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        drawingView = findViewById(R.id.drawingView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.saveButton) {
            Bitmap drawnBitmap = drawingView.getDrawingBitmap();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("drawnBitmap", drawnBitmap);
            setResult(RESULT_OK, resultIntent);
            finish();
            return true;
        } else if (item.getItemId() == R.id.cancelButton) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
