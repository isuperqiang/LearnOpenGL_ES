package com.richie.opengl.util;

import android.opengl.Matrix;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author Richie on 2018.08.23
 */
public final class MatrixState {
    // 相机矩阵
    private float[] mMatrixCamera = new float[16];
    // 投影矩阵
    private float[] mMatrixProjection = new float[16];
    // 单位矩阵
    private float[] mMatrixCurrent = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };

    // 变换矩阵堆栈
    private LinkedList<float[]> mStack = new LinkedList<>();

    // 保护现场
    public void pushMatrix() {
        mStack.push(Arrays.copyOf(mMatrixCurrent, mMatrixCurrent.length));
    }

    // 恢复现场
    public void popMatrix() {
        mMatrixCurrent = mStack.pop();
    }

    public void clearMatrix() {
        mStack.clear();
    }

    // 平移变换
    public void translate(float x, float y, float z) {
        Matrix.translateM(mMatrixCurrent, 0, x, y, z);
    }

    // 旋转变换
    public void rotate(float angle, float x, float y, float z) {
        Matrix.rotateM(mMatrixCurrent, 0, angle, x, y, z);
    }

    // 缩放变换
    public void scale(float x, float y, float z) {
        Matrix.scaleM(mMatrixCurrent, 0, x, y, z);
    }

    // 设置相机
    public void setCamera(float ex, float ey, float ez, float cx, float cy, float cz, float ux, float uy, float uz) {
        Matrix.setLookAtM(mMatrixCamera, 0, ex, ey, ez, cx, cy, cz, ux, uy, uz);
    }

    // 透视投影
    public void frustum(float left, float right, float bottom, float top, float near, float far) {
        Matrix.frustumM(mMatrixProjection, 0, left, right, bottom, top, near, far);
    }

    // 正交投影
    public void ortho(float left, float right, float bottom, float top, float near, float far) {
        Matrix.orthoM(mMatrixProjection, 0, left, right, bottom, top, near, far);
    }

    // 最终结果
    public float[] getFinalMatrix() {
        float[] ans = new float[16];
        Matrix.multiplyMM(ans, 0, mMatrixCamera, 0, mMatrixCurrent, 0);
        Matrix.multiplyMM(ans, 0, mMatrixProjection, 0, ans, 0);
        return ans;
    }

}
