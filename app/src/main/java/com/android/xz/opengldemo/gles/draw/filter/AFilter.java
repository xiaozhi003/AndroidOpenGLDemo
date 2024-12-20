package com.android.xz.opengldemo.gles.draw.filter;

public interface AFilter {

    void setTextureSize(int width, int height);

    void setFrameBuffer(int frameBuffer);

    int getOffscreenTexture();

    void setBindFBO(boolean bindFBO);

    void bindFBO();

    void unBindFBO();

    void surfaceCreated();

    void surfaceChanged(int width, int height);

    int draw(int textureId, float[] matrix);

    void onDraw(int textureId, float[] matrix);

    void release();
}
