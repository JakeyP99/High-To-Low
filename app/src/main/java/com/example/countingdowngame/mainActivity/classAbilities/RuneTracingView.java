package com.example.countingdowngame.mainActivity.classAbilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class RuneTracingView extends View {

    private Paint targetPaint;
    private Paint userPaint;
    private Path targetPath;
    private Path userPath;
    
    private List<float[]> targetPoints;
    private List<float[]> userPoints;

    public RuneTracingView(Context context) {
        super(context);
        init();
    }

    public RuneTracingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        targetPaint = new Paint();
        targetPaint.setColor(Color.LTGRAY);
        targetPaint.setStyle(Paint.Style.STROKE);
        targetPaint.setStrokeWidth(15f);
        targetPaint.setAlpha(100);
        targetPaint.setAntiAlias(true);
        targetPaint.setStrokeJoin(Paint.Join.ROUND);
        targetPaint.setStrokeCap(Paint.Cap.ROUND);

        userPaint = new Paint();
        userPaint.setColor(Color.parseColor("#021457")); // bluedark
        userPaint.setStyle(Paint.Style.STROKE);
        userPaint.setStrokeWidth(12f);
        userPaint.setAntiAlias(true);
        userPaint.setStrokeJoin(Paint.Join.ROUND);
        userPaint.setStrokeCap(Paint.Cap.ROUND);

        targetPath = new Path();
        userPath = new Path();
        targetPoints = new ArrayList<>();
        userPoints = new ArrayList<>();
    }

    private boolean drawingEnabled = false;

    public void setDrawingEnabled(boolean enabled) {
        this.drawingEnabled = enabled;
    }

    public void setRune(Path path) {
        this.targetPath = path;
        samplePathPoints(targetPath, targetPoints);
        invalidate();
    }

    public void hideTargetRune() {
        this.targetPath = new Path();
        invalidate();
    }

    private void samplePathPoints(Path path, List<float[]> pointsList) {
        pointsList.clear();
        PathMeasure pm = new PathMeasure(path, false);
        float length = pm.getLength();
        if (length == 0) return;
        float distance = 0f;
        float speed = length / 200f; // Sample more points for accuracy

        while (distance <= length) {
            float[] pos = new float[2];
            pm.getPosTan(distance, pos, null);
            pointsList.add(pos);
            distance += speed;
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // Center and scale everything to fit the view size
        float scaleX = (float) getWidth() / 300f;
        float scaleY = (float) getHeight() / 300f;
        float scale = Math.min(scaleX, scaleY);

        canvas.save();
        canvas.scale(scale, scale, getWidth() / 2f, getHeight() / 2f);

        if (!targetPath.isEmpty()) {
            canvas.drawPath(targetPath, targetPaint);
        }
        canvas.drawPath(userPath, userPaint);

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!drawingEnabled) return false;

        // Inverse scale the touch coordinates to match the 300x300 internal coordinate system
        float scaleX = (float) getWidth() / 300f;
        float scaleY = (float) getHeight() / 300f;
        float scale = Math.min(scaleX, scaleY);

        float offsetX = (getWidth() - 300f * scale) / 2f;
        float offsetY = (getHeight() - 300f * scale) / 2f;

        float x = (event.getX() - offsetX) / scale;
        float y = (event.getY() - offsetY) / scale;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                userPath.moveTo(x, y);
                userPoints.add(new float[]{x, y});
                break;
            case MotionEvent.ACTION_MOVE:
                userPath.lineTo(x, y);
                userPoints.add(new float[]{x, y});
                break;
        }
        invalidate();
        return true;
    }

    public void reset() {
        userPath.reset();
        userPoints.clear();
        invalidate();
    }

    /**
     * Calculates similarity percentage between 0 and 100.
     */
    public float calculateSimilarity() {
        if (userPoints.isEmpty() || targetPoints.isEmpty()) return 0;

        float totalDistance = 0;
        int count = 0;

        // For each user point, find the distance to the closest target point
        for (float[] uPoint : userPoints) {
            float minDistance = Float.MAX_VALUE;
            for (float[] tPoint : targetPoints) {
                float dist = (float) Math.sqrt(Math.pow(uPoint[0] - tPoint[0], 2) + Math.pow(uPoint[1] - tPoint[1], 2));
                if (dist < minDistance) {
                    minDistance = dist;
                }
            }
            totalDistance += minDistance;
            count++;
        }

        float averageDistance = totalDistance / count;
        
        // Normalize distance to a percentage. 
        // Let's say 100 pixels average distance is 0% similarity, and 0 pixels is 100%.
        float maxAllowedDist = 150f; 
        float similarity = Math.max(0, 100 - (averageDistance / maxAllowedDist * 100));
        
        return similarity;
    }
}
