package com.android.xz.opengldemo.gles.draw.filter;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.android.xz.opengldemo.util.MatrixUtils;

import java.text.Format;

public class BlendBlurFilter extends BaseFilter {

    private int mTextureWidth;
    private int mTextureHeight;

    private BlurFilter mHorizontalFilter;
    private BlurFilter mVerticalFilter;
    private OriginFilter mOriginFilter;

    float[] mvpMatrix = new float[16];
    private int mWidth;
    private int mHeight;

    @Override
    public void setTextureSize(int width, int height) {
        mTextureWidth = width;
        mTextureHeight = height;
        mHorizontalFilter.setTextureSize(mTextureWidth, mTextureHeight);
        mVerticalFilter.setTextureSize(mTextureWidth, mTextureHeight);
    }

    @Override
    public void bindFBO(boolean bindFBO) {
    }

    @Override
    public void surfaceCreated() {
        mHorizontalFilter = new BlurFilter();
        mHorizontalFilter.setTextureSize(mTextureWidth, mTextureHeight);
        mHorizontalFilter.bindFBO(true);
        mHorizontalFilter.setImageFilter(BlurFilter.HORIZONTAL_BLUR_SHADER);
        mHorizontalFilter.surfaceCreated();

        mVerticalFilter = new BlurFilter();
        mVerticalFilter.setTextureSize(mTextureWidth, mTextureHeight);
        mVerticalFilter.bindFBO(true);
        mVerticalFilter.setImageFilter(BlurFilter.VERTICAL_BLUR_SHADER);
        mVerticalFilter.surfaceCreated();

        mOriginFilter = new OriginFilter();
        mOriginFilter.surfaceCreated();
    }

    @Override
    public void surfaceChanged(int width, int height) {
        mWidth = width;
        mHeight = height;
        mHorizontalFilter.surfaceChanged(mTextureWidth, mTextureHeight);
        mVerticalFilter.surfaceChanged(mTextureWidth, mTextureHeight);
        mOriginFilter.surfaceChanged(width, height);
    }

    @Override
    public void onDraw(int textureId, float[] matrix) {
        int fboId;
        fboId = mHorizontalFilter.draw(textureId, MatrixUtils.getOriginalMatrix());
        fboId = mVerticalFilter.draw(fboId, MatrixUtils.getOriginalMatrix());

        // 可进行多次模糊
        for (int i = 0; i < 0; i++) {
            fboId = mHorizontalFilter.draw(fboId, MatrixUtils.getOriginalMatrix());
            fboId = mVerticalFilter.draw(fboId, MatrixUtils.getOriginalMatrix());
        }

        mOriginFilter.draw(fboId, matrix);
    }

    @Override
    public void release() {
        super.release();
        mHorizontalFilter.release();
        mVerticalFilter.release();
        mOriginFilter.release();
    }
}
