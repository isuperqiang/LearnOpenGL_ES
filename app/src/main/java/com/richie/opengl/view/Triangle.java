package com.richie.opengl.view;

import android.opengl.GLES20;

import com.richie.opengl.util.GLESUtils;

import java.nio.FloatBuffer;

/**
 * @author Richie on 2018.08.05
 */
public class Triangle implements GraphRender {
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
    private static final float[] COLORS = {0.4f, 0.5f, 0.8f, 1.0f};
    // number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 3;
    private static final float[] COORDS = {
            0, 0.6f, 0,
            -0.6f, -0.3f, 0,
            0.6f, -0.3f, 0
    };
    private FloatBuffer mFloatBuffer;
    private int mProgram;

    @Override
    public void onSurfaceCreated() {
        mFloatBuffer = GLESUtils.createFloatBuffer(COORDS);
        int vertexShader = GLESUtils.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fragmentShader = GLESUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
        mProgram = GLESUtils.createProgram(vertexShader, fragmentShader);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {

    }

    @Override
    public void onDrawFrame(float[] matrix) {
        GLES20.glUseProgram(mProgram);
        int position = GLES20.glGetAttribLocation(mProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(position);
        GLES20.glVertexAttribPointer(position, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                COORDS_PER_VERTEX * GLESUtils.SIZEOF_FLOAT, mFloatBuffer);

        int color = GLES20.glGetUniformLocation(mProgram, "uColor");
        GLES20.glUniform4fv(color, 1, COLORS, 0);

        int mvpMatrix = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mvpMatrix, 1, false, matrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, COORDS.length / COORDS_PER_VERTEX);

        GLES20.glDisableVertexAttribArray(position);
    }
}
