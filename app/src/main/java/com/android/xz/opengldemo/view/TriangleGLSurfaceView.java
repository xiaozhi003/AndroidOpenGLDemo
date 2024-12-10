package com.android.xz.opengldemo.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.android.xz.opengldemo.gles.draw.Triangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 绘制三角形
 */
public class TriangleGLSurfaceView extends GLSurfaceView {

    private Context mContext;
    private MyRenderer mMyRenderer;

    public TriangleGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public TriangleGLSurfaceView(Context context, AttributeSet attrs) {
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

        Triangle mTriangle;

        public MyRenderer() {
            mTriangle = new Triangle();
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            mTriangle.surfaceCreated();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            mTriangle.surfaceChanged(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            mTriangle.draw();
        }
    }
}
