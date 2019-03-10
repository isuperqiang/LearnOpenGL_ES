package com.richie.opengl.view.triangle;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.richie.opengl.util.GLESUtils;
import com.richie.opengl.view.GraphRender;

import java.nio.FloatBuffer;

/**
 * @author Richie on 2018.08.05
 */
public class TriangleColorful implements GraphRender {
    private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 aPosition;" +
                    "attribute vec4 aColor;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * aPosition;" +
                    "  vColor = aColor;" +
                    "}";
    private static final String FRAGMENT_SHADER =
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    // Set color with red, green, blue and alpha (opacity) values
    private static final float[] COLORS = {
            0.8f, 0.2f, 0.3f, 1.0f,
            0.2f, 0.6f, 0.2f, 1.0f,
            0.2f, 0.2f, 0.8f, 1.0f
    };
    // number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 2;
    private static final int COORDS_PER_COLOR = 4;
    private static final float[] COORDS = {
            0, 0.6f,
            -0.6f, -0.3f,
            0.6f, -0.3f,
    };
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;
    private int mProgram;
    private float[] mMvpMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];

    @Override
    public void onSurfaceCreated() {
        mVertexBuffer = GLESUtils.createFloatBuffer(COORDS);
        mColorBuffer = GLESUtils.createFloatBuffer(COLORS);
        int vertexShader = GLESUtils.createVertexShader(VERTEX_SHADER);
        int fragmentShader = GLESUtils.createFragmentShader(FRAGMENT_SHADER);
        mProgram = GLESUtils.createProgram(vertexShader, fragmentShader);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        float ratio = 1.0f * width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 2.5f, 6);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 3, 0, 0, 0, 0, 1, 0);
        Matrix.multiplyMM(mMvpMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }

    @Override
    public void onDrawFrame() {
        GLES20.glUseProgram(mProgram);
        int position = GLES20.glGetAttribLocation(mProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(position);
        GLES20.glVertexAttribPointer(position, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                COORDS_PER_VERTEX * GLESUtils.SIZEOF_FLOAT, mVertexBuffer);

        int color = GLES20.glGetAttribLocation(mProgram, "aColor");
        GLES20.glEnableVertexAttribArray(color);
        GLES20.glVertexAttribPointer(color, COORDS_PER_COLOR, GLES20.GL_FLOAT, false,
                COORDS_PER_COLOR * GLESUtils.SIZEOF_FLOAT, mColorBuffer);

        int mvpMatrix = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrix, 1, false, mMvpMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, COORDS.length / COORDS_PER_VERTEX);

        GLES20.glDisableVertexAttribArray(position);
        GLES20.glDisableVertexAttribArray(color);
        GLES20.glUseProgram(0);
    }
}
