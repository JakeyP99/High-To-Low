package com.example.countingdowngame;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

public class ImageUtil {

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        int diameter = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap circularBitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Canvas canvas = new Canvas(circularBitmap);
        canvas.drawOval(new RectF(0, 0, diameter, diameter), paint);

        return circularBitmap;
    }
}
