package com.richie.opengl.view;

/**
 * @author Richie on 2018.08.05
 */
public interface GraphRender {
    void onSurfaceCreated();

    void onSurfaceChanged(int width, int height);

    void onDrawFrame(float[] matrix);
}
