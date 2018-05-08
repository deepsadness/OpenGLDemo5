package com.cry.opengldemo5.data;

import android.opengl.GLES20;

import com.cry.opengldemo5.common.Constant;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * DESCRIPTION: 代表顶点的内存对象的抽象类
 * Author: Cry
 * DATE: 2018/5/9 上午12:52
 */
public class VertexArray {

    private final FloatBuffer mFloatBuffer;

    public VertexArray(float[] vertexData) {
        //得到内存的
        mFloatBuffer = ByteBuffer
                .allocateDirect(vertexData.length * Constant.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
    }

    public void setVertexAttributePointer(
            int dataOffset,
            int attributePointer,
            int size,
            int stride
    ) {
        mFloatBuffer.position(dataOffset);
        GLES20.glEnableVertexAttribArray(attributePointer);
        GLES20.glVertexAttribPointer(
                attributePointer,
                size,
                GLES20.GL_FLOAT,
                false,
                stride,
                mFloatBuffer
                );
    }
}
