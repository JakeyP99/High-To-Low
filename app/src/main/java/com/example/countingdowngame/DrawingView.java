package com.example.countingdowngame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {

    private Path drawingPath;
    private Paint drawingPaint;
    private Canvas drawingCanvas;
    private Bitmap canvasBitmap;
    private int currentColor;
    private boolean isEraserMode;
    private float penSize;

    public DrawingView(Context context) {
        super(context);
        setupDrawing();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupDrawing();
    }

    private void setupDrawing() {
        drawingPath = new Path();
        drawingPaint = new Paint();

        currentColor = Color.BLACK;
        isEraserMode = false;
        penSize = 5;

        drawingPaint.setColor(currentColor);
        drawingPaint.setAntiAlias(true);
        drawingPaint.setStrokeWidth(penSize);
        drawingPaint.setStyle(Paint.Style.STROKE);

        drawingCanvas = new Canvas();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawingCanvas.setBitmap(canvasBitmap);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(canvasBitmap, 0, 0, drawingPaint);
        canvas.drawPath(drawingPath, drawingPaint);
    }

    public void clearCanvas() {
        canvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        drawingCanvas.setBitmap(canvasBitmap);
        drawingPath.reset();
        invalidate();
    }

    public void setCurrentColor(int color) {
        currentColor = color;
        if (!isEraserMode) {
            drawingPaint.setColor(currentColor);
        }
    }
    public boolean isEraserMode() {
        return isEraserMode;
    }
    public void setEraserMode(boolean enabled) {
        isEraserMode = enabled;
        if (isEraserMode) {
            drawingPaint.setColor(Color.WHITE);  // Set the color to white for eraser
        } else {
            drawingPaint.setColor(currentColor); // Restore the previous color
        }
    }

    public void setPenSize(float size) {
        penSize = size;
        drawingPaint.setStrokeWidth(penSize);
    }

    public Bitmap getDrawingBitmap() {
        return canvasBitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawingPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawingPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawingCanvas.drawPath(drawingPath, drawingPaint);
                drawingPath.reset();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }
}
