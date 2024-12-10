package com.android.xz.opengldemo.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import com.android.xz.opengldemo.camera.CameraManager;
import com.android.xz.opengldemo.camera.callback.CameraCallback;
import com.android.xz.opengldemo.gles.draw.CameraFilter;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 摄像头预览GLSurfaceView
 */
public class CameraGLSurfaceView extends GLSurfaceView implements SurfaceTexture.OnFrameAvailableListener {

    private static final String TAG = CameraGLSurfaceView.class.getSimpleName();

    private Context mContext;
    private SurfaceTexture mSurfaceTexture;
    private Handler mMainHandler;
    private CameraManager mCameraManager;
    private boolean hasSurface; // 是否存在摄像头显示层

    public CameraGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mMainHandler = new Handler(mContext.getMainLooper());
        setEGLContextClientVersion(2);
        setRenderer(new MyRenderer(this));
        mCameraManager = new CameraManager(mContext);
        mCameraManager.setCameraCallback(mCameraCallback);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasSurface) {
            openCamera();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        closeCamera();
    }

    private void surfaceTextureCreated(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
        hasSurface = true;
        mSurfaceTexture.setOnFrameAvailableListener(this);
        openCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        closeCamera();
        hasSurface = false;
    }

    /**
     * 打开摄像头
     */
    private void openCamera() {
        if (mSurfaceTexture == null) {
            Log.e(TAG, "mSurfaceTexture is null.");
            return;
        }
        if (mCameraManager.isOpen()) {
            Log.w(TAG, "Camera is opened！");
            return;
        }
        mCameraManager.openCamera();
    }

    /**
     * 关闭摄像头
     */
    private void closeCamera() {
        mCameraManager.releaseCamera();
        mSurfaceTexture = null;
    }

    CameraCallback mCameraCallback = new CameraCallback() {
        @Override
        public void onOpen() {
            mCameraManager.startPreview(mSurfaceTexture);
        }

        @Override
        public void onOpenError(int error, String msg) {

        }

        @Override
        public void onPreview(int previewWidth, int previewHeight) {

        }

        @Override
        public void onPreviewError(int error, String msg) {

        }

        @Override
        public void onClose() {

        }
    };

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender();
    }

    static class MyRenderer implements Renderer {

        private CameraFilter mCameraFilter;
        private int mTextureId;
        private SurfaceTexture mSurfaceTexture;
        private CameraGLSurfaceView mView;
        private final float[] mDisplayProjectionMatrix = new float[16];

        public MyRenderer(CameraGLSurfaceView glSurfaceView) {
            mView = glSurfaceView;
            mCameraFilter = new CameraFilter();
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            mCameraFilter.surfaceCreated();
            mTextureId = mCameraFilter.getTextureId();
            mSurfaceTexture = new SurfaceTexture(mTextureId);
            mView.mMainHandler.post(() -> mView.surfaceTextureCreated(mSurfaceTexture));
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            mCameraFilter.surfaceChanged(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            // 更新最新纹理
            mSurfaceTexture.updateTexImage();
            // 获取SurfaceTexture变换矩阵
            mSurfaceTexture.getTransformMatrix(mDisplayProjectionMatrix);
            // 将SurfaceTexture绘制到GLSurfaceView上
            mCameraFilter.draw(mDisplayProjectionMatrix);
        }
    }
}
