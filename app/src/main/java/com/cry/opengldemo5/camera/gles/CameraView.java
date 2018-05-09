package com.cry.opengldemo5.camera.gles;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.cry.opengldemo5.camera.core.CameraAPI14;
import com.cry.opengldemo5.camera.core.ICamera;
import com.cry.opengldemo5.camera.core.ISize;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * DESCRIPTION: CameraRender Wrapper
 * Author: Cry
 * DATE: 2018/5/9 下午10:02
 */
public class CameraView extends GLSurfaceView implements GLSurfaceView.Renderer {

    public ICamera mCameraApi;
    private int mCameraId = 0;
    public CameraDrawer mCameraDrawer;
    private Runnable mRunnable;

    public CameraView(Context context) {
        super(context);
        initEGL(context);
    }

    //初始化OpenGL ES2.0
    private void initEGL(Context context) {
        setEGLContextClientVersion(2);
        setRenderer(this);
        //只有刷新之后，才会去重绘
        setRenderMode(RENDERMODE_WHEN_DIRTY);

        mCameraApi = new CameraAPI14();
        mCameraDrawer = new CameraDrawer(context.getResources());
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCameraDrawer.onSurfaceCreated(gl, config);
        if(mRunnable!=null){
            mRunnable.run();
            mRunnable=null;
        }
        //在onSurfaceCreated中打开SurfaceView
        mCameraApi.open(mCameraId);
        //设置CameraDrawer
        mCameraDrawer.setCameraId(mCameraId);
        ISize previewSize = mCameraApi.getPreviewSize();
        mCameraDrawer.setPreviewSize(previewSize.getHeight(),previewSize.getWidth());
        mCameraApi.setPreviewTexture(mCameraDrawer.getSurfaceTexture());
        //默认使用的GLThread
        mCameraDrawer.getSurfaceTexture().setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();
            }
        });
        mCameraApi.preview();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCameraDrawer.onSurfaceChanged(gl, width, height);
        //设置ViewPort是必须要做的
        GLES20.glViewport(0, 0, width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mCameraDrawer.onDrawFrame(gl);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraApi.close();
    }
}
