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

    public static Bitmap loadBitmap(Context context, String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }

        try {
            // 1. Nếu là ảnh trong thư mục assets
            if (filePath.startsWith("file:///android_asset/")) {
                String path = filePath.replace("file:///android_asset/", "");
                InputStream is = context.getAssets().open(path);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                is.close();
                return bitmap;
            }
            // 2. Nếu là ảnh từ bộ nhớ máy (Internal/External Storage)
            else {
                String path = filePath.replace("file://", "");
                return BitmapFactory.decodeFile(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
