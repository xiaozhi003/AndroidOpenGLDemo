package com.android.xz.opengldemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.util.Log;

import com.android.xz.opengldemo.R;
import com.android.xz.opengldemo.gles.GLESUtils;
import com.android.xz.opengldemo.gles.draw.filter.GrayFilter;
import com.android.xz.opengldemo.util.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FBOReadGLSurfaceView extends GLSurfaceView {

    private static final String TAG = FBOReadGLSurfaceView.class.getSimpleName();
    private Context mContext;
    private MyRenderer mRenderer;

    public FBOReadGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public FBOReadGLSurfaceView(Context context, AttributeSet attrs) {
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

    static class MyRenderer implements Renderer {

        private Context mContext;
        private GrayFilter mImageFilter = new GrayFilter();
        private int mTextureId;
        private Bitmap mBitmap;

        private float[] mMVPMatrix = new float[16];

        public MyRenderer(Context context) {
            mContext = context;
            mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.girl);
            mImageFilter.bindFBO(true);
            Matrix.setIdentityM(mMVPMatrix, 0);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Log.i(TAG, "onSurfaceCreated...");
            mTextureId = GLESUtils.create2DTexture(mBitmap);
            mImageFilter.surfaceCreated();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Log.i(TAG, "onSurfaceChanged...");
            mImageFilter.surfaceChanged(mBitmap.getWidth(), mBitmap.getHeight());
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            long start = System.currentTimeMillis();
            mImageFilter.draw(mTextureId, mMVPMatrix);
            Log.i(TAG, "onDrawFrame:" + (System.currentTimeMillis() - start) + "ms");

            mImageFilter.bindFBO();
            Bitmap resultBitmap = readBufferPixelToBitmap(mBitmap.getWidth(), mBitmap.getHeight());
            mImageFilter.unBindFBO();
            Log.i(TAG, "readBufferPixelToBitmap:" + (System.currentTimeMillis() - start) + "ms");

            byte[] jpgData = Bitmap2Bytes(resultBitmap, 100);
            FileUtils.writeFile(mContext.getExternalFilesDir("gles").getAbsolutePath() + File.separator + "gray.jpg", jpgData);
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            if (mImageFilter != null) {
                mImageFilter.release();
            }
        }

        public static byte[] Bitmap2Bytes(Bitmap bm, int compress) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, compress, baos);
            return baos.toByteArray();
        }

        private Bitmap readBufferPixelToBitmap(int width, int height) {
            ByteBuffer buf = ByteBuffer.allocateDirect(width * height * 4);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buf);
            buf.rewind();

            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bmp.copyPixelsFromBuffer(buf);
            return bmp;
        }
    }
}
