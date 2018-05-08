package com.cry.opengldemo5.program;

import android.content.Context;
import android.opengl.GLES20;

/**
 * DESCRIPTION: 最简单的平面三角形
 * Author: Cry
 * DATE: 2018/5/9 上午12:44
 */
public class TriangleShaderProgram extends BaseShaderProgram {
    private static final String VERTEX_SHADER_FILE = "shape/triangle_vertex_shader.glsl";
    private static final String FRAGMENT_SHADER_FILE = "shape/triangle_fragment_shader.glsl";
    private static final String A_POSITION = "a_Position";
    private static final String U_COLOR = "u_Color";
    private final int mAPosition;
    private final int mUColor;

    public TriangleShaderProgram(Context context) {
        super(context, VERTEX_SHADER_FILE, FRAGMENT_SHADER_FILE);
        //得到各种属性
        mAPosition = GLES20.glGetAttribLocation(mProgramObjectId, A_POSITION);
        mUColor = GLES20.glGetUniformLocation(mProgramObjectId, U_COLOR);
    }

    public void setUniforms(float[] color) {
        GLES20.glUniform1fv(mUColor, 1, color, 0);
    }

    public int getAPosition() {
        return mAPosition;
    }
}
