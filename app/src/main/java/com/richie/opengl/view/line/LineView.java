package com.richie.opengl.view.line;

import android.content.Context;
import android.util.AttributeSet;

import com.richie.opengl.view.GraphView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author Richie on 2018.08.23
 */
public class LineView extends GraphView {
    private Line mLine;

    public LineView(Context context) {
        super(context);
    }

    public LineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onConstruct() {
        super.onConstruct();
        mLine = new Line();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        super.onSurfaceCreated(gl10, eglConfig);
        mLine.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        super.onSurfaceChanged(gl10, width, height);
        mLine.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame() {
        mLine.onDrawFrame();
    }
}
