package com.android.xz.opengldemo.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.android.xz.opengldemo.gles.draw.Circle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CircleGLSurfaceView extends GLSurfaceView {

    private Context mContext;
    private MyRenderer mMyRenderer;

    public CircleGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public CircleGLSurfaceView(Context context, AttributeSet attrs) {
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

        Circle mCircle;

        public MyRenderer() {
            mCircle = new Circle();
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            mCircle.surfaceCreated();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            mCircle.surfaceChanged(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            mCircle.draw();
        }
    }
}
