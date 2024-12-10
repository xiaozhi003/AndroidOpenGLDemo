package com.android.xz.opengldemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import com.android.xz.opengldemo.R;
import com.android.xz.opengldemo.gles.draw.Image;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ImageGLSurfaceView extends GLSurfaceView {

    private static final String TAG = ImageGLSurfaceView.class.getSimpleName();
    private Context mContext;
    private MyRenderer mMyRenderer;

    public ImageGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public ImageGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mMyRenderer = new MyRenderer(mContext);
        setEGLContextClientVersion(2);
        setRenderer(mMyRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    static class MyRenderer implements Renderer {

        private Image mImage;

        private Context mContext;
        private Bitmap mBitmap;

        public MyRenderer(Context context) {
            mContext = context;
            mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.beauty);
            Log.i(TAG, "mBitmap [" + mBitmap.getWidth() + ", " + mBitmap.getHeight() + "]");
            mImage = new Image(mBitmap);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // Set the background frame color
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            mImage.surfaceCreated();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            mImage.surfaceChanged(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            Log.i(TAG, "onDrawFrame.");
            mImage.draw();
        }
    }
}
