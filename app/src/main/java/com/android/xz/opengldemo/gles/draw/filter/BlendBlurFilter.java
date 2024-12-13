package com.android.xz.opengldemo.gles.draw.filter;

import com.android.xz.opengldemo.util.MatrixUtils;

public class BlendBlurFilter extends BaseFilter {
    private int mTextureWidth;
    private int mTextureHeight;

    private BlurFilter mHorizontalFilter;
    private BlurFilter mVerticalFilter;
    private OriginFilter mOriginFilter;

    @Override
    public void setTextureSize(int width, int height) {
        mTextureWidth = width;
        mTextureHeight = height;
        mHorizontalFilter.setTextureSize(mTextureWidth, mTextureHeight);
        mVerticalFilter.setTextureSize(mTextureWidth, mTextureHeight);
    }

    public void setBlurRadius(int radius) {
        mHorizontalFilter.setBlurRadius(radius);
        mVerticalFilter.setBlurRadius(radius);
    }

    @Override
    public void bindFBO(boolean bindFBO) {
    }

    @Override
    public void surfaceCreated() {
        mHorizontalFilter = new BlurFilter(BlurFilter.HORIZONTAL_BLUR_SHADER);
        mHorizontalFilter.setTextureSize(mTextureWidth, mTextureHeight);
        mHorizontalFilter.bindFBO(true);
        mHorizontalFilter.surfaceCreated();

        mVerticalFilter = new BlurFilter(BlurFilter.VERTICAL_BLUR_SHADER);
        mVerticalFilter.setTextureSize(mTextureWidth, mTextureHeight);
        mVerticalFilter.bindFBO(true);
        mVerticalFilter.surfaceCreated();

        mOriginFilter = new OriginFilter();
        mOriginFilter.surfaceCreated();
    }

    @Override
    public void surfaceChanged(int width, int height) {
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
