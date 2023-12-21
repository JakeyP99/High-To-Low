package com.example.countingdowngame.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.example.countingdowngame.R;

public class DonutProgressBar extends ProgressBar {
    private RectF rectF;
    private Paint paint;

    public DonutProgressBar(Context context) {
        super(context);
        init();
    }

    public DonutProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DonutProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        rectF = new RectF();
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        rectF.set(0, 0, getWidth(), getHeight());
        float angle = (float) (360 * getProgress()) / getMax();
        paint.setColor(getResources().getColor(R.color.colorPrimary)); // Progress color
        canvas.drawArc(rectF, -90, angle, true, paint);
    }
}
