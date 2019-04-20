package com.richie.opengl.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.richie.opengl.R;
import com.richie.opengl.util.GLESUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean supportGL20 = GLESUtils.isSupportGL20(this);
        if (!supportGL20) {
            Toast.makeText(this, "不支持 GLES 2.0", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.btn_triangle).setOnClickListener(this);
        findViewById(R.id.btn_triangle_color).setOnClickListener(this);
        findViewById(R.id.btn_square).setOnClickListener(this);
        findViewById(R.id.btn_graph).setOnClickListener(this);
        findViewById(R.id.btn_line).setOnClickListener(this);
        findViewById(R.id.btn_camera).setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent = null;
        if (id == R.id.btn_triangle) {
            intent = new Intent(this, TriangleActivity.class);
        } else if (id == R.id.btn_triangle_color) {
            intent = new Intent(this, TriangleColorfulActivity.class);
        } else if (id == R.id.btn_square) {
            intent = new Intent(this, SquareActivity.class);
        } else if (id == R.id.btn_graph) {
            intent = new Intent(this, ImageActivity.class);
        } else if (id == R.id.btn_line) {
            intent = new Intent(this, LineActivity.class);
        } else if (id == R.id.btn_camera) {
            intent = new Intent(this, CameraActivity.class);
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}
