package com.richie.opengl.view.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.richie.opengl.GlApp;
import com.richie.opengl.R;
import com.richie.opengl.util.GLESUtils;
import com.richie.opengl.view.GraphRenderer;

import java.nio.FloatBuffer;

/**
 * @author Richie on 2018.08.06
 */
public class ImageRenderer implements GraphRenderer {
    private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 aPosition;" +
                    "attribute vec2 aTexCoord;" +
                    "varying vec2 vTexCoord;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * aPosition;" +
                    "  vTexCoord = aTexCoord;" +
                    "}";
    private static final String FRAGMENT_SHADER =
            "precision mediump float;" +
                    "uniform sampler2D uTextureUnit;" +
                    "varying vec2 vTexCoord;" +
                    "void main() {" +
                    "  gl_FragColor = texture2D(uTextureUnit, vTexCoord);" +
                    "}";
    private static final int COORDS_PER_VERTEX = 2;
    private static final int COORDS_PER_TEXTURE = 2;
    private static final float[] VERTEX = {
            1, 1,  // top right
            -1, 1, // top left
            1, -1, // bottom right
            -1, -1,// bottom left
    };
    private static final float[] TEXTURE = {
            1, 0,  // top right
            0, 0,  // top left
            1, 1,  // bottom right
            0, 1,  // bottom left
    };
    private final FloatBuffer mVertexBuffer;
    private final FloatBuffer mTextureBuffer;
    private float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private int mProgram;
    private int mMvpMatrixHandle;
    private int mPositionHandle;
    private int mTexCoordHandle;
    private int mBitmapWidth;
    private int mBitmapHeight;

    public ImageRenderer() {
        mVertexBuffer = GLESUtils.createFloatBuffer(VERTEX);
        mTextureBuffer = GLESUtils.createFloatBuffer(TEXTURE);
    }

    @Override
    public void onSurfaceCreated() {
        int vertexShader = GLESUtils.createVertexShader(VERTEX_SHADER);
        int fragmentShader = GLESUtils.createFragmentShader(FRAGMENT_SHADER);
        mProgram = GLESUtils.createProgram(vertexShader, fragmentShader);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mMvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        int texUnitHandle = GLES20.glGetAttribLocation(mProgram, "uTextureUnit");

        // 加载图片并且保存在 OpenGL 纹理系统中
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(GlApp.getContext().getResources(), R.drawable.cat, options);
        mBitmapWidth = bitmap.getWidth();
        mBitmapHeight = bitmap.getHeight();
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //生成纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
        //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        //根据以上指定的参数，生成一个2D纹理
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        // 把选定的纹理单元传给片段着色器
        GLES20.glUniform1i(texUnitHandle, 0);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        // 计算图片缩放
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(GlApp.getContext().getResources(), R.drawable.cat, options);
        int w = options.outWidth;
        int h = options.outHeight;
        // 1000*645, 1080*1920
        float sWH = (float) w / h;
        float sWidthHeight = (float) width / height;
        if (width > height) {
            // landscape
            if (sWH > sWidthHeight) {
                // 正交投影
                Matrix.orthoM(mProjectionMatrix, 0, -sWidthHeight * sWH, sWidthHeight * sWH, -1, 1, 3, 5);
            } else {
                Matrix.orthoM(mProjectionMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH, -1, 1, 3, 5);
            }
        } else {
            // portrait or square
            if (sWH > sWidthHeight) {
                Matrix.orthoM(mProjectionMatrix, 0, -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 5);
            } else {
                Matrix.orthoM(mProjectionMatrix, 0, -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 5);
            }
        }
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // 或者直接使用
        // mMVPMatrix = GLESUtils.changeMvpMatrixInside(width, height,mBitmapWidth,mBitmapHeight);
    }

    @Override
    public void onDrawFrame() {
        GLES20.glUseProgram(mProgram);
        // Enable a handle to the triangle vertices
        GLES20.glUniformMatrix4fv(mMvpMatrixHandle, 1, false, mMVPMatrix, 0);

        // 传入顶点坐标
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                0, mVertexBuffer);
        // 传入纹理坐标
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
        GLES20.glVertexAttribPointer(mTexCoordHandle, COORDS_PER_TEXTURE, GLES20.GL_FLOAT, false,
                0, mTextureBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordHandle);
        GLES20.glUseProgram(0);
    }
}
