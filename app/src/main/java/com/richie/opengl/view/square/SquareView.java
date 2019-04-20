package com.richie.opengl.view.square;

import android.content.Context;
import android.util.AttributeSet;

import com.richie.opengl.view.GraphView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author Richie on 2018.08.05
 */
public class SquareView extends GraphView {
    private SquareRenderer mSquare;

    public SquareView(Context context) {
        super(context);
    }

    public SquareView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onConstruct() {
        super.onConstruct();
        mSquare = new SquareRenderer();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        super.onSurfaceCreated(gl10, eglConfig);
        mSquare.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        super.onSurfaceChanged(gl10, width, height);
        mSquare.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame() {
        mSquare.onDrawFrame();
    }
}
