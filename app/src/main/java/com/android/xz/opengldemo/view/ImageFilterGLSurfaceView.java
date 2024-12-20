package com.android.xz.opengldemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import com.android.xz.opengldemo.R;
import com.android.xz.opengldemo.gles.GLESUtils;
import com.android.xz.opengldemo.gles.draw.filter.AFilter;
import com.android.xz.opengldemo.gles.draw.filter.BlendBlurFilter;
import com.android.xz.opengldemo.gles.draw.filter.OriginFilter;
import com.android.xz.opengldemo.gles.draw.filter.OverlayFilter;
import com.android.xz.opengldemo.util.MatrixUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ImageFilterGLSurfaceView extends GLSurfaceView {

    private static final String TAG = ImageFilterGLSurfaceView.class.getSimpleName();
    private Context mContext;
    private MyRenderer mRenderer;

    public ImageFilterGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public ImageFilterGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setImageFilter(AFilter filter) {
        queueEvent(() -> mRenderer.setImageFilter(filter));
        requestRender();
    }

    private void init(Context context) {
        mContext = context;
        // 创建OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        mRenderer = new MyRenderer(mContext);
        setEGLContextClientVersion(2);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    static class MyRenderer implements Renderer {

        private Context mContext;
        private AFilter mImageFilter = new OriginFilter();
        private int mTextureId;
        private Bitmap mBitmap;
        private float[] mMVPMatrix = new float[16];

        private int mWidth;
        private int mHeight;

        public MyRenderer(Context context) {
            mContext = context;
            mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.girl);
        }

        public void setImageFilter(AFilter filter) {
            if (mImageFilter != null) {
                mImageFilter.release();
            }
            mImageFilter = filter;
            mImageFilter.setTextureSize(mBitmap.getWidth(), mBitmap.getHeight());
            mImageFilter.surfaceCreated();
            mImageFilter.surfaceChanged(mWidth, mHeight);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            mTextureId = GLESUtils.create2DTexture(mBitmap);
            mImageFilter.surfaceCreated();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            mWidth = width;
            mHeight = height;
            int w = mBitmap.getWidth();
            int h = mBitmap.getHeight();
            MatrixUtils.getMatrix(mMVPMatrix, MatrixUtils.TYPE_CENTERINSIDE, w, h, width, height);
            /**
             * 由于Bitmap拷贝到纹理中，数据从Bitmap左上角开始拷贝到纹理的原点(0,0)
             * 导致图像上下翻转了180度，所以绘制坐标需要上下翻转180度才行
             */
            MatrixUtils.flip(mMVPMatrix, false, true);
            mImageFilter.surfaceChanged(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            long start = System.currentTimeMillis();
            mImageFilter.draw(mTextureId, mMVPMatrix);
            Log.i(TAG, "onDrawFrame:" + (System.currentTimeMillis() - start) + "ms");
        }
    }
}
