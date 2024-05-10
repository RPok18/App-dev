package com.example.myapplication;


import android.graphics.Bitmap;
import android.graphics.Color;

public class Filter {

    public static Bitmap applyBlackWhiteFilter(Bitmap originalBitmap) {
        Bitmap blackWhiteBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(blackWhiteBitmap);
        android.graphics.Paint paint = new android.graphics.Paint();
        android.graphics.ColorMatrix colorMatrix = new android.graphics.ColorMatrix();
        colorMatrix.setSaturation(0);
        android.graphics.ColorMatrixColorFilter colorFilter = new android.graphics.ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorFilter);
        canvas.drawBitmap(originalBitmap, 0, 0, paint);
        return blackWhiteBitmap;
    }

    public static Bitmap applyRedFilter(Bitmap originalBitmap) {
        Bitmap redBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        for (int x = 0; x < redBitmap.getWidth(); x++) {
            for (int y = 0; y < redBitmap.getHeight(); y++) {
                int pixel = redBitmap.getPixel(x, y);
                int alpha = android.graphics.Color.alpha(pixel);
                int red = android.graphics.Color.red(pixel);
                redBitmap.setPixel(x, y, android.graphics.Color.argb(alpha, red, 0, 0)); // 红色滤镜，绿色和蓝色分量设置为0
            }
        }
        return redBitmap;
    }

    public static Bitmap applyGreenFilter(Bitmap originalBitmap) {
        Bitmap greenBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        for (int x = 0; x < greenBitmap.getWidth(); x++) {
            for (int y = 0; y < greenBitmap.getHeight(); y++) {
                int pixel = greenBitmap.getPixel(x, y);
                int alpha = android.graphics.Color.alpha(pixel);
                int green = android.graphics.Color.green(pixel);
                greenBitmap.setPixel(x, y, android.graphics.Color.argb(alpha, 0, green, 0)); // 绿色滤镜，红色和蓝色分量设置为0
            }
        }
        return greenBitmap;
    }

    public static Bitmap applyBlueFilter(Bitmap originalBitmap) {
        Bitmap blueBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        for (int x = 0; x < blueBitmap.getWidth(); x++) {
            for (int y = 0; y < blueBitmap.getHeight(); y++) {
                int pixel = blueBitmap.getPixel(x, y);
                int alpha = android.graphics.Color.alpha(pixel);
                int blue = android.graphics.Color.blue(pixel);
                blueBitmap.setPixel(x, y, android.graphics.Color.argb(alpha, 0, 0, blue)); // 蓝色滤镜，红色和绿色分量设置为0
            }
        }
        return blueBitmap;
    }
}
