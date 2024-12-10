package com.android.xz.opengldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.android.xz.opengldemo.view.CircleGLSurfaceView;
import com.android.xz.opengldemo.view.EnvGLSurfaceView;
import com.android.xz.opengldemo.view.ImageGLSurfaceView;
import com.android.xz.opengldemo.view.SquareGLSurfaceView;
import com.android.xz.opengldemo.view.TriangleGLSurfaceView;

public class GLActivity extends AppCompatActivity {

    public static final String EXTRA_GL_VIEW_TYPE = "com.android.xz.opengldemo.GL_VIEW_TYPE";

    public static final int TYPE_ENV = 0;
    public static final int TYPE_TRIANGLE = 1;
    public static final int TYPE_SQUARE = 2;
    public static final int TYPE_CIRCLE = 3;
    public static final int TYPE_IMAGE = 4;

    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gl);

        LinearLayout layout = findViewById(R.id.contentLayout);
        switch (getIntent().getIntExtra(EXTRA_GL_VIEW_TYPE, TYPE_ENV)) {
            case TYPE_ENV:
                mGLSurfaceView = new EnvGLSurfaceView(this);
                break;
            case TYPE_TRIANGLE:
                mGLSurfaceView = new TriangleGLSurfaceView(this);
                break;
            case TYPE_SQUARE:
                mGLSurfaceView = new SquareGLSurfaceView(this);
                break;
            case TYPE_CIRCLE:
                mGLSurfaceView = new CircleGLSurfaceView(this);
                break;
            case TYPE_IMAGE:
                mGLSurfaceView = new ImageGLSurfaceView(this);
                break;
        }
        layout.addView(mGLSurfaceView);
    }
}