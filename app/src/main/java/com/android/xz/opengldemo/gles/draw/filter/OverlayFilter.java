package com.android.xz.opengldemo.gles.draw.filter;

import com.android.xz.opengldemo.util.MatrixUtils;

/**
 * 离屏渲染多种滤镜叠加
 */
public class OverlayFilter extends BaseFilter {

    private int mTextureWidth;
    private int mTextureHeight;
    private GrayFilter mGrayFilter;
    private HueFilter mHueFilter;
    private OriginFilter mOriginFilter;

    public OverlayFilter() {
        mGrayFilter = new GrayFilter();
        mGrayFilter.setBindFBO(true);

        mHueFilter = new HueFilter(new float[]{-0.2f, -0.2f, -0.2f});
        mHueFilter.setBindFBO(true);

        mOriginFilter = new OriginFilter();
    }

    @Override
    public void setTextureSize(int width, int height) {
        mTextureWidth = width;
        mTextureHeight = height;
        mGrayFilter.setTextureSize(mTextureWidth, mTextureHeight);
        mHueFilter.setTextureSize(mTextureWidth, mTextureHeight);
    }

    @Override
    public void surfaceCreated() {
        mGrayFilter.surfaceCreated();
        mHueFilter.surfaceCreated();
        mOriginFilter.surfaceCreated();
    }

    @Override
    public void surfaceChanged(int width, int height) {
        super.surfaceChanged(width, height);
        mGrayFilter.surfaceChanged(mTextureWidth, mTextureHeight);
        mHueFilter.surfaceChanged(mTextureWidth, mTextureHeight);
        mOriginFilter.surfaceChanged(width, height);
    }

    @Override
    public void onDraw(int textureId, float[] matrix) {
        int fboId = mGrayFilter.draw(textureId, MatrixUtils.getOriginalMatrix());
        fboId = mHueFilter.draw(fboId, MatrixUtils.getOriginalMatrix());
        mOriginFilter.draw(fboId, matrix);
    }

    @Override
    public void release() {
        super.release();
        mGrayFilter.release();
        mHueFilter.release();
        mOriginFilter.release();
    }
}
