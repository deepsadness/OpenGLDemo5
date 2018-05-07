package com.cry.opengldemo5.render;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.cry.opengldemo5.shape.Ball3DShapeRender;
import com.cry.opengldemo5.shape.Cone3DShapeRender;

public class ViewActivity extends AppCompatActivity {

    /**
     * 是否已经设置过render
     */
    private boolean isRenderSet;
    /**
     * 当前显示的gl Surface
     */
    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_view);

        boolean isSupportEs2 = GLESUtils.isSupportEs2(this);

        //表示支持
        if (isSupportEs2) {
            //创建一个GLSurfaceView
            glSurfaceView = new GLSurfaceView(this);
            glSurfaceView.setEGLContextClientVersion(2);
            //设置自己的Render.Render 内进行图形的绘制
            glSurfaceView.setRenderer(new Cone3DShapeRender(this));
            isRenderSet = true;
            setContentView(glSurfaceView);
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0!!!", Toast.LENGTH_SHORT).show();
        }
    }

    //同时要注意生命周期的方法

    @Override
    protected void onPause() {
        super.onPause();
        if (isRenderSet) {
            glSurfaceView.onPause();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRenderSet) {
            glSurfaceView.onResume();
        }

    }
}
