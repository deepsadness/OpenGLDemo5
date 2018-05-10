package com.cry.opengldemo5.camera;

import android.os.Bundle;

import com.cry.opengldemo5.R;

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
public class Camera2Activity extends CameraPermissionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
    }

    @Override
    protected void startCamera() {

    }
}
