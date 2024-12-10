package com.android.xz.opengldemo.gles.draw;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.android.xz.opengldemo.gles.GLESUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * 定义正方形
 */
public class Square {
    /**
     * 绘制的流程
     * 1.顶点着色程序 - 用于渲染形状的顶点的 OpenGL ES 图形代码
     * 2.片段着色器 - 用于渲染具有特定颜色或形状的形状的 OpenGL ES 代码纹理。
     * 3.程序 - 包含您想要用于绘制的着色器的 OpenGL ES 对象 一个或多个形状
     * <p>
     * 您至少需要一个顶点着色器来绘制形状，以及一个 fragment 着色器来为该形状着色。
     * 这些着色器必须经过编译，然后添加到 OpenGL ES 程序中，该程序随后用于绘制形状。
     */

    // 顶点着色器代码
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;\n" +
                    "attribute vec4 vPosition;\n" +
                    "void main() {\n" +
                    "  gl_Position = uMVPMatrix * vPosition;\n" +
                    "}\n";

    // 片段着色器代码
    private final String fragmentShaderCode =
            "precision mediump float;\n" +
                    "uniform vec4 vColor;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = vColor;\n" +
                    "}\n";

    // OpenGL ES程序句柄
    private int mProgram;
    private FloatBuffer mVertexBuffer;
    private ShortBuffer mDrawIndexBuffer;

    // 此数组中每个顶点的坐标数
    static final int COORDS_PER_VERTEX = 3;
    // 正方形四个顶点坐标
    static float squareCoords[] = {
            -0.5f, 0.5f, 0.0f,   // top left
            -0.5f, -0.5f, 0.0f,  // bottom left
            0.5f, -0.5f, 0.0f,   // bottom right
            0.5f, 0.5f, 0.0f,    // top right
    };

    // 绘制顶点的顺序
    private short drawOrder[] = {0, 1, 2, 0, 2, 3};

    // 设置颜色为红色
    float color[] = {1.0f, 0.0f, 0.0f, 1.0f};

    /**
     * 顶点着色器vPosition属性的句柄
     */
    private int mPositionHandle;
    /**
     * 片段着色器vColor属性的句柄
     */
    private int mColorHandle;
    /**
     * 顶点着色器uMVPMatrix属性的句柄
     */
    private int vPMatrixHandle;

    private final int vertexCount = squareCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // vPMatrix是“模型视图投影矩阵”的缩写
    // 最终变化矩阵
    private final float[] mMVPMatrix = new float[16];
    // 投影矩阵
    private final float[] mProjectionMatrix = new float[16];
    // 相机矩阵
    private final float[] mViewMatrix = new float[16];

    public Square() {
        // 初始化形状坐标的顶点字节缓冲区
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(squareCoords);
        mVertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        mDrawIndexBuffer = dlb.asShortBuffer();
        mDrawIndexBuffer.put(drawOrder);
        mDrawIndexBuffer.position(0);
    }

    public void surfaceCreated() {
        int vertexShader = GLESUtils.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = GLESUtils.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // 创建空的OpenGL ES程序
        mProgram = GLES20.glCreateProgram();
        // 将顶点着色器添加到程序中
        GLES20.glAttachShader(mProgram, vertexShader);
        // 将片段着色器添加到程序中
        GLES20.glAttachShader(mProgram, fragmentShader);
        // 创建OpenGL ES程序可执行文件
        GLES20.glLinkProgram(mProgram);

        // 获取顶点着色器vPosition成员的句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // 获取片段着色器vColor成员的句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        // 获取绘制矩阵句柄
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void surfaceChanged(int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio;
        if (width > height) {
            ratio = (float) width / height;
            // 横屏使用
            // 透视投影，特点：物体离视点越远，呈现出来的越小。离视点越近，呈现出来的越大
            // 该投影矩阵应用于对象坐标
            Matrix.orthoM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        } else {
            ratio = (float) height / width;
            // 竖屏使用
            // 透视投影，特点：物体离视点越远，呈现出来的越小。离视点越近，呈现出来的越大
            // 该投影矩阵应用于对象坐标
            Matrix.orthoM(mProjectionMatrix, 0, -1, 1, -ratio, ratio, 3, 7);
        }

        Matrix.setLookAtM(mViewMatrix, 0,
                0, 0, 3f,
                0f, 0f, 0f,
                0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }

    public void draw() {
        // 将程序添加到OpenGL ES环境
        GLES20.glUseProgram(mProgram);

        // 重新绘制背景色为黑色
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // 为正方形顶点启用控制柄
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // 准备正方形坐标数据
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, mVertexBuffer);

        // 设置绘制正方形的颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // 将投影和视图变换传递给着色器
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mMVPMatrix, 0);

        // 画正方形
        // 顶点法绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);
        // 索引法绘制
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, mDrawIndexBuffer);

        // 禁用顶点阵列
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
