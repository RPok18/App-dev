package com.example.myapplication;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class CubeActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private CubeRenderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 创建 GLSurfaceView 实例
        glSurfaceView = new GLSurfaceView(this);

        // 设置 OpenGL ES 版本 (这里设置为 2.0)
        glSurfaceView.setEGLContextClientVersion(2);

        // 创建渲染器实例
        renderer = new CubeRenderer(this);

        // 设置渲染器
        glSurfaceView.setRenderer(renderer);

        // 设置触摸事件监听
        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float normalizedX = event.getX() / (float) v.getWidth() * 2 - 1;
                float normalizedY = -(event.getY() / (float) v.getHeight() * 2 - 1);

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    renderer.handleTouchPress(normalizedX, normalizedY);
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    renderer.handleTouchDrag(normalizedX, normalizedY);
                }

                return true;
            }
        });

        // 设置 GLSurfaceView 作为活动视图
        setContentView(glSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (glSurfaceView != null) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (glSurfaceView != null) {
            glSurfaceView.onResume();
        }
    }
}
