package com.cry.opengldemo5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;

import com.cry.opengldemo5.camera.CameraActivity;
import com.cry.opengldemo5.render.ViewActivity;

/**
 * 0.简单的创建一个Open GL View
 * 1.如何绘制一个形状
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        defaultDisplay.getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        System.out.println("widthPixels=" + widthPixels);
        System.out.println("heightPixels=" + heightPixels);

        findViewById(R.id.btn_view).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btn_camera).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            startActivity(intent);
        });

    }
}
