package com.cry.opengldemo5.camera;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cry.opengldemo5.R;
import com.cry.opengldemo5.camera.core.CameraAPI14;
import com.cry.opengldemo5.camera.core.ISize;
import com.cry.opengldemo5.camera.gles.TextureController;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Camera2
 * <p>
 * 因为CameraActivity。通过一个滤镜了Camera到预览截面的过程。 surfaceTexture->TextureId->draw
 * Camera2主要是来完成 。 滤镜组的工作。
 * <p>
 * ----------------------------------------
 * <p>
 * - Camera
 * Camera通过Oes纹理textureId与预览的Surface链接
 * - 用于预览Surface -
 * 预览的Surface通过frameBuffer和RenderBuffer 于滤镜组链接
 * - 用于滤镜处理的FrameBuffer。用于滤镜处理的FrameBuffer同样需要隔离开的EGL环境！
 * 滤镜组，将纹理和图形绘制到 textureId,等同于绘制到Surface
 * -
 * -
 */
public class Camera2Activity extends BasePmActivity {

    //这个Surface用来显示
    private SurfaceView mSurface;
    private int mCameraId;
    private TextureController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        mSurface = (SurfaceView) findViewById(R.id.sf_w);
    }

    @Override
    protected void initCamera() {

        mController = new TextureController(Camera2Activity.this);

        mSurface.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //这里需要将这个Holder设置到我们的Render当中
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                //Surface发生了变化
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                //预览的SurfaceDestory

            }
        });
    }

    /**
     * Camera1 Api 中对Render生命周期做出对应的反应
     */
    private class Camera1Render implements GLSurfaceView.Renderer {
        private CameraAPI14 mCameraApi;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //创建Camera
            mCameraApi = new CameraAPI14();
            //在onSurfaceCreated中打开SurfaceView
            mCameraApi.open(mCameraId);

            ISize previewSize = mCameraApi.getPreviewSize();
            ISize pictureSize = mCameraApi.getPictureSize();


            mController.setPreviewSize(previewSize);
            mController.setPicSize(pictureSize);
            mCameraApi.setPreviewTexture(mController.getTexture());
            //默认使用的GLThread
            mController.getTexture().setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    mController.requestRender();
                }
            });
            mCameraApi.preview();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {

        }

        @Override
        public void onDrawFrame(GL10 gl) {

        }

        public void onDestroy() {
            if (mCameraApi != null) {
                mCameraApi.close();
                mCameraApi = null;
            }
        }

    }
}
