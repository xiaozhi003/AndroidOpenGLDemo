package com.android.xz.opengldemo.gles.draw.filter;

import android.opengl.GLES20;

import com.android.xz.opengldemo.gles.GLESUtils;

/**
 * 滤镜效果抽象类
 */
public abstract class BaseFilter implements AFilter {

    /**
     * 离屏渲染纹理id
     */
    private int mOffscreenTexture = -1;
    /**
     * 帧缓冲区
     */
    private int mFrameBuffer = -1;
    /**
     * 深度缓冲区
     */
    private int mDepthBuffer = -1;
    /**
     * 是否使用离屏渲染
     */
    protected boolean bindFBO;
    private int mWidth;
    private int mHeight;

    @Override
    public void setBindFBO(boolean bindFBO) {
        this.bindFBO = bindFBO;
    }

    @Override
    public void setFrameBuffer(int frameBuffer) {
        mFrameBuffer = frameBuffer;
    }

    @Override
    public int getOffscreenTexture() {
        return mOffscreenTexture;
    }

    @Override
    public void bindFBO() {
        if (mFrameBuffer > 0) {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer);
        }
    }

    @Override
    public void unBindFBO() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    /**
     * 创建帧缓冲区（FBO）
     *
     * @param width
     * @param height
     */
    public void createFrameBuffers(int width, int height) {
        if (mFrameBuffer > 0) {
            destroyFrameBuffers();
        }
        // 1.创建一个纹理对象并绑定它，这将是颜色缓冲区。
        int[] values = new int[1];
        GLES20.glGenTextures(1, values, 0);
        GLESUtils.checkGlError("glGenTextures");
        mOffscreenTexture = values[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mOffscreenTexture);
        GLESUtils.checkGlError("glBindTexture " + mOffscreenTexture);

        // 2.创建纹理存储对象
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height,
                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        // 3.设置参数。我们可能正在使用二维的非幂函数，所以某些值可能无法使用。
        // 设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLESUtils.checkGlError("glTexParameter");

        // 4.创建帧缓冲区对象并将其绑定
        GLES20.glGenFramebuffers(1, values, 0);
        GLESUtils.checkGlError("glGenFramebuffers");
        mFrameBuffer = values[0];    // expected > 0
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer);
        GLESUtils.checkGlError("glBindFramebuffer " + mFrameBuffer);

        // 5.创建深度缓冲区并绑定它
        GLES20.glGenRenderbuffers(1, values, 0);
        GLESUtils.checkGlError("glGenRenderbuffers");
        mDepthBuffer = values[0];    // expected > 0
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mDepthBuffer);
        GLESUtils.checkGlError("glBindRenderbuffer " + mDepthBuffer);

        // 为深度缓冲区分配存储空间。
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);
        GLESUtils.checkGlError("glRenderbufferStorage");

        // 6.将深度缓冲区和纹理（颜色缓冲区）附着到帧缓冲区对象
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, mDepthBuffer);
        GLESUtils.checkGlError("glFramebufferRenderbuffer");
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mOffscreenTexture, 0);
        GLESUtils.checkGlError("glFramebufferTexture2D");

        // 检查是否一切正常
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer not complete, status=" + status);
        }

        // 解绑纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        // 解绑Frame Buffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        // 解绑Render Buffer
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);

        GLESUtils.checkGlError("prepareFramebuffer done");
    }

    /**
     * 销毁帧缓冲区（FBO）
     */
    public void destroyFrameBuffers() {
        // 删除fbo的纹理
        if (mOffscreenTexture > 0) {
            GLES20.glDeleteTextures(1, new int[]{mOffscreenTexture}, 0);
            mOffscreenTexture = -1;
        }
        if (mFrameBuffer > 0) {
            GLES20.glDeleteFramebuffers(1, new int[]{mFrameBuffer}, 0);
            mFrameBuffer = -1;
        }
        if (mDepthBuffer > 0) {
            GLES20.glDeleteRenderbuffers(1, new int[]{mDepthBuffer}, 0);
            mDepthBuffer = -1;
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
            // 绑定FBO
            bindFBO();
        }

        onDraw(textureId, matrix);

        if (bindFBO) {
            // 解绑FBO
            unBindFBO();
            //返回fbo的纹理id
            return mOffscreenTexture;
        } else {
            return textureId;
        }
    }

    @Override
    public void release() {
        destroyFrameBuffers();
    }
}
