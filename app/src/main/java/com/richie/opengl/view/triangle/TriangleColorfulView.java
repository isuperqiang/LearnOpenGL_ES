package com.richie.opengl.view.triangle;

import android.content.Context;
import android.util.AttributeSet;

import com.richie.opengl.view.GraphView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author Richie on 2018.08.05
 * 绘制多彩三角形
 */
public class TriangleColorfulView extends GraphView {
    private TriangleColorfulRenderer mTriangle;

    public TriangleColorfulView(Context context) {
        super(context);
    }

    public TriangleColorfulView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onConstruct() {
        super.onConstruct();
        mTriangle = new TriangleColorfulRenderer();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        super.onSurfaceCreated(gl10, eglConfig);
        mTriangle.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        super.onSurfaceChanged(gl10, width, height);
        mTriangle.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame() {
        mTriangle.onDrawFrame();
    }
}
