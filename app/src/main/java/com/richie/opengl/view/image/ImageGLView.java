package com.richie.opengl.view.image;

import android.content.Context;
import android.util.AttributeSet;

import com.richie.opengl.util.LimitFpsUtil;
import com.richie.opengl.view.GraphView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author Richie on 2018.08.06
 */
public class ImageGLView extends GraphView {
    private ImageRenderer mImage;

    public ImageGLView(Context context) {
        super(context);
    }

    public ImageGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onConstruct() {
        super.onConstruct();
        mImage = new ImageRenderer();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        super.onSurfaceCreated(gl10, eglConfig);
        mImage.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        super.onSurfaceChanged(gl10, width, height);
        mImage.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame() {
        LimitFpsUtil.limitFrameRate(30);
        mImage.onDrawFrame();
        requestRender();
    }
}
