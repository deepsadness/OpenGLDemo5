package com.cry.opengldemo5;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.cry.opengldemo5.render.GLESUtils;
import com.cry.opengldemo5.render.ViewActivity;

import java.io.InputStream;

/**
 * 0.简单的创建一个Open GL View
 * 1.如何绘制一个形状
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.btn_view).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewActivity.class);
            startActivity(intent);
        });

    }
}
