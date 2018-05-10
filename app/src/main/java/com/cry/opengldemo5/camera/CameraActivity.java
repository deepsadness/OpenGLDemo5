package com.cry.opengldemo5.camera;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.cry.opengldemo5.R;
import com.cry.opengldemo5.camera.core.ICamera;
import com.cry.opengldemo5.camera.gles.CameraView;

import java.nio.ByteBuffer;

/**
 * 将滤镜用于预览。但是简单的调用相机的拍照效果的话，得到的数据是不会有滤镜效果的。
 * <p>
 * 简单的可以通过在GLThread中 调用GL.glReadPixels方式得到显存
 */
public class CameraActivity extends BasePmActivity {


    public CameraView mCameraView;
    private ViewGroup mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);
        mContainer = (ViewGroup) findViewById(R.id.container);
        findViewById(R.id.btn_take).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraView != null) {
                    mCameraView.takePhoto(new ICamera.TakePhotoCallback() {
                        @Override
                        public void onTakePhoto(byte[] bytes, int width, int height) {
                            //这里这个是从GL中读取现存
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ByteBuffer wrap = ByteBuffer.wrap(bytes);
                                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                                    bitmap.copyPixelsFromBuffer(wrap);
//                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    CameraActivity context = CameraActivity.this;
                                    ImageView imageView = new ImageView(context);
                                    imageView.setImageBitmap(bitmap);
                                    //因为读到的图上下翻转了。所以scale
                                    imageView.setScaleY(-1);
                                    new AlertDialog.Builder(context).setView(imageView).setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                                }
                            });

                        }
                    });
                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraView != null) {
            mCameraView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCameraView != null) {
            mCameraView.onResume();
        }
    }

    @Override
    public void initCamera() {
        if (mCameraView == null) {
            mCameraView = new CameraView(this);
            mContainer.addView(mCameraView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }


}
