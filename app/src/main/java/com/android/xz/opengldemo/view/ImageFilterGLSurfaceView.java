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
import com.android.xz.opengldemo.util.MatrixUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ImageFilterGLSurfaceView extends GLSurfaceView {

    private static final String TAG = ImageFilterGLSurfaceView.class.getSimpleName();
    private Context mContext;
    private MyRenderer mRenderer;
    private AFilter mImageFilter;

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

        mImageFilter = new OriginFilter();

        mRenderer = new MyRenderer(mContext, mImageFilter);
        setEGLContextClientVersion(2);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    static class MyRenderer implements Renderer {

        private Context mContext;
        private AFilter mImageFilter;
        private int mTextureId;
        private int mScaleTextureId;
        private Bitmap mBitmap;
        private Bitmap mScaleBitmap;
        private float[] mMVPMatrix = new float[16];

        private int mWidth;
        private int mHeight;

        public MyRenderer(Context context, AFilter filter) {
            mContext = context;
            mImageFilter = filter;
            mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.girl);
            mScaleBitmap = scaleBitmap(mBitmap, 0.1f);
        }

        public void setImageFilter(AFilter filter) {
            if (mImageFilter != null) {
                mImageFilter.release();
            }
            mImageFilter = filter;
            mImageFilter.surfaceCreated();
            if (mImageFilter instanceof BlendBlurFilter) {
                mImageFilter.setTextureSize(mScaleBitmap.getWidth(), mScaleBitmap.getHeight());
            } else {
                mImageFilter.setTextureSize(mBitmap.getWidth(), mBitmap.getHeight());
            }
            mImageFilter.surfaceChanged(mWidth, mHeight);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            mTextureId = GLESUtils.create2DTexture(mBitmap);
            mScaleTextureId = GLESUtils.create2DTexture(mScaleBitmap);
            // Set the background frame color
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
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
            MatrixUtils.rotate(mMVPMatrix, 180);
            mImageFilter.surfaceChanged(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            long start = System.currentTimeMillis();
            if (mImageFilter instanceof BlendBlurFilter) {
                mImageFilter.draw(mScaleTextureId, mMVPMatrix);
            } else {
                mImageFilter.draw(mTextureId, mMVPMatrix);
            }
            Log.i(TAG, "onDrawFrame:" + (System.currentTimeMillis() - start) + "ms");
        }
    }

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的Bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBm = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBm.equals(origin)) {
            return newBm;
        }
        return newBm;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (mImageFilter != null) {
            mImageFilter.release();
        }
    }
}
