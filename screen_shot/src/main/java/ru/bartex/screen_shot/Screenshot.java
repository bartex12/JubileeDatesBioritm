package ru.bartex.screen_shot;

import android.graphics.Bitmap;
import android.view.View;

public class Screenshot {
    public static Bitmap takeScreenshot(View v){
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static Bitmap takeScreenshotOfRootView(View v){
        return  takeScreenshot(v.getRootView());
    }
}
