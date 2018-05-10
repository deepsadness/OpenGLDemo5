package com.cry.opengldemo5.camera.gles;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;

import com.cry.opengldemo5.camera.core.ISize;
import com.cry.opengldemo5.camera.gles.filter.AFilter;
import com.cry.opengldemo5.camera.gles.filter.GroupFilter;
import com.cry.opengldemo5.camera.gles.filter.TextureFilter;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

/**
 * Texture filter controller.
 * <p>
 * Created by a2957 on 2018/5/10.
 */
public class TextureController implements GLSurfaceView.Renderer {

    private final Context mContext;
    private GLView mGLView;
    private ISize previewSize;
    private ISize picSize;

    //    private Renderer mRenderer;                                 //用户附加的Renderer或用来监听Renderer
    private TextureFilter mEffectFilter;                        //特效处理的Filter
    private GroupFilter mGroupFilter;                           //中间特效
    private AFilter mShowFilter;

    public TextureController(Context context) {
        this.mContext = context;
        init(context);
    }

    private void init(Context context) {
        mGLView = new GLView(context);

        //为了避免GLView的attachToWindow和detachFromWindow奔溃？？
//        ViewGroup v = new ViewGroup(context) {
//            @Override
//            protected void onLayout(boolean changed, int l, int t, int r, int b) {
//
//            }
//        };
//        v.addView(mGLView);
//        v.setVisibility(View.GONE);
        //开始设置滤镜。因为经过多次滤镜。所以我们需要创建多个滤镜

        //设置Camera中提供的previewSize 和实际的picSize

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

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

    //这里还是使用GLSurfaceView中提供了的EGL环境
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
                    return null;
                }

                @Override
                public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {

                }
            });
            setEGLContextClientVersion(2);
            //将外面的这个Render设置成它的render,后续的生命周期方法会转发给它
            setRenderer(TextureController.this);
            setRenderMode(RENDERMODE_WHEN_DIRTY);
            //默认是设置成false.这里设置成true,表示的在GLSurface OnPause时，还保存这个Context,设置成false,就是释放
            setPreserveEGLContextOnPause(true);
        }
    }
}
