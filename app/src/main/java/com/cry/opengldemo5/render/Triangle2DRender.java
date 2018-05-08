package com.cry.opengldemo5.render;

import android.content.Context;

import com.cry.opengldemo5.data.VertexArray;
import com.cry.opengldemo5.program.TriangleShaderProgram;
import com.cry.opengldemo5.shape.plane.Triangle2DShape;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * DESCRIPTION:
 * Author: Cry
 * DATE: 2018/5/9 上午1:04
 */

public class Triangle2DRender extends BaseGLRender {
    private final Context context;
    public TriangleShaderProgram mProgram;
    public float[] mGenerateVertex;
    public VertexArray mVertexArray;

    public Triangle2DRender(Context context) {
        this.context = context;

        //创建三角形
        mGenerateVertex = Triangle2DShape.generateVertex();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        //创建Program
        mProgram = new TriangleShaderProgram(context);
        mVertexArray = new VertexArray(mGenerateVertex);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        mProgram.userProgram();
        mVertexArray.setVertexAttributePointer(
                0,
                mProgram.getAPosition(),
                Triangle2DShape.getPositionOffset(),
                Triangle2DShape.getStride()
        );
        mProgram.setUniforms(Triangle2DShape.getColorUniform());

        //绘制
        Triangle2DShape.draw();

    }
}
