package com.example.countingdowngame.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class DrawingViewPlayerModels extends View {

    private Path drawingPath;
    private Paint drawingPaint;
    private Canvas drawingCanvas;
    private Bitmap canvasBitmap;
    private int currentColor;
    private boolean isEraserMode;
    private float penSize;
    private float startX, startY;
    private float eraserSize = 90f;

    public DrawingViewPlayerModels(Context context) {
        super(context);
        setupDrawing();
    }

    public DrawingViewPlayerModels(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    public DrawingViewPlayerModels(Context context, AttributeSet attrs, int defStyleAttr) {
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
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(canvasBitmap, 0, 0, drawingPaint);
        canvas.drawPath(drawingPath, drawingPaint);
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

    public void setEraserMode(boolean eraserMode) {
        this.isEraserMode = eraserMode;
        if (eraserMode) {
            drawingPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            drawingPaint.setStrokeWidth(eraserSize);
        } else {
            drawingPaint.setColor(currentColor);
            drawingPaint.setStrokeWidth(penSize);
        }
    }

    public void setPenSize(float size) {
        penSize = size;
        drawingPaint.setStrokeWidth(penSize);
    }

    public void setEraserSize(float size) {
        this.eraserSize = size;
        if (isEraserMode) {
            drawingPaint.setStrokeWidth(size);
        }
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
                startX = touchX;
                startY = touchY;
                drawingPath.moveTo(touchX, touchY);
                break;

            case MotionEvent.ACTION_MOVE:
                drawingPath.lineTo(touchX, touchY);
                break;

            case MotionEvent.ACTION_UP:
                // If movement was very minimal, treat as a dot
                if (Math.abs(touchX - startX) < 5 && Math.abs(touchY - startY) < 5) {
                    drawingCanvas.drawCircle(touchX, touchY, penSize / 2, drawingPaint);
                } else {
                    drawingCanvas.drawPath(drawingPath, drawingPaint);
                }
                drawingPath.reset();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

}
