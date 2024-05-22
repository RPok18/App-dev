package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

public class BlurProcessor {

    public static Bitmap blur(Context context, Bitmap image) {
        // Get the bitmap's width and height
        int width = image.getWidth();
        int height = image.getHeight();

        // Create a new bitmap to store the blurred image
        Bitmap blurredBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // Define the radius of the blur
        int radius = 10;

        // Create arrays to store the pixel data
        int[] pixels = new int[width * height];
        int[] temp = new int[width * height];

        // Get the pixels from the bitmap
        image.getPixels(pixels, 0, width, 0, 0, width, height);

        // Apply the horizontal blur
        for (int y = 0; y < height; y++) {
            blurHorizontal(pixels, temp, width, y, radius);
        }

        // Apply the vertical blur
        for (int x = 0; x < width; x++) {
            blurVertical(temp, pixels, width, height, x, radius);
        }

        // Set the pixels of the blurred bitmap
        blurredBitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return blurredBitmap;
    }

    private static void blurHorizontal(int[] pixels, int[] temp, int width, int y, int radius) {
        int sumRed, sumGreen, sumBlue;
        int pixel, halfRadius = radius / 2;

        for (int x = 0; x < width; x++) {
            sumRed = sumGreen = sumBlue = 0;

            for (int dx = -halfRadius; dx <= halfRadius; dx++) {
                int nx = x + dx;
                if (nx >= 0 && nx < width) {
                    pixel = pixels[y * width + nx];
                    sumRed += Color.red(pixel);
                    sumGreen += Color.green(pixel);
                    sumBlue += Color.blue(pixel);
                }
            }

            temp[y * width + x] = Color.rgb(sumRed / radius, sumGreen / radius, sumBlue / radius);
        }
    }

    private static void blurVertical(int[] temp, int[] pixels, int width, int height, int x, int radius) {
        int sumRed, sumGreen, sumBlue;
        int pixel, halfRadius = radius / 2;

        for (int y = 0; y < height; y++) {
            sumRed = sumGreen = sumBlue = 0;

            for (int dy = -halfRadius; dy <= halfRadius; dy++) {
                int ny = y + dy;
                if (ny >= 0 && ny < height) {
                    pixel = temp[ny * width + x];
                    sumRed += Color.red(pixel);
                    sumGreen += Color.green(pixel);
                    sumBlue += Color.blue(pixel);
                }
            }

            pixels[y * width + x] = Color.rgb(sumRed / radius, sumGreen / radius, sumBlue / radius);
        }
    }
}
