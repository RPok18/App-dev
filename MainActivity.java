package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class MainActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;

    private ImageView imageView;
    private Button angleButton;
    private Button filterButton;
    private Button openGalleryButton;
    private HorizontalScrollView filterMenu;
    private Button blackWhiteFilterButton;
    private Button redFilterButton;
    private Button greenFilterButton;
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    private Bitmap originalBitmap;
    private Button blueFilterButton;
    private Button primaryFilterButton;
    private Button scaleButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        angleButton = findViewById(R.id.angleButton);
        filterButton = findViewById(R.id.filterButton);
        openGalleryButton = findViewById(R.id.openGalleryButton);
        filterMenu = findViewById(R.id.filterMenu);
        blackWhiteFilterButton = findViewById(R.id.blackWhiteFilterButton);
        redFilterButton = findViewById(R.id.redFilterButton);
        greenFilterButton = findViewById(R.id.greenFilterButton);
        scaleButton = findViewById(R.id.scaleButton);

        angleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAngleInputDialog();
            }
        });
        openGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFilterMenu();
            }
        });

        blackWhiteFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilterAsync(new Runnable() {
                    @Override
                    public void run() {
                        applyBlackWhiteFilter();
                    }
                });
            }
        });

        redFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilterAsync(new Runnable() {
                    @Override
                    public void run() {
                        applyRedFilter();
                    }
                });
            }
        });

        greenFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilterAsync(new Runnable() {
                    @Override
                    public void run() {
                        applyGreenFilter();
                    }
                });
            }
        });
        blueFilterButton = findViewById(R.id.blueFilterButton);
        primaryFilterButton = findViewById(R.id.primaryFilterButton);

        blueFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilterAsync(new Runnable() {
                    @Override
                    public void run() {
                        applyBlueFilter();
                    }
                });
            }
        });

        primaryFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilterAsync(new Runnable() {
                    @Override
                    public void run() {
                        cancelFilter();
                    }
                });
            }
        });
        scaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScaleInputDialog();
            }
        });


    }

    private void applyFilterAsync(Runnable filterOperation) {
        executor.execute(filterOperation);
    }


    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            try {
                originalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                imageView.setImageBitmap(originalBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 显示输入角度对话框 Display the input angle dialog box

    private void showAngleInputDialog() {
        final EditText angleEditText = new EditText(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        angleEditText.setLayoutParams(layoutParams);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Rotation angle");
        builder.setView(angleEditText);
        builder.setPositiveButton("Sure", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                String angleStr = angleEditText.getText().toString();
                if (!angleStr.isEmpty()) {
                    int angle = Integer.parseInt(angleStr);
                    // 调用旋转图片的方法，传入原始位图和旋转角度
                    Bitmap rotatedBitmap = rotateImage(originalBitmap, angle);
                    imageView.setImageBitmap(rotatedBitmap);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }
    // 显示输入缩放倍数的对话框 Displays a dialog box for entering the zoom factor
    private void showScaleInputDialog() {
        final EditText scaleEditText = new EditText(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        scaleEditText.setLayoutParams(layoutParams);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Scale Factor");
        builder.setView(scaleEditText);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String scaleStr = scaleEditText.getText().toString();
                if (!scaleStr.isEmpty()) {
                    float scaleFactor = Float.parseFloat(scaleStr);
                    // 缩放图片
                    Bitmap scaledBitmap = scaleImage(originalBitmap, scaleFactor);
                    imageView.setImageBitmap(scaledBitmap);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }
    // 缩放图片的方法 How to zoom pictures
    private Bitmap scaleImage(Bitmap originalBitmap, float scaleFactor) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        // 计算缩放后的图像尺寸 Calculate the scaled image size
        int newWidth = (int) (width * scaleFactor);
        int newHeight = (int) (height * scaleFactor);

        // 创建缩放后的位图 Create a scaled bitmap
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);

        return scaledBitmap;
    }
    // 旋转图片的方法 How to rotate pictures
    private Bitmap rotateImage(Bitmap originalBitmap, int angle) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        // 计算旋转后的图像尺寸 Calculate image size after rotation
        double radians = Math.toRadians(angle);
        int newWidth = (int) (Math.abs(width * Math.cos(radians)) + Math.abs(height * Math.sin(radians)));
        int newHeight = (int) (Math.abs(height * Math.cos(radians)) + Math.abs(width * Math.sin(radians)));

        // 创建一个新的位图，将图像扩大以容纳旋转后的图像 Create a new bitmap that expands the image to accommodate the rotated image
        Bitmap rotatedBitmap = Bitmap.createBitmap(newWidth, newHeight, originalBitmap.getConfig());

        // 创建画布，并将原点移动到新位图的中心 Create the canvas and move the origin to the center of the new bitmap
        Canvas canvas = new Canvas(rotatedBitmap);
        canvas.translate(newWidth / 2, newHeight / 2);
        canvas.rotate(angle);

        // 绘制旋转后的图像 Draw rotated image
        Paint paint = new Paint();
        canvas.drawBitmap(originalBitmap, -width / 2f, -height / 2f, paint);

        return rotatedBitmap;
    }



    // 切换滤镜菜单的可见性 Toggle the visibility of the filter menu
    private void toggleFilterMenu() {
        if (filterMenu.getVisibility() == View.VISIBLE) {
            filterMenu.setVisibility(View.GONE);
        } else {
            filterMenu.setVisibility(View.VISIBLE);
        }
    }

    // 应用黑白滤镜 Apply black and white filter
    private void applyBlackWhiteFilter() {
        Bitmap blackWhiteBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(blackWhiteBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorFilter);
        canvas.drawBitmap(originalBitmap, 0, 0, paint);
        imageView.setImageBitmap(blackWhiteBitmap);
    }

    // 应用红色滤镜 Apply red filter
    private void applyRedFilter() {
        Bitmap redBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        for (int x = 0; x < redBitmap.getWidth(); x++) {
            for (int y = 0; y < redBitmap.getHeight(); y++) {
                int pixel = redBitmap.getPixel(x, y);
                int alpha = Color.alpha(pixel);
                int red = Color.red(pixel);
                redBitmap.setPixel(x, y, Color.argb(alpha, red, 0, 0)); // 红色滤镜，绿色和蓝色分量设置为0
            }
        }
        imageView.setImageBitmap(redBitmap);
    }

    // 应用绿色滤镜 Apply green filter
    private void applyGreenFilter() {
        Bitmap greenBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        for (int x = 0; x < greenBitmap.getWidth(); x++) {
            for (int y = 0; y < greenBitmap.getHeight(); y++) {
                int pixel = greenBitmap.getPixel(x, y);
                int alpha = Color.alpha(pixel);
                int green = Color.green(pixel);
                greenBitmap.setPixel(x, y, Color.argb(alpha, 0, green, 0)); // 绿色滤镜，红色和蓝色分量设置为0
            }
        }
        imageView.setImageBitmap(greenBitmap);
    }


    // 应用蓝色滤镜 Apply blue filter
    private void applyBlueFilter() {
        Bitmap blueBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        for (int x = 0; x < blueBitmap.getWidth(); x++) {
            for (int y = 0; y < blueBitmap.getHeight(); y++) {
                int pixel = blueBitmap.getPixel(x, y);
                int alpha = Color.alpha(pixel);
                int blue = Color.blue(pixel);
                blueBitmap.setPixel(x, y, Color.argb(alpha, 0, 0, blue)); // 蓝色滤镜，红色和绿色分量设置为0
            }
        }
        imageView.setImageBitmap(blueBitmap);
    }

    // 取消滤镜 Cancel filter
    private void cancelFilter() {
        imageView.setImageBitmap(originalBitmap);
    }
}
