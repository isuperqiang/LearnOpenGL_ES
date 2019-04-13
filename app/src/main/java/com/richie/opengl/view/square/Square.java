package com.richie.opengl.view.square;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.richie.opengl.util.GLESUtils;
import com.richie.opengl.view.GraphRender;

import java.nio.FloatBuffer;

/**
 * @author Richie on 2018.08.05
 */
public class Square implements GraphRender {
    private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 aPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * aPosition;" +
                    "}";
    private static final String FRAGMENT_SHADER =
            "precision mediump float;" +
                    "uniform vec4 uColor;" +
                    "void main() {" +
                    "  gl_FragColor = uColor;" +
                    "}";
    private static final int COORDS_PER_VERTEX = 2;
    private static final float[] VERTEX_COORDS = {
            0.5f, 0.5f,
            -0.5f, 0.5f,
            0.5f, -0.5f,
            -0.5f, -0.5f
    };
    private static final float[] COLORS = {0.6f, 0.8f, 0.6f, 1.0f};
    private FloatBuffer mVertexBuffer;
    private int mProgram;
    private float[] mMvpMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private int mColorHandle;
    private int mMvpMatrixHandle;
    private int mPositionHandle;

    @Override
    public void onSurfaceCreated() {
        mVertexBuffer = GLESUtils.createFloatBuffer(VERTEX_COORDS);
        int vertexShader = GLESUtils.createVertexShader(VERTEX_SHADER);
        int fragmentShader = GLESUtils.createFragmentShader(FRAGMENT_SHADER);
        mProgram = GLESUtils.createProgram(vertexShader, fragmentShader);
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mMvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        float ratio = 1.0f * width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 6);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 3, 0, 0, 0, 0, 1, 0);
        Matrix.multiplyMM(mMvpMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame() {
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(mMvpMatrixHandle, 1, false, mMvpMatrix, 0);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                COORDS_PER_VERTEX * GLESUtils.SIZEOF_FLOAT, mVertexBuffer);

        GLES20.glUniform4fv(mColorHandle, 1, COLORS, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glUseProgram(0);
    }
}
