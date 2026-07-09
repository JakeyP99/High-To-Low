package com.mydomain.countingdowngame.mainActivity.classAbilities;

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
import java.util.Random;

public class RuneTracingView extends View {

    private Paint targetPaint;
    private Paint userPaint;
    private Path targetPath;
    private Path userPath;

    private List<float[]> targetPoints;
    private List<float[]> userPoints;
    private boolean drawingEnabled = false;

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
        targetPaint.setColor(Color.DKGRAY);
        targetPaint.setStyle(Paint.Style.STROKE);
        targetPaint.setStrokeWidth(18f);
        targetPaint.setAlpha(200);
        targetPaint.setAntiAlias(true);
        targetPaint.setStrokeJoin(Paint.Join.ROUND);
        targetPaint.setStrokeCap(Paint.Cap.ROUND);

        userPaint = new Paint();
        userPaint.setColor(Color.parseColor("#021457")); // bluedark
        userPaint.setStyle(Paint.Style.STROKE);
        userPaint.setStrokeWidth(15f);
        userPaint.setAntiAlias(true);
        userPaint.setStrokeJoin(Paint.Join.ROUND);
        userPaint.setStrokeCap(Paint.Cap.ROUND);

        targetPath = new Path();
        userPath = new Path();
        targetPoints = new ArrayList<>();
        userPoints = new ArrayList<>();
    }

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
        do {
            float length = pm.getLength();
            if (length == 0) continue;
            float distance = 0f;
            float speed = Math.max(1f, length / 100f); // Sample enough points per contour

            while (distance <= length) {
                float[] pos = new float[2];
                pm.getPosTan(distance, pos, null);
                pointsList.add(pos);
                distance += speed;
            }
        } while (pm.nextContour());
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        float scaleX = (float) getWidth() / 300f;
        float scaleY = (float) getHeight() / 300f;
        float scale = Math.min(scaleX, scaleY);

        // Calculate offsets to center the 300x300 area
        float offsetX = (getWidth() - 300f * scale) / 2f;
        float offsetY = (getHeight() - 300f * scale) / 2f;

        canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.scale(scale, scale);

        if (!targetPath.isEmpty()) {
            canvas.drawPath(targetPath, targetPaint);
        }
        canvas.drawPath(userPath, userPaint);

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!drawingEnabled) return false;

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

    /**
     * Calculates similarity percentage between 0 and 100.
     */
    public float calculateSimilarity() {
        if (userPoints.isEmpty() || targetPoints.isEmpty()) return 0;

        // 1. Precision: How close were the user's points to the target?
        float totalUserToTargetDist = 0;
        for (float[] uPoint : userPoints) {
            float minDistance = Float.MAX_VALUE;
            for (float[] tPoint : targetPoints) {
                float dist = (float) Math.sqrt(Math.pow(uPoint[0] - tPoint[0], 2) + Math.pow(uPoint[1] - tPoint[1], 2));
                if (dist < minDistance) {
                    minDistance = dist;
                }
            }
            totalUserToTargetDist += minDistance;
        }
        float avgPrecisionDist = totalUserToTargetDist / userPoints.size();

        // 2. Coverage: How much of the target rune was actually traced?
        int reachedPoints = 0;
        float reachThreshold = 18f; // Stricter threshold

        for (float[] tPoint : targetPoints) {
            boolean reached = false;
            for (float[] uPoint : userPoints) {
                float dist = (float) Math.sqrt(Math.pow(uPoint[0] - tPoint[0], 2) + Math.pow(uPoint[1] - tPoint[1], 2));
                if (dist < reachThreshold) {
                    reached = true;
                    break;
                }
            }
            if (reached) reachedPoints++;
        }
        float coverageRatio = (float) reachedPoints / targetPoints.size();

        // 3. Final Calculation
        // Normalize precision: 0-45 pixels is 100-0% precision
        float maxAllowedPrecisionDist = 45f;
        float precisionScore = Math.max(0, 100 - (avgPrecisionDist / maxAllowedPrecisionDist * 100));

        // Similarity is weighted by coverage.
        float finalSimilarity = precisionScore * coverageRatio;

        return finalSimilarity;
    }


    static Path generateRandomRune() {

        Path path = new Path();
        Random r = new Random();

        int type = r.nextInt(7);

        switch (type) {

            case 0: // Hexagram (magic star)
                path.moveTo(150, 20);
                path.lineTo(280, 250);
                path.lineTo(20, 250);
                path.close();

                path.moveTo(20, 50);
                path.lineTo(280, 50);
                path.lineTo(150, 280);
                path.close();
                break;


            case 1: // Crescent moon rune
                path.moveTo(210, 40);

                for (int i = 0; i <= 180; i++) {
                    double angle = Math.toRadians(i);

                    float x = (float) (150 + 100 * Math.cos(angle));
                    float y = (float) (150 + 100 * Math.sin(angle));

                    path.lineTo(x, y);
                }

                for (int i = 180; i >= 0; i--) {

                    double angle = Math.toRadians(i);

                    float x = (float) (180 + 70 * Math.cos(angle));
                    float y = (float) (150 + 70 * Math.sin(angle));

                    path.lineTo(x, y);
                }

                path.close();
                break;


            case 2: // Eye rune
                path.moveTo(40, 150);

                for (int i = 0; i <= 360; i++) {

                    double t = Math.toRadians(i);

                    float x = (float) (150 + 110 * Math.cos(t));
                    float y = (float) (150 + 60 * Math.sin(t));

                    path.lineTo(x, y);
                }

                path.close();

                // pupil
                path.addCircle(150, 150, 30, Path.Direction.CW);

                break;

            case 3: // Rune tree
                path.moveTo(150, 280);
                path.lineTo(150, 70);

                path.moveTo(150, 100);
                path.lineTo(80, 170);

                path.moveTo(150, 140);
                path.lineTo(220, 210);

                path.moveTo(150, 190);
                path.lineTo(90, 240);

                path.moveTo(150, 220);
                path.lineTo(230, 260);

                break;


            case 4: // Diamond rune
                path.moveTo(150, 20);
                path.lineTo(270, 150);
                path.lineTo(150, 280);
                path.lineTo(30, 150);
                path.close();

                path.moveTo(150, 70);
                path.lineTo(210, 150);
                path.lineTo(150, 230);
                path.lineTo(90, 150);
                path.close();

                break;


            case 5: // Lightning rune
                path.moveTo(180, 20);
                path.lineTo(80, 150);
                path.lineTo(150, 150);
                path.lineTo(90, 280);
                path.lineTo(230, 120);
                path.lineTo(160, 120);
                path.close();

                break;


            case 6: // Viking style rune
                path.moveTo(100, 40);
                path.lineTo(100, 260);

                path.moveTo(100, 80);
                path.lineTo(230, 80);

                path.moveTo(100, 160);
                path.lineTo(200, 260);

                path.moveTo(100, 160);
                path.lineTo(220, 40);

                break;
        }

        return path;
    }

}
