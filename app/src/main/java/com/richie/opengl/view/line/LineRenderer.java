package com.richie.opengl.view.line;

import android.opengl.GLES20;

import com.richie.opengl.util.GLESUtils;
import com.richie.opengl.util.MatrixState;
import com.richie.opengl.view.GraphRenderer;

import java.nio.FloatBuffer;

/**
 * @author Richie on 2018.08.23
 */
public class LineRenderer implements GraphRenderer {

    private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 aPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * aPosition;" +
                    "  gl_PointSize = 20.0;" +
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
    private static final float[] VERTEX_COORDS = {
            0.5f, 0.5f,
            -0.5f, 0.5f,
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0f, 0f
    };

    private FloatBuffer mVertexBuffer;
    private int mProgram;
    private float[] mMvpMatrix = new float[16];
    private int mPositionHandle;
    private int mColorHandle;
    private int mMvpMatrixHandle;

    private MatrixState mMatrixState;

    public LineRenderer() {
        mVertexBuffer = GLESUtils.createFloatBuffer(VERTEX_COORDS);
        mMatrixState = new MatrixState();
    }

    @Override
    public void onSurfaceCreated() {
        int vertexShader = GLESUtils.createVertexShader(VERTEX_SHADER);
        int fragmentShader = GLESUtils.createFragmentShader(FRAGMENT_SHADER);
        mProgram = GLESUtils.createProgram(vertexShader, fragmentShader);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor");
        mMvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        float ratio = (float) width / height;
        mMatrixState.frustum(-ratio, ratio, -1, 1, 2.5f, 6);
        mMatrixState.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
        //mMatrixState.translate(0.2f, 0, 0);
        mMatrixState.rotate(30, 0, 0, 1);
        mMatrixState.scale(1, 0.8f, 1);
        mMvpMatrix = mMatrixState.getFinalMatrix();
    }

    @Override
    public void onDrawFrame() {
        GLES20.glUseProgram(mProgram);
        GLES20.glLineWidth(10f);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                0, mVertexBuffer);
        GLES20.glUniform4fv(mColorHandle, 1, COLORS, 0);
        GLES20.glUniformMatrix4fv(mMvpMatrixHandle, 1, false, mMvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, 4);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 4, 1);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glUseProgram(0);
    }
}
