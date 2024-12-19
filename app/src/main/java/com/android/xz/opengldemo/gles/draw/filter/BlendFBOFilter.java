package com.android.xz.opengldemo.gles.draw.filter;

/**
 * 离屏渲染多种滤镜叠加
 */
public class BlendFBOFilter extends BaseFilter {

    private int mTextureWidth;
    private int mTextureHeight;
    private GrayFilter mGrayFilter;
    private HueFilter mHueFilter;

    @Override
    public void setTextureSize(int width, int height) {
        mTextureWidth = width;
        mTextureHeight = height;
        mGrayFilter.setTextureSize(mTextureWidth, mTextureHeight);
        mHueFilter.setTextureSize(mTextureWidth, mTextureHeight);
    }

    @Override
    public void surfaceCreated() {

    }

    @Override
    public void onDraw(int textureId, float[] matrix) {

    }
}
