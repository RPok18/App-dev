package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
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
import android.content.DialogInterface;

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
                        imageView.setImageBitmap(filteredBitmap);
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
                        Bitmap filteredBitmap = Filter.applyRedFilter(originalBitmap);
                        imageView.setImageBitmap(filteredBitmap);
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
                        imageView.setImageBitmap(filteredBitmap);
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
                        imageView.setImageBitmap(filteredBitmap);
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
                    Bitmap rotatedBitmap = ImageProcessor.rotateImage(originalBitmap, angle);
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
                    Bitmap scaledBitmap = ImageProcessor.scaleImage(originalBitmap, scaleFactor);
                    imageView.setImageBitmap(scaledBitmap);}
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
        imageView.setImageBitmap(originalBitmap);
    }

}
