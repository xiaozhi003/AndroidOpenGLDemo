package com.android.xz.opengldemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.android.xz.opengldemo.R;
import com.android.xz.opengldemo.gles.GLESUtils;
import com.android.xz.opengldemo.gles.draw.filter.AFilter;
import com.android.xz.opengldemo.gles.draw.filter.BlendBlurFilter;
import com.android.xz.opengldemo.gles.draw.filter.BlurFilter;
import com.android.xz.opengldemo.util.MatrixUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ImageBlurGLSurfaceView extends GLSurfaceView {

    private static final String TAG = ImageBlurGLSurfaceView.class.getSimpleName();

    private Context mContext;
    private MyRenderer mRenderer;

    public ImageBlurGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public ImageBlurGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
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

    public void setImageFilter(AFilter filter) {
        queueEvent(() -> mRenderer.setImageFilter(filter));
        requestRender();
    }

    public void setBlurRadius(int radius) {
        queueEvent(() -> mRenderer.setBlurRadius(radius));
        requestRender();
    }

    static class MyRenderer implements Renderer {

        private Context mContext;
        private AFilter mBlurFilter;
        private int mTextureId;
        private int mScaleTextureId;
        private Bitmap mBitmap;
        private Bitmap mScaleBitmap;
        private float[] mMVPMatrix = new float[16];

        private int mWidth;
        private int mHeight;

        private DrawListener mDrawListener;

        public MyRenderer(Context context) {
            mContext = context;
            mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.girl);
            mScaleBitmap = scaleBitmap(mBitmap, 0.1f);
            mBlurFilter = new BlurFilter(BlurFilter.HORIZONTAL_BLUR_SHADER);
            mBlurFilter.setTextureSize(mBitmap.getWidth(), mBitmap.getHeight());
        }

        public void setDrawListener(DrawListener drawListener) {
            mDrawListener = drawListener;
        }

        public void setBlurRadius(int radius) {
            if (mBlurFilter instanceof BlurFilter) {
                ((BlurFilter) mBlurFilter).setBlurRadius(radius);
            } else if (mBlurFilter instanceof BlendBlurFilter) {
                ((BlendBlurFilter) mBlurFilter).setBlurRadius(radius);
            }
        }

        public void setImageFilter(AFilter filter) {
            release();
            mBlurFilter = filter;
            mBlurFilter.surfaceCreated();
            if (mBlurFilter instanceof BlendBlurFilter) {
                mBlurFilter.setTextureSize(mScaleBitmap.getWidth(), mScaleBitmap.getHeight());
            } else {
                mBlurFilter.setTextureSize(mBitmap.getWidth(), mBitmap.getHeight());
            }
            mBlurFilter.surfaceChanged(mWidth, mHeight);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            mTextureId = GLESUtils.create2DTexture(mBitmap);
            mScaleTextureId = GLESUtils.create2DTexture(mScaleBitmap);
            mBlurFilter.surfaceCreated();
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
            mBlurFilter.surfaceChanged(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            long start = System.currentTimeMillis();
            if (mBlurFilter instanceof BlendBlurFilter) {
                mBlurFilter.draw(mScaleTextureId, mMVPMatrix);
            } else {
                mBlurFilter.draw(mTextureId, mMVPMatrix);
            }
            if (mDrawListener != null) {
                mDrawListener.onDraw((System.currentTimeMillis() - start));
            }
        }

        public void release() {
            mBlurFilter.release();
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
        matrix.postScale(ratio, ratio, 0, 0);
        Bitmap newBm = Bitmap.createBitmap((int) (width * ratio), (int) (height * ratio), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(newBm);
        Paint paint = new Paint();
        canvas.drawBitmap(origin, matrix, paint);
        return newBm;
    }


    public void setDrawListener(DrawListener drawListener) {
        if (mRenderer != null) {
            mRenderer.setDrawListener(drawListener);
        }
    }

    public interface DrawListener {
        void onDraw(long millis);
    }
}
