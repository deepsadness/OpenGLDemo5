package com.cry.opengldemo5.camera.gles;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.cry.opengldemo5.camera.Camera2Activity;
import com.cry.opengldemo5.camera.core.ISize;
import com.cry.opengldemo5.camera.gles.filter.AFilter;
import com.cry.opengldemo5.camera.gles.filter.GroupFilter;
import com.cry.opengldemo5.camera.gles.filter.NoFilter;
import com.cry.opengldemo5.camera.gles.filter.TextureFilter;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

/**
 * Texture filter controller.
 * 将自己的GLView的生命周期监听到。分发给其他的render
 * <p>
 * Created by a2957 on 2018/5/10.
 */
public class TextureController implements GLSurfaceView.Renderer {

    private final Context mContext;
    private GLView mGLView;
    private ISize previewSize;
    private ISize picSize;

    //    private Renderer mRenderer;
    //主要就是下面这个三个Render了.再仔细来看一下这三个Render
    private TextureFilter mEffectFilter;                        //特效处理的Filter
    private GroupFilter mGroupFilter;                           //中间特效
    private AFilter mShowFilter;                                //用来渲染输出的Filter
    private Object mNativeWindowSurface;
    private Camera2Activity.Camera1Render mRender;                //用户附加的Renderer或用来监听Renderer

    //离屏的buffer.最后用来导出数据
    private int[] mExportFrame = new int[1];
    private int[] mExportTexture = new int[1];

    private ByteBuffer[] outputBuffer = new ByteBuffer[3]; //用于存储回调数据的buffer

    private int indexOutput = 0; //回调数据的buffer
    private AtomicBoolean isParamSet = new AtomicBoolean(false);

    //matrix
    private float[] SM=new float[16];                           //用于绘制到屏幕上的变换矩阵
    private float[] callbackOM=new float[16];                   //用于绘制回调缩放的矩阵


    public TextureController(Context context) {
        this.mContext = context;
        init(context);
    }

    public void surfaceCreated(Object nativeWindow) {
        this.mNativeWindowSurface = nativeWindow;
        //调用GLView的生命周期方法？
        mGLView.surfaceCreated(null);
    }

    public void surfaceChanged(int width, int height) {
        this.previewSize = new ISize(width, height);
        mGLView.surfaceChanged(null, 0, width, height);
    }

    public void surfaceDestroyed() {
        mGLView.surfaceDestroyed(null);
    }

    private void init(Context context) {
        mGLView = new GLView(context);

        //为了避免GLView的attachToWindow和detachFromWindow奔溃？？
        ViewGroup v = new ViewGroup(context) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {

            }
        };
        v.addView(mGLView);
        v.setVisibility(View.GONE);
        //开始设置滤镜。因为经过多次滤镜。所以我们需要创建多个滤镜
        mEffectFilter = new TextureFilter(mContext.getResources());
        mShowFilter = new NoFilter(mContext.getResources());
        mGroupFilter = new GroupFilter(mContext.getResources());

        //设置Camera中提供的previewSize 和实际的picSize

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //创建各种Render
        mEffectFilter.create();
        mGroupFilter.create();
        mShowFilter.create();
        if (!isParamSet.get()) {
            if (mRender != null) {
                mRender.onSurfaceCreated(gl, config);
                picSize = mRender.mPictureSize;
            }
            sdkParamSet();
        }
        calculateCallbackOM();

        //先deleteFrameBuffer。然后重新创建
        deleteFrameBuffer();
        //重新创建
        GLES20.glGenFramebuffers(1, mExportFrame, 0);
        GLES20.glGenTextures(1, mExportTexture, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mExportTexture[0]);
        GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D, 0,
                GLES20.GL_RGBA,
                picSize.getWidth(), picSize.getHeight(), 0,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE, null);

        //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

    }

    /*
    删除的是FrameBuffer和Texture的纹理
     */
    private void deleteFrameBuffer() {
        GLES20.glDeleteFramebuffers(1, mExportFrame, 0);
        GLES20.glDeleteTextures(1, mExportTexture, 0);
    }

    private void sdkParamSet() {
        if (!isParamSet.get() && picSize.getWidth() > 0 && picSize.getHeight() > 0) {
            isParamSet.set(true);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mShowFilter.setSize(width, height);
        mShowFilter.setMatrix(SM);
        mGroupFilter.setSize(picSize.getWidth(),picSize.getHeight());
        mEffectFilter.setSize(picSize.getWidth(),picSize.getHeight());
        mShowFilter.setSize(picSize.getWidth(),picSize.getHeight());
        if(mRender!=null){
            mRender.onSurfaceChanged(gl, width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if(isParamSet.get()){
            mEffectFilter.draw();
            mGroupFilter.setTextureId(mEffectFilter.getOutputTexture());
            mGroupFilter.draw();

            //显示传入的texture上，一般是显示在屏幕上
            GLES20.glViewport(0,0,previewSize.getWidth(),previewSize.getHeight());
            mShowFilter.setMatrix(SM);
            mShowFilter.setTextureId(mGroupFilter.getOutputTexture());
            mShowFilter.draw();
            if(mRender!=null){
                mRender.onDrawFrame(gl);
            }

//            callbackIfNeeded();
        }
    }
    private void calculateCallbackOM(){
//        if(frameCallbackHeight>0&&frameCallbackWidth>0&&picSize.getWidth()>0&&picSize.getHeight()>0){
//            //计算输出的变换矩阵
//            MatrixUtils.getMatrix(callbackOM,MatrixUtils.TYPE_CENTERCROP,picSize.getWidth(), picSize.getHeight(),
//                    frameCallbackWidth,
//                    frameCallbackHeight);
//            MatrixUtils.flip(callbackOM,false,true);
//        }
    }
    public void setPreviewSize(ISize previewSize) {
        this.previewSize = previewSize;
    }

    public void setPicSize(ISize picSize) {
        this.picSize = picSize;
    }

    public SurfaceTexture getTexture() {
        return mEffectFilter.getTexture();
    }

    public void requestRender() {
        mGLView.requestRender();
    }

    /**
     * 增加滤镜
     * @param filter 滤镜
     */
    public void addFilter(AFilter filter){
        mGroupFilter.addFilter(filter);
    }


    public void setRender(Camera2Activity.Camera1Render render) {
        mRender = render;
        //这个时候不能设置，因为都还没有走到SurfaceCreated方法中
//        picSize = mRender.mPictureSize;
    }

    public void onResume() {
        if (mGLView != null) {
            mGLView.onResume();
        }
    }

    public void onPause() {
        if (mGLView != null) {
            mGLView.onPause();
        }
    }

    public void destroy() {
        if (mRender != null) {
            mRender.onDestroy();
        }
        mGLView.surfaceDestroyed(null);
        mGLView.detachedFromWindow();
    }

    //这里还是使用GLSurfaceView中提供了的EGL环境

    /**
     * 自定义GLSurfaceView，暴露出onAttachedToWindow
     * 方法及onDetachedFromWindow方法，取消holder的默认监听
     * onAttachedToWindow及onDetachedFromWindow必须保证view
     * 存在Parent
     */
    private class GLView extends GLSurfaceView {

        public GLView(Context context) {
            super(context);
            initEGL(context);
        }

        private void initEGL(Context context) {
            //为什么要将callBack设置成null?又不能取消原来的
            getHolder().addCallback(null);
            //这里要将这个Surface改成我们自己外面定义的Surface.其实单独的EGL环境中，提供的windowSurface
            setEGLWindowSurfaceFactory(new EGLWindowSurfaceFactory() {
                @Override
                public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config, Object nativeWindow) {
                    //在这里传入了我们的surface
                    return egl.eglCreateWindowSurface(display, config, mNativeWindowSurface, null);
                }

                @Override
                public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
                    egl.eglDestroySurface(display, surface);
                }
            });
            setEGLContextClientVersion(2);
            //将外面的这个Render设置成它的render,后续的生命周期方法会转发给它
            setRenderer(TextureController.this);
            setRenderMode(RENDERMODE_WHEN_DIRTY);
            //默认是设置成false.这里设置成true,表示的在GLSurface OnPause时，还保存这个Context,设置成false,就是释放
            setPreserveEGLContextOnPause(true);
        }

        public void attachedToWindow() {
            super.onAttachedToWindow();
        }

        public void detachedFromWindow() {
            super.onDetachedFromWindow();
        }

    }
}
