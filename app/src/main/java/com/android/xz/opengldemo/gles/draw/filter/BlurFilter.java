package com.android.xz.opengldemo.gles.draw.filter;

import android.opengl.GLES20;
import android.util.Log;
import android.view.ViewGroup;

import com.android.xz.opengldemo.gles.GLESUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 灰度滤镜
 */
public class BlurFilter extends BaseFilter {

    private static final String TAG = BlurFilter.class.getSimpleName();

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
                    // 顶点坐标
                    "attribute vec4 vPosition;\n" +
                    // 纹理坐标
                    "attribute vec2 vTexCoordinate;\n" +
                    "varying vec2 aTexCoordinate;\n" +
                    "void main() {\n" +
                    "  gl_Position = uMVPMatrix * vPosition;\n" +
                    "  aTexCoordinate = vTexCoordinate;\n" +
                    "}\n";

    // 片段着色器代码
    private String fragmentShaderCode = HORIZONTAL_BLUR_SHADER;

    public static final String HORIZONTAL_BLUR_SHADER =
            "precision mediump float;\n" +
                    "uniform sampler2D vTexture;\n" +
                    "varying vec2 aTexCoordinate;\n" +
                    "uniform int uBlurRadius;\n" +
                    "uniform vec2 uBlurOffset;\n" +
                    "uniform float weight[30];" +
                    "// 边界处理\n" +

                    "vec2 clampCoordinate(vec2 coordinate) {\n" +
                    "    return vec2(clamp(coordinate.x, 0.0, 1.0), clamp(coordinate.y, 0.0, 1.0));\n" +
                    "}\n" +

                    "void main() {\n" +
                    "    vec4 rgba = texture2D(vTexture, aTexCoordinate);\n" +

                    "    if (uBlurRadius <= 1) {\n" +
                    "        gl_FragColor = rgba;\n" +
                    "        return;\n" +
                    "    }\n" +

                    "    vec3 finalColor = rgba.rgb * weight[0];\n" +

                    // 水平方向累加
                    "    for (int i = 1; i < uBlurRadius + 1; i++) {\n" +
                    "        finalColor += texture2D(vTexture, clampCoordinate(aTexCoordinate + vec2(uBlurOffset.x * float(i), 0.0))).rgb * weight[i];\n" +
                    "        finalColor += texture2D(vTexture, clampCoordinate(aTexCoordinate - vec2(uBlurOffset.x * float(i), 0.0))).rgb * weight[i];\n" +
                    "    }\n" +

                    "    gl_FragColor = vec4(finalColor, rgba.a);\n" +
                    "}\n";

    public static final String VERTICAL_BLUR_SHADER =
            "precision mediump float;\n" +
                    "uniform sampler2D vTexture;\n" +
                    "varying vec2 aTexCoordinate;\n" +
                    "uniform int uBlurRadius;\n" +
                    "uniform vec2 uBlurOffset;\n" +
                    "uniform float weight[30];" +
                    "// 边界处理\n" +

                    "vec2 clampCoordinate(vec2 coordinate) {\n" +
                    "    return vec2(clamp(coordinate.x, 0.0, 1.0), clamp(coordinate.y, 0.0, 1.0));\n" +
                    "}\n" +

                    "void main() {\n" +
                    "    vec4 rgba = texture2D(vTexture, aTexCoordinate);\n" +

                    "    if (uBlurRadius <= 1) {\n" +
                    "        gl_FragColor = rgba;\n" +
                    "        return;\n" +
                    "    }\n" +

                    "    vec3 finalColor = rgba.rgb * weight[0];\n" +

                    // 垂直方向累加
                    "    for (int i = 1; i < uBlurRadius + 1; i++) {\n" +
                    "        finalColor += texture2D(vTexture, clampCoordinate(aTexCoordinate + vec2(0.0, uBlurOffset.y * float(i)))).rgb * weight[i];\n" +
                    "        finalColor += texture2D(vTexture, clampCoordinate(aTexCoordinate - vec2(0.0, uBlurOffset.y * float(i)))).rgb * weight[i];\n" +
                    "    }\n" +

                    "    gl_FragColor = vec4(finalColor, rgba.a);\n" +
                    "}\n";

    private int mProgram;

    // 顶点坐标缓冲区
    private FloatBuffer vertexBuffer;

    // 纹理坐标缓冲区
    private FloatBuffer textureBuffer;

    // 此数组中每个顶点的坐标数
    static final int COORDS_PER_VERTEX = 2;

    /**
     * 顶点坐标数组
     * 顶点坐标系中原点(0,0)在画布中心
     * 向左为x轴正方向
     * 向上为y轴正方向
     * 画布四个角坐标如下：
     * (-1, 1),(1, 1)
     * (-1,-1),(1,-1)
     */
    private float vertexCoords[] = {
            -1.0f, 1.0f,   // 左上
            -1.0f, -1.0f,  // 左下
            1.0f, 1.0f,    // 右上
            1.0f, -1.0f,   // 右下
    };

    /**
     * 纹理坐标数组
     * 这里我们需要注意纹理坐标系，原点(0,0s)在画布左下角
     * 向左为x轴正方向
     * 向上为y轴正方向
     * 画布四个角坐标如下：
     * (0,1),(1,1)
     * (0,0),(1,0)
     */
    private float textureCoords[] = {
            0.0f, 1.0f, // 左上
            0.0f, 0.0f, // 左下
            1.0f, 1.0f, // 右上
            1.0f, 0.0f, // 右下
    };

    private int positionHandle;
    // 纹理坐标句柄
    private int texCoordinateHandle;
    // 纹理Texture句柄
    private int texHandle;
    // Use to access and set the view transformation
    private int vPMatrixHandle;

    private final int vertexCount = vertexCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private int mBlurRadius = 5;
    private int mBlurOffset = 1;
    private float[] mBlurWeight;
    private int blurRadiusHandle;
    private int blurOffsetHandle;
    private int blurWeightHandle;
    private int mTextureWidth;
    private int mTextureHeight;

    public BlurFilter() {
        // 初始化形状坐标的顶点字节缓冲区
        vertexBuffer = ByteBuffer.allocateDirect(vertexCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexCoords);
        vertexBuffer.position(0);

        // 初始化纹理坐标顶点字节缓冲区
        textureBuffer = ByteBuffer.allocateDirect(textureCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureCoords);
        textureBuffer.position(0);
    }

    public void setImageFilter(String filter) {
        fragmentShaderCode = filter;
    }

    public void setTextureSize(int width, int height) {
        mTextureWidth = width;
        mTextureHeight = height;
    }

    @Override
    public void surfaceCreated() {
        // 加载顶点着色器程序
        int vertexShader = GLESUtils.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        // 加载片段着色器程序
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
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // 获取顶点着色器中纹理坐标的句柄
        texCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "vTexCoordinate");
        // 获取绘制矩阵句柄
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        // 获取Texture句柄
        texHandle = GLES20.glGetUniformLocation(mProgram, "vTexture");

        blurRadiusHandle = GLES20.glGetUniformLocation(mProgram, "uBlurRadius");
        blurOffsetHandle = GLES20.glGetUniformLocation(mProgram, "uBlurOffset");
        blurWeightHandle = GLES20.glGetUniformLocation(mProgram, "weight");
    }

    @Override
    public void surfaceChanged(int width, int height) {
        super.surfaceChanged(width, height);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDraw(int textureId, float[] matrix) {
        // 将程序添加到OpenGL ES环境
        GLES20.glUseProgram(mProgram);

        // 重新绘制背景色为黑色
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // 为正方形顶点启用控制句柄
        GLES20.glEnableVertexAttribArray(positionHandle);
        // 写入坐标数据
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        // 启用纹理坐标控制句柄
        GLES20.glEnableVertexAttribArray(texCoordinateHandle);
        // 写入坐标数据
        GLES20.glVertexAttribPointer(texCoordinateHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, textureBuffer);

        // 将投影和视图变换传递给着色器
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, matrix, 0);

        GLES20.glUniform1i(blurRadiusHandle, mBlurRadius);
        GLES20.glUniform2f(blurOffsetHandle, mBlurOffset / (float) mTextureWidth, mBlurOffset / (float) mTextureHeight);
        mBlurWeight = constructGaussianWeight(mBlurRadius);
        GLES20.glUniform1fv(blurWeightHandle, mBlurWeight.length, mBlurWeight, 0);

        // 激活纹理编号0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // 绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        // 设置纹理采样器编号，该编号和glActiveTexture中设置的编号相同
        GLES20.glUniform1i(texHandle, 0);

        // 绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        // 禁用顶点阵列
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texCoordinateHandle);
    }

    @Override
    public void release() {
        super.release();
        GLES20.glDeleteProgram(mProgram);
        mProgram = -1;
    }

    /**
     * 构造stack权重
     *
     * @param blurRadius
     * @return
     */
    private float[] constructStackWeight(int blurRadius) {
        float[] weight = new float[blurRadius + 1];
        for (int i = 0; i < blurRadius + 1; i++) {
            weight[i] = getStackWeight(i, blurRadius);
        }
        return weight;
    }

    /**
     * 构造高斯权重
     *
     * @param blurRadius
     * @return
     */
    private float[] constructGaussianWeight(int blurRadius) {
        float[] weight = new float[blurRadius + 1];
        for (int i = 0; i < blurRadius + 1; i++) {
            weight[i] = getGaussianWeight(i, blurRadius);
        }
        return weight;
    }

    float getStackWeight(int i, int blurRadius) {
        int sum = (blurRadius + 1) * blurRadius + (blurRadius + 1);
        return ((blurRadius + 1) - i) / (float) sum;
    }

    static final float PI = 3.1415926f;

    float getGaussianWeight(int i, int blurRadius) {
        float sigma = (float) (blurRadius + 1) / 3.0f;
        return (float) ((1.0f / Math.sqrt(2.0 * PI * sigma * sigma)) * Math.exp(-(float) (i * i) / (2.0f * sigma * sigma)) / 1);
    }
}
