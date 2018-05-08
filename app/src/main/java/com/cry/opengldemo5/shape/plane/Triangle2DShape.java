package com.cry.opengldemo5.shape.plane;

import android.opengl.GLES20;

import com.cry.opengldemo5.common.Constant;

/**
 * DESCRIPTION:
 * Author: Cry
 * DATE: 2018/5/9 上午1:01
 */

public class Triangle2DShape {
    private static float TRIANGLE_COORDS[] = {
            //Order of coordinates: X, Y, Z
            0.5f, 0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f   // bottom right
    };
    //在数组中，一个顶点需要3个来描述其位置，需要3个偏移量
    private static final int COORDS_PER_VERTEX = 3;
    private static final int COORDS_PER_COLOR = 0;

    //在数组中，描述一个顶点，总共的顶点需要的偏移量。这里因为只有位置顶点，所以和上面的值一样
    private static final int TOTAL_COMPONENT_COUNT = COORDS_PER_VERTEX + COORDS_PER_COLOR;
    //一个点需要的byte偏移量。
    private static final int STRIDE = TOTAL_COMPONENT_COUNT * Constant.BYTES_PER_FLOAT;
    //设置颜色，依次为红绿蓝和透明通道。
    //因为颜色是常量，所以用单独的数据表示？
    private static float TRIANGLE_COLOR[] = {1.0f, 1.0f, 1.0f, 1.0f};

    public static float[] generateVertex() {
        return TRIANGLE_COORDS;
    }

    public static int getSize() {
        return TRIANGLE_COORDS.length / TOTAL_COMPONENT_COUNT;
    }

    public static int getPositionOffset() {
        return COORDS_PER_VERTEX;
    }

    public static int getStride() {
        return STRIDE;
    }

    public static float[] getColorUniform() {
        return TRIANGLE_COLOR;
    }

    public static void draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,getSize());
    }
}
