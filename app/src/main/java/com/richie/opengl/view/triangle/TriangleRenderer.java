package com.richie.opengl.view.triangle;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.richie.opengl.util.GLESUtils;
import com.richie.opengl.view.GraphRenderer;

import java.nio.FloatBuffer;

/**
 * @author Richie on 2018.08.05
 */
public class TriangleRenderer implements GraphRenderer {
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
    // Set color with red, green, blue and alpha (opacity) values
    private static final float[] COLORS = {0.8f, 0.5f, 0.3f, 1.0f};
    // number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 2;
    private static final float[] COORDS = {
            0, 0.6f,
            -0.6f, -0.3f,
            0.6f, -0.3f,
    };
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
        mVertexBuffer = GLESUtils.createFloatBuffer(COORDS);
        int vertexShader = GLESUtils.createVertexShader(VERTEX_SHADER);
        int fragmentShader = GLESUtils.createFragmentShader(FRAGMENT_SHADER);
        mProgram = GLESUtils.createProgram(vertexShader, fragmentShader);
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mMvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 2.5f, 6);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 3, 0, 0, 0, 0, 1, 0);
        Matrix.multiplyMM(mMvpMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame() {
        GLES20.glUseProgram(mProgram);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                0, mVertexBuffer);
        GLES20.glUniform4fv(mColorHandle, 1, COLORS, 0);

        GLES20.glUniformMatrix4fv(mMvpMatrixHandle, 1, false, mMvpMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, COORDS.length / COORDS_PER_VERTEX);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glUseProgram(0);
    }
}
