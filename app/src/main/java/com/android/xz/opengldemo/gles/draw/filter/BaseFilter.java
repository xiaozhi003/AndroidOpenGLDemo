package com.android.xz.opengldemo.gles.draw.filter;

import android.opengl.GLES20;

import com.android.xz.opengldemo.gles.GLESUtils;

public abstract class BaseFilter implements AFilter {

    //FBO id
    protected int[] mFrameBuffers;
    //fbo 纹理id
    protected int[] mFrameBufferTextures;
    // 是否使用离屏渲染
    protected boolean bindFBO;
    private int mWidth;
    private int mHeight;

    @Override
    public void bindFBO(boolean bindFBO) {
        this.bindFBO = bindFBO;
    }

    /**
     * 创建帧缓冲区（FBO）
     *
     * @param width
     * @param height
     */
    public void createFrameBuffers(int width, int height) {
        if (mFrameBuffers != null) {
            destroyFrameBuffers();
        }
        //fbo的创建 (缓存)
        //1、创建fbo （离屏屏幕）
        mFrameBuffers = new int[1];
        // 1、创建几个fbo 2、保存fbo id的数据 3、从这个数组的第几个开始保存
        GLES20.glGenFramebuffers(mFrameBuffers.length, mFrameBuffers, 0);

        //2、创建属于fbo的纹理
        mFrameBufferTextures = new int[1]; //用来记录纹理id
        //创建纹理
        int textureId = GLESUtils.create2DTexture();
        mFrameBufferTextures[0] = textureId;

        //让fbo与 纹理发生关系
        //创建一个 2d的图像
        // 目标 2d纹理+等级 + 格式 +宽、高+ 格式 + 数据类型(byte) + 像素数据
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height,
                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        // 设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // 让fbo与纹理绑定起来 ， 后续的操作就是在操作fbo与这个纹理上了
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0], 0);
        //解绑
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    /**
     * 销毁帧缓冲区（FBO）
     */
    public void destroyFrameBuffers() {
        //删除fbo的纹理
        if (mFrameBufferTextures != null) {
            GLES20.glDeleteTextures(1, mFrameBufferTextures, 0);
            mFrameBufferTextures = null;
        }
        //删除fbo
        if (mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(1, mFrameBuffers, 0);
            mFrameBuffers = null;
        }
    }

    @Override
    public void surfaceChanged(int width, int height) {
        mWidth = width;
        mHeight = height;
        if (bindFBO) {
            createFrameBuffers(width, height);
        }
    }

    public int draw(int textureId, float[] matrix) {
        GLES20.glViewport(0, 0, mWidth, mHeight);
        if (bindFBO) {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        }

        onDraw(textureId, matrix);

        if (bindFBO) {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            //返回fbo的纹理id
            return mFrameBufferTextures[0];
        } else {
            return textureId;
        }
    }

    @Override
    public void release() {
        destroyFrameBuffers();
    }
}
