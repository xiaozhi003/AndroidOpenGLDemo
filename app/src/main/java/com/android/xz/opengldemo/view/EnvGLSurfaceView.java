package com.android.xz.opengldemo.view;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class EnvGLSurfaceView extends GLSurfaceView {

    private Context mContext;
    private MyGLRenderer mRenderer;

    public EnvGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public EnvGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        // 创建OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        mRenderer = new MyGLRenderer();

        // 设置渲染器
        setRenderer(mRenderer);
        // 设置渲染模式：仅当图形数据发生更改时渲染视图
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    static class MyGLRenderer implements GLSurfaceView.Renderer {

        public void onSurfaceCreated(GL10 unused, EGLConfig config) {
            // 设置背景颜色为红色
            GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        }

        public void onDrawFrame(GL10 unused) {
            // 设置红色清屏
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        }

        public void onSurfaceChanged(GL10 unused, int width, int height) {
            // 修改OpenGL ES窗口大小
            GLES20.glViewport(0, 0, width, height);
        }
    }
}
