import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class ImageProcessor {

    private Bitmap inputBitmap;
    private Bitmap outputBitmap;
    private Canvas canvas;
    private Paint paint;
    private float lastTouchX, lastTouchY;
    private int blurRadius = 20; // Adjust blur radius as needed

    public void applyGaussianBlur(final Bitmap inputBitmap, final View view) {
        this.inputBitmap = inputBitmap;
        outputBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(outputBitmap);
        paint = new Paint();

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastTouchX = event.getX();
                        lastTouchY = event.getY();
                        applyBlurAtPoint(lastTouchX, lastTouchY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float currentX = event.getX();
                        float currentY = event.getY();
                        applyBlurBetweenPoints(lastTouchX, lastTouchY, currentX, currentY);
                        lastTouchX = currentX;
                        lastTouchY = currentY;
                        break;
                }
                view.invalidate(); // Force a redraw of the view
                return true;
            }
        });
    }

    private void applyBlurAtPoint(float x, float y) {
        applyBlurToArea((int) x - blurRadius, (int) y - blurRadius, (int) x + blurRadius, (int) y + blurRadius);
    }

    private void applyBlurBetweenPoints(float startX, float startY, float endX, float endY) {
        // Approximate a line with multiple blur points
        float distance = (float) Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
        int steps = (int) distance / blurRadius;
        for (int i = 0; i <= steps; i++) {
            float t = (float) i / steps;
            float x = startX + t * (endX - startX);
            float y = startY + t * (endY - startY);
            applyBlurAtPoint(x, y);
        }
    }

    private void applyBlurToArea(int left, int top, int right, int bottom) {
        // Ensure the area is within the image bounds
        left = Math.max(0, left);
        top = Math.max(0, top);
        right = Math.min(inputBitmap.getWidth(), right);
        bottom = Math.min(inputBitmap.getHeight(), bottom);

        // Extract the area to be blurred
        Bitmap blurredArea = Bitmap.createBitmap(inputBitmap, left, top, right - left, bottom - top);

        // Apply Gaussian blur to the extracted area
        // (You can replace this with your own blur implementation)
        blurredArea = fastBlur(blurredArea, blurRadius);

        // Draw the blurred area back onto the output bitmap
        canvas.drawBitmap(blurredArea, left, top, null);
    }


    //  Replace with a more efficient Gaussian blur implementation
    private Bitmap fastBlur(Bitmap sentBitmap, int radius) {
        // Placeholder for a faster blur algorithm - you'll need to implement this 
        return sentBitmap.copy(sentBitmap.getConfig(), true);
    }
}
