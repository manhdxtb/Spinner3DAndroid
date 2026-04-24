package com.app.spinner.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

import java.io.InputStream;

public class BitmapUtils {

    public static Bitmap getTintedBitmapFromAsset(Context context, String assetPath, int color) {
        try {
            String path = assetPath.replace("file:///android_asset/", "");
            InputStream is = context.getAssets().open(path);
            Bitmap original = BitmapFactory.decodeStream(is);
            
            Bitmap tinted = Bitmap.createBitmap(original.getWidth(), original.getHeight(), original.getConfig());
            Canvas canvas = new Canvas(tinted);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            if (color != 0 && color != 0xFFFFFFFF) {
                paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
            canvas.drawBitmap(original, 0, 0, paint);
            
            return tinted;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getDominantColor(Bitmap bitmap) {
        if (bitmap == null) return Color.WHITE;
        
        Bitmap smallBitmap = Bitmap.createScaledBitmap(bitmap, 8, 8, false);
        int width = smallBitmap.getWidth();
        int height = smallBitmap.getHeight();
        
        long r = 0, g = 0, b = 0;
        int count = 0;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = smallBitmap.getPixel(x, y);
                if (Color.alpha(pixel) > 128) {
                    r += Color.red(pixel);
                    g += Color.green(pixel);
                    b += Color.blue(pixel);
                    count++;
                }
            }
        }
        
        if (count == 0) return Color.WHITE;
        return Color.rgb((int)(r / count), (int)(g / count), (int)(b / count));
    }
}
