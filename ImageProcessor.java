package com.example.myapplication;

import android.graphics.Bitmap;

public class ImageProcessor {

    public static Bitmap rotateImage(Bitmap originalBitmap, int angle) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        // 计算旋转后的图像尺寸 Calculate image size after rotation
        double radians = Math.toRadians(angle);
        int newWidth = (int) (Math.abs(width * Math.cos(radians)) + Math.abs(height * Math.sin(radians)));
        int newHeight = (int) (Math.abs(height * Math.cos(radians)) + Math.abs(width * Math.sin(radians)));

        // 创建一个新的位图，将图像扩大以容纳旋转后的图像 Create a new bitmap that expands the image to accommodate the rotated image
        Bitmap rotatedBitmap = Bitmap.createBitmap(newWidth, newHeight, originalBitmap.getConfig());

        // 创建画布，并将原点移动到新位图的中心 Create the canvas and move the origin to the center of the new bitmap
        android.graphics.Canvas canvas = new android.graphics.Canvas(rotatedBitmap);
        canvas.translate(newWidth / 2, newHeight / 2);
        canvas.rotate(angle);

        // 绘制旋转后的图像 Draw rotated image
        android.graphics.Paint paint = new android.graphics.Paint();
        canvas.drawBitmap(originalBitmap, -width / 2f, -height / 2f, paint);

        return rotatedBitmap;
    }

    public static Bitmap scaleImage(Bitmap originalBitmap, float scaleFactor) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        // 计算缩放后的图像尺寸
        int newWidth = (int) (width * scaleFactor);
        int newHeight = (int) (height * scaleFactor);

        // 创建缩放后的位图
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);

        return scaledBitmap;
    }
}
