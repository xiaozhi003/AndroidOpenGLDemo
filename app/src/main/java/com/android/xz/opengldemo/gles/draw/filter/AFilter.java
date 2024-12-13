package com.android.xz.opengldemo.gles.draw.filter;

public interface AFilter {

    public void setTextureSize(int width, int height);

    void bindFBO(boolean bindFBO);

    void surfaceCreated();

    void surfaceChanged(int width, int height);

    int draw(int textureId, float[] matrix);

    void onDraw(int textureId, float[] matrix);

    void release();
}
