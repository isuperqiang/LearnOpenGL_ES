package com.richie.opengl.view.camera;


import android.app.Activity;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.richie.easylog.ILogger;
import com.richie.easylog.LoggerFactory;
import com.richie.opengl.util.CameraUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Richie on 2018.08.01
 */
public class CameraHolder {
    private static final int PREVIEW_WIDTH = 1280;
    private static final int PREVIEW_HEIGHT = 720;
    private final ILogger logger = LoggerFactory.getLogger(getClass());
    private Camera mCamera;
    private Point mPreviewPoint;

    public void setPreviewTexture(SurfaceTexture texture) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(texture);
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }

    public void setOnPreviewFrameCallback(final PreviewFrameCallback callback) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    callback.onPreviewFrame(data, mPreviewPoint.x, mPreviewPoint.y);
                }
            });
        }
    }

    public void startPreview() {
        if (mCamera != null) {
            mCamera.startPreview();
        }
    }

    public void release() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
    }

    public Point getPreviewPoint() {
        return mPreviewPoint;
    }

    public void openCamera(Activity activity) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int number = Camera.getNumberOfCameras();
        logger.info("camera number:{}", number);
        for (int i = 0; i < number; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    mCamera = Camera.open(i);
                    CameraUtils.setCameraDisplayOrientation(activity, i, mCamera);
                    Camera.Parameters params = mCamera.getParameters();
                    int[] previewSize = CameraUtils.choosePreviewSize(params, CameraUtils.CAMERA_MODE_16_9);
                    logger.info("previewSize:{}", previewSize);
                    if (previewSize[0] == 0 || previewSize[1] == 0) {
                        previewSize[0] = PREVIEW_WIDTH;
                        previewSize[1] = PREVIEW_HEIGHT;
                    }
                    params.setPreviewSize(previewSize[0], previewSize[1]);
                    mPreviewPoint = new Point(previewSize[0], previewSize[1]);
                    mCamera.setParameters(params);
                } catch (Exception e) {
                    logger.error("openCamera error", e);
                }
                break;
            }
        }
    }

    public void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    private Camera.Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth) {
        Collections.sort(list, new CameraSizeComparator());
        int i = 0;
        for (Camera.Size s : list) {
            if ((s.height >= minWidth) && equalRate(s, th)) {
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;
        }
        return list.get(i);
    }

    private boolean equalRate(Camera.Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.03) {
            return true;
        } else {
            return false;
        }
    }


    interface PreviewFrameCallback {
        /**
         * 预览帧
         *
         * @param bytes
         * @param width  图像的宽度
         * @param height 图像的高度
         */
        void onPreviewFrame(byte[] bytes, int width, int height);
    }

    private class CameraSizeComparator implements Comparator<Camera.Size> {
        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (lhs.height == rhs.height) {
                return 0;
            } else if (lhs.height > rhs.height) {
                return 1;
            } else {
                return -1;
            }
        }
    }

}
