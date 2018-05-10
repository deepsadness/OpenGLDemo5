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
    private TextureController mController;
    public Camera1Render mRender;
    private int mCameraId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);

        mSurface = (SurfaceView) findViewById(R.id.sf_w);
    }

    //同样将生命周期方法，回调给内部的GLView
    @Override
    protected void onResume() {
        super.onResume();
        if (mController!=null) {
            mController.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mController != null) {
            mController.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mController != null) {
            mController.destroy();
        }
    }

    @Override
    protected void initCamera() {
        mRender = new Camera1Render();
        mController = new TextureController(Camera2Activity.this);

        mSurface.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //这里需要将这个Holder设置到我们的Render当中
                //将我们预览的surface传递进去
                mController.surfaceCreated(holder);
                //将Render传递进去
                mController.setRender(mRender);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                //Surface发生了变化
                //同样将对应的生命周期方法传递过去。来调用内部的GL同步生命周期方法
                mController.surfaceChanged(width, height);

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                //预览的SurfaceDestory
                mController.surfaceDestroyed();
            }
        });
    }

    /**
     * Camera1 Api 中对Render生命周期做出对应的反应.
     * 主要需要相应其实就是在onSurfaceCreated 打开相机的预览
     */
    public class Camera1Render implements GLSurfaceView.Renderer {
        private CameraAPI14 mCameraApi;
        public ISize mPreviewSize;
        public ISize mPictureSize;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //创建Camera
            mCameraApi = new CameraAPI14();
            //在onSurfaceCreated中打开SurfaceView
            mCameraApi.open(mCameraId);

            mPreviewSize = mCameraApi.getPreviewSize();
            mPictureSize = mCameraApi.getPictureSize();


            mController.setPreviewSize(mPreviewSize);
            mController.setPicSize(mPictureSize);
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
