/*
 *
 * TrackFilter.java
 * 
 * Created by Wuwang on 2016/12/21
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.cry.opengldemo5.camera.gles.filter;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.util.Log;

import com.cry.opengldemo5.utils.EasyGlUtils;

import java.nio.ByteBuffer;


/**
 * Description:
 *
 * 这个TextureFilter 是一个特效处理的Filter?
 * Filter其实就是在Render的对应生命周期进行绘制，变形的Render类的抽象
 *
 * TextureFilter里面有一个CameraFilter 来讲相机传递的参数画出来 oes 纹理
 *
 * 父类默认是画全屏
 *
 * 主要就是完成这一步转换的滤镜。为什么不和后面的其他滤镜组合在一起？我的理解是有部分原因是因为这里导入的是oes的问题，到frameBuffer
 * 后面的滤镜都不需要导入oes的纹理，所以。。。。
 *
 */
public class TextureFilter extends AFilter {

    private CameraFilter mFilter;
    private int width = 0;
    private int height = 0;

    //离屏的buffer?
    private int[] fFrame = new int[1];
    private int[] fTexture = new int[1];
    //相机的Render中，创建的textureId 代表oes纹理的name
    private int[] mCameraTexture = new int[1];
    //创建的SurfaceTextureView 传递给相机。来作为输入口
    private SurfaceTexture mSurfaceTexture;
    //坐标系
    private float[] mCoordOM = new float[16];

    //获取Track数据
    private ByteBuffer tBuffer;

    public TextureFilter(Resources mRes) {
        super(mRes);
        mFilter = new CameraFilter(mRes);
    }

    public void setCoordMatrix(float[] matrix) {
        mFilter.setCoordMatrix(matrix);
    }

    //通过这里get方法，和外面相机的setPreview方法联通。作为输入口
    public SurfaceTexture getTexture() {
        return mSurfaceTexture;
    }

    @Override
    public void setFlag(int flag) {
        mFilter.setFlag(flag);
    }

    //本身不需要绘制
    @Override
    protected void initBuffer() {

    }

    @Override
    public void setMatrix(float[] matrix) {
        mFilter.setMatrix(matrix);
    }

    //得到向外输出的textureName
    //数据从相机输入到cameraTextureId,然后我们再输入到frameBuffer的textureid
    @Override
    public int getOutputTexture() {
        return fTexture[0];
    }

    //绘制。就是添加特效的过程。
    //这里需要先调用SurfaceView updateImage方法。因为是接受相机的回调。

    @Override
    public void draw() {
        //关掉深度检查？
        boolean a = GLES20.glIsEnabled(GLES20.GL_DEPTH_TEST);
        if (a) {
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        }
        //告诉surfaceTextureView更新了。
        if (mSurfaceTexture != null) {
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mCoordOM);
            mFilter.setCoordMatrix(mCoordOM);
        }

        //使用Color Attachment的方式，将绑定的FrameBuffer 绑定到fTextureId上
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, fTexture[0], 0);
        //设置视图的大小？
        GLES20.glViewport(0, 0, width, height);
        mFilter.setTextureId(mCameraTexture[0]);
        //调用Camera的Filter 进行draw就是想相机得到的数据先画出来
        //因为绑定了FrameBuffer,所有数据就到frameBuffer上来？这样我们就可以根据ftexture[0]拿到这部分的数据了？

        mFilter.draw();
        Log.e("wuwang", "textureFilter draw");
        //调用完了，就解绑FrameBuffer的绑定
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);

        //恢复深度检测。不影响下一个路径。
        if (a) {
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }
    }

    @Override
    protected void onCreate() {
        mFilter.create();
        createOesTexture();
        mSurfaceTexture = new SurfaceTexture(mCameraTexture[0]);
    }

    @Override
    protected void onSizeChanged(int width, int height) {
        mFilter.setSize(width, height);
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
            //创建FrameBuffer和Texture
            deleteFrameBuffer();
            GLES20.glGenFramebuffers(1, fFrame, 0);
            EasyGlUtils.genTexturesWithParameter(1, fTexture, 0, GLES20.GL_RGBA, width, height);
        }
    }

    private void deleteFrameBuffer() {
        GLES20.glDeleteFramebuffers(1, fFrame, 0);
        GLES20.glDeleteTextures(1, fTexture, 0);
    }

    private void createOesTexture() {
        GLES20.glGenTextures(1, mCameraTexture, 0);
    }

}
