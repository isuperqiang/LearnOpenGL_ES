package com.richie.opengl.view.camera;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Environment;
import android.widget.Toast;

import com.richie.easylog.ILogger;
import com.richie.easylog.LoggerFactory;
import com.richie.opengl.GlApp;
import com.richie.opengl.util.GLESUtils;
import com.richie.opengl.util.ThreadHelper;
import com.richie.opengl.view.GraphRenderer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * @author Richie on 2019.04.20
 */
public class CameraRenderer implements GraphRenderer {
    private static final String VERTEX_SHADER =
            "attribute vec4 aPosition;" + // 标准化设备坐标点(NDC) 坐标点
                    "attribute vec4 aTextureCoord;" + // 纹理坐标点
                    "uniform mat4 uMVPMatrix;" + // NDC MVP 变换矩阵
                    "uniform mat4 uTexMatrix;" + // 纹理坐标变换矩阵
                    "varying vec2 vTextureCoord;" + // 纹理坐标点变换后输出
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * aPosition;" + // 对 NDC 坐标点进行变换
                    "  vTextureCoord = (uTexMatrix * aTextureCoord).xy;" + // 对纹理坐标点进行变换
                    "}";
    private static final String FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" + // 声明OES纹理使用扩展
                    "precision mediump float;" + // 声明精度
                    "varying vec2 vTextureCoord;" + // 顶点着色器输出经图元装配和栅格化的纹理坐标点序列
                    "uniform samplerExternalOES sTexture;" + // OES 纹理，接收相机纹理作为输入
                    "void main() {" +
                    "  gl_FragColor = texture2D(sTexture, vTextureCoord);" +
                    "}";
    // 顶点坐标
    private static final float[] VERTEX = {   // in counterclockwise order:
            1, 1,   // top right
            -1, 1,  // top left
            -1, -1, // bottom left
            1, -1,   // bottom right
    };
    // 纹理坐标
    private static final float[] TEXTURE = {   // in counterclockwise order:
            1, 0,  // top right
            0, 0,  // top left
            0, 1,  // bottom left
            1, 1,  // bottom right
    };
    private final ILogger logger = LoggerFactory.getLogger(getClass());
    private SurfaceTexture mSurfaceTexture;
    // 程序句柄
    private int mProgram;
    // 顶点坐标Buffer
    private FloatBuffer mVerBuffer;
    // 纹理坐标Buffer
    private FloatBuffer mTexBuffer;
    // 纹理句柄
    private int mTextureID;
    // 顶点坐标 MVP 变换矩阵
    private float[] mMvpMatrix;
    // 纹理坐标变换矩阵
    private float[] mTexMatrix = new float[16];
    // 用于存储回调数据的buffer
    private ByteBuffer[] mOutPutBuffer = new ByteBuffer[3];
    // 回调数据使用的buffer索引
    private int mIndexOutput = 0;
    private int mPreviewWidth = 1280;
    private int mPreviewHeight = 720;
    private volatile boolean mIsShot;
    private int mMvpMatrixHandle;
    private int mPositionHandle;
    private int mTexCoordHandle;
    private int mTexMatrixHandle;

    public CameraRenderer() {
        mVerBuffer = GLESUtils.createFloatBuffer(VERTEX);
        mTexBuffer = GLESUtils.createFloatBuffer(TEXTURE);
    }

    @Override
    public void onSurfaceCreated() {
        mTextureID = GLESUtils.createOESTexture();
        mSurfaceTexture = new SurfaceTexture(mTextureID);
        int vertexShader = GLESUtils.createShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fragmentShader = GLESUtils.createShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
        mProgram = GLESUtils.createProgram(vertexShader, fragmentShader);
        logger.info("textureId:{}, vertexShader:{}, fragmentShader:{}, program:{}", mTextureID,
                vertexShader, fragmentShader, mProgram);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        mMvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mTexMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uTexMatrix");
        int texUnitHandle = GLES20.glGetAttribLocation(mProgram, "sTexture");
        GLES20.glUniform1i(texUnitHandle, 0);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        logger.info("onSurfaceChanged width:{}, height:{}, prevHeight:{}, prevWidth:{}",
                width, height, mPreviewHeight, mPreviewWidth);
        mMvpMatrix = Arrays.copyOf(GLESUtils.IDENTITY_MATRIX, 16);
        GLESUtils.changeMVPMatrix(mMvpMatrix, width, height, mPreviewHeight, mPreviewWidth);
        GLESUtils.rotate(mMvpMatrix, 180);
        GLESUtils.flip(mMvpMatrix, true, false);
        logger.info("MVPMatrix:{}", Arrays.toString(mMvpMatrix));
    }

    @Override
    public void onDrawFrame() {
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mTexMatrix);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);
        GLES20.glUseProgram(mProgram);
        GLES20.glUniformMatrix4fv(mMvpMatrixHandle, 1, false, mMvpMatrix, 0);
        GLES20.glUniformMatrix4fv(mTexMatrixHandle, 1, false, mTexMatrix, 0);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, mVerBuffer);
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, mTexBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordHandle);

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glUseProgram(0);
    }

    public void setPreviewSize(int w, int h) {
        mPreviewWidth = w;
        mPreviewHeight = h;
    }

    public void setShot(boolean shot) {
        mIsShot = shot;
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    private void handleFrame(final ByteBuffer buffer) {
        ThreadHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(mPreviewHeight, mPreviewWidth, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(buffer);
                try {
                    saveBitmap(bitmap, getExternalPath());
                    ThreadHelper.getInstance().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GlApp.getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        });
    }


    private String getExternalPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    }

    private void saveBitmap(Bitmap bitmap, String dir) throws IOException {
        String fileName = "opengl_" + System.currentTimeMillis() + ".jpg";
        File dest = new File(dir, fileName);
        BufferedOutputStream bos = null;
        try {
            FileOutputStream fos = new FileOutputStream(dest);
            bos = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
        } finally {
            if (bos != null) {
                bos.close();
            }
        }
    }

    private void bindFrameBuffer() {
        int[] frameBufferId = new int[1];
        GLES20.glGenFramebuffers(1, frameBufferId, 0);
        mIndexOutput = mIndexOutput++ >= 2 ? 0 : mIndexOutput;
        if (mOutPutBuffer[mIndexOutput] == null) {
            mOutPutBuffer[mIndexOutput] = ByteBuffer.allocate(mPreviewWidth * mPreviewHeight * 4);
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferId[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mTextureID, 0);
        mOutPutBuffer[mIndexOutput].rewind();
        GLES20.glReadPixels(0, 0, mPreviewWidth, mPreviewHeight,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mOutPutBuffer[mIndexOutput]);
        handleFrame(mOutPutBuffer[mIndexOutput]);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

}
