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

        drawingPaint.setColor(Color.BLACK);
        drawingPaint.setAntiAlias(true);
        drawingPaint.setStrokeWidth(5);
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

    public void clearDrawing() {
        drawingCanvas.drawColor(Color.WHITE);
        invalidate();
    }

    public Bitmap getDrawingBitmap() {
        return canvasBitmap;
    }
}
