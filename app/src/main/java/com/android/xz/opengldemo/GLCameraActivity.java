package com.android.xz.opengldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.xz.opengldemo.view.CameraGLSurfaceView;

public class GLCameraActivity extends AppCompatActivity {

    private CameraGLSurfaceView mCameraGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mCameraGLSurfaceView = findViewById(R.id.cameraView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraGLSurfaceView.onPause();
    }
}