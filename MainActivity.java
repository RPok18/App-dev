package com.example.myapplication;

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
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;

    private ImageView imageView;
    private Button angleButton;
    private Button filterButton;
    private Button openGalleryButton;
    private LinearLayout filterMenu;
    private Button blackWhiteFilterButton;
    private Button redFilterButton;
    private Button greenFilterButton;

    private Bitmap originalBitmap;

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

        angleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAngleInputDialog();
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFilterMenu();
            }
        });

        openGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        blackWhiteFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyBlackWhiteFilter();
            }
        });

        redFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyRedFilter();
            }
        });

        greenFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyGreenFilter();
            }
        });
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
                    rotateImage(angle);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }

   // 旋转图片的方法 How to rotate pictures
    private void rotateImage(int angle) {
        android.graphics.Matrix matrix = new android.graphics.Matrix();
        matrix.setRotate(angle, originalBitmap.getWidth() / 2f, originalBitmap.getHeight() / 2f);
        Bitmap rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
        imageView.setImageBitmap(rotatedBitmap);
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
}
