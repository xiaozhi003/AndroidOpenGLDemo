package com.android.xz.opengldemo.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.android.xz.opengldemo.gles.draw.Square;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SquareGLSurfaceView extends GLSurfaceView {

    private Context mContext;
    private MyRenderer mMyRenderer;

    public SquareGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public SquareGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mMyRenderer = new MyRenderer();
        setEGLContextClientVersion(2);
        setRenderer(mMyRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    static class MyRenderer implements Renderer {

        Square mSquare;

        public MyRenderer() {
            mSquare = new Square();
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            mSquare.surfaceCreated();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            mSquare.surfaceChanged(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            mSquare.draw();
        }
    }
}
