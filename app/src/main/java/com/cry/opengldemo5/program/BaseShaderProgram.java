package com.cry.opengldemo5.program;

import android.content.Context;
import android.opengl.GLES20;

import com.cry.opengldemo5.common.GLESUtils;

/**
 * DESCRIPTION: ShaderProgram的基础类
 * Author: Cry
 * DATE: 2018/5/9 上午12:37
 */
public abstract class BaseShaderProgram {
    protected final int mProgramObjectId;

    public BaseShaderProgram(Context context, String vertexShaderFile, String fragmentShaderFile) {
        String vertexShaderCode = GLESUtils.readAssetShaderCode(context, vertexShaderFile);
        String fragmentShaderCode = GLESUtils.readAssetShaderCode(context, fragmentShaderFile);
        //1.得到之后，进行编译。得到id
        int vertexShaderObjectId = GLESUtils.compileShaderCode(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShaderObjectId = GLESUtils.compileShaderCode(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        //3.继续套路。取得到program
        mProgramObjectId = GLES20.glCreateProgram();
        //将shaderId绑定到program当中
        GLES20.glAttachShader(mProgramObjectId, vertexShaderObjectId);
        GLES20.glAttachShader(mProgramObjectId, fragmentShaderObjectId);
        //4.最后，启动GL link program
        GLES20.glLinkProgram(mProgramObjectId);
    }

    public int getProgramObjectId() {
        return mProgramObjectId;
    }

    public boolean userProgram() {
        if (mProgramObjectId < 0) {
            return false;
        }
        int[] status = new int[1];
        GLES20.glGetProgramiv(mProgramObjectId, GLES20.GL_VALIDATE_STATUS, status, 0);

        if (status[0] == 0) {
            return false;
        } else {
            GLES20.glUseProgram(mProgramObjectId);
            return true;
        }
    }
}
