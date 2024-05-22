package com.example.myapplication;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
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
    private Button cubeButton;
    private Bitmap currentBitmap;
    private Button blurButton;
    private boolean blurMode = false;
    private Set<String> blurredRegions = new HashSet<>();
    private Button saveButton; // 新增保存按钮

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
        Button newbButton = findViewById(R.id.newb);
        cubeButton = findViewById(R.id.cubeButton);
        blurButton = findViewById(R.id.blurButton);
        saveButton = findViewById(R.id.saveButton); // 初始化保存按钮

        angleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAngleInputDialog();
            }
        });

        Button anotherPageButton = findViewById(R.id.anotherPageButton);
        anotherPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AnotherActivity.class);
                startActivity(intent);
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
                        Bitmap filteredBitmap = Filter.applyBlackWhiteFilter(originalBitmap);
                        currentBitmap = filteredBitmap;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(filteredBitmap);
                            }
                        });
                    }
                });
            }
        });

        cubeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CubeActivity.class);
                startActivity(intent);
            }
        });

        redFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilterAsync(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap filteredBitmap = Filter.applyRedFilter(originalBitmap);
                        currentBitmap = filteredBitmap;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(filteredBitmap);
                            }
                        });
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
                        Bitmap filteredBitmap = Filter.applyGreenFilter(originalBitmap);
                        currentBitmap = filteredBitmap;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(filteredBitmap);
                            }
                        });
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
                        Bitmap filteredBitmap = Filter.applyBlueFilter(originalBitmap);
                        currentBitmap = filteredBitmap;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(filteredBitmap);
                            }
                        });
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

        saveButton.setOnClickListener(new View.OnClickListener() { // 保存按钮点击事件
            @Override
            public void onClick(View v) {
                saveImageToGallery(currentBitmap);
            }
        });

        scaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScaleInputDialog();
            }
        });

        newbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUnsharpInputDialog();
            }
        });

        blurButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blurMode = !blurMode;
                if (blurMode) {
                    Toast.makeText(MainActivity.this, "Blur mode enabled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Blur mode disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (blurMode && (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN)) {
                    applyBlur(event.getX(), event.getY());
                }
                return true;
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
                currentBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                imageView.setImageBitmap(currentBitmap);
                blurredRegions.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveImageToGallery(Bitmap bitmap) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Edited_Image_" + System.currentTimeMillis() + ".jpg");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        try {
            if (uri != null) {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();
                    Toast.makeText(MainActivity.this, "图片已保存到相册", Toast.LENGTH_SHORT).show();
                } else {Toast.makeText(MainActivity.this, "无法打开输出流", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "无法插入图片", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "保存图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAngleInputDialog() {
        final EditText angleEditText = new EditText(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        angleEditText.setLayoutParams(layoutParams);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rotation angle");
        builder.setView(angleEditText);
        builder.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String angleStr = angleEditText.getText().toString();
                if (!angleStr.isEmpty()) {
                    int angle = Integer.parseInt(angleStr);
                    Bitmap rotatedBitmap = ImageProcessor.rotateImage(currentBitmap, angle);
                    currentBitmap = rotatedBitmap;
                    imageView.setImageBitmap(rotatedBitmap);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private void showScaleInputDialog() {
        final EditText scaleEditText = new EditText(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        scaleEditText.setLayoutParams(layoutParams);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scaling Factor");
        builder.setView(scaleEditText);
        builder.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String scaleStr = scaleEditText.getText().toString();
                if (!scaleStr.isEmpty()) {
                    float scaleFactor = Float.parseFloat(scaleStr);
                    Bitmap scaledBitmap = ImageProcessor.scaleImage(currentBitmap, scaleFactor);
                    currentBitmap = scaledBitmap;
                    imageView.setImageBitmap(scaledBitmap);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void toggleFilterMenu() {
        if (filterMenu.getVisibility() == View.VISIBLE) {
            filterMenu.setVisibility(View.GONE);
        } else {
            filterMenu.setVisibility(View.VISIBLE);
        }
    }

    private void cancelFilter() {
        currentBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        imageView.setImageBitmap(currentBitmap);
    }

    private void showUnsharpInputDialog() {
        final EditText unsharpEditText = new EditText(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        unsharpEditText.setLayoutParams(layoutParams);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please enter a sharpening ratio");
        builder.setView(unsharpEditText);
        builder.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String unsharpStr = unsharpEditText.getText().toString();
                if (!unsharpStr.isEmpty()) {
                    float unsharpFactor = Float.parseFloat(unsharpStr);
                    Bitmap sharpenedBitmap = UnsharpMask.applyUnsharpMask(currentBitmap, unsharpFactor);
                    currentBitmap = sharpenedBitmap;
                    imageView.setImageBitmap(sharpenedBitmap);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void applyBlur(float x, float y) {
        // Define the radius of the blur
        int blurRadius = 50;

        // Ensure we don't blur the same region repeatedly
        String regionKey = (int) x + "_" + (int) y;
        if (blurredRegions.contains(regionKey)) {
            return;
        }

        // Mark the region as blurred
        blurredRegions.add(regionKey);

        // Calculate the region to blur
        int startX = (int) Math.max(0, x - blurRadius);
        int startY = (int) Math.max(0, y - blurRadius);
        int endX = (int) Math.min(currentBitmap.getWidth(), x + blurRadius);
        int endY = (int) Math.min(currentBitmap.getHeight(), y + blurRadius);

        // Extract the region
        Bitmap regionToBlur = Bitmap.createBitmap(currentBitmap, startX, startY, endX - startX, endY - startY);

        // Apply the blur
        Bitmap blurredRegion = BlurProcessor.blur(this, regionToBlur);

        // Draw the blurred region back onto the currentBitmap
        Canvas canvas = new Canvas(currentBitmap);
        canvas.drawBitmap(blurredRegion, startX, startY, null);

        // Update the ImageView with the new blurred image
        imageView.setImageBitmap(currentBitmap);
    }
}
