package com.richie.opengl.view.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;

import com.richie.easylog.ILogger;
import com.richie.easylog.LoggerFactory;
import com.richie.opengl.view.GraphView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author Richie on 2019.04.20
 */
public class CameraView extends GraphView {
    private final ILogger logger = LoggerFactory.getLogger(CameraView.class);
    private CameraRenderer mCameraRenderer;
    private CameraHolder mCameraHolder;

    public CameraView(Context context) {
        super(context);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onConstruct() {
        super.onConstruct();
        mCameraRenderer = new CameraRenderer();
        mCameraHolder = new CameraHolder();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        super.onSurfaceCreated(gl10, eglConfig);
        mCameraRenderer.onSurfaceCreated();
        SurfaceTexture surfaceTexture = mCameraRenderer.getSurfaceTexture();
        surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();
            }
        });
        mCameraHolder.openCamera((Activity) getContext());
        mCameraHolder.setPreviewTexture(surfaceTexture);
        mCameraHolder.startPreview();
        Point previewPoint = mCameraHolder.getPreviewPoint();
        mCameraRenderer.setPreviewSize(previewPoint.x, previewPoint.y);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        super.onSurfaceChanged(gl10, width, height);
        mCameraRenderer.onSurfaceChanged(width, height);
    }

    @Override
    protected void onDrawFrame() {
        mCameraRenderer.onDrawFrame();
    }

    @Override
    public void onResume() {
        super.onResume();
        logger.info("onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        logger.info("onPause");
        mCameraHolder.stopPreview();
        mCameraHolder.release();
    }

    public void snapshot() {
        mCameraRenderer.setShot(true);
    }
}
