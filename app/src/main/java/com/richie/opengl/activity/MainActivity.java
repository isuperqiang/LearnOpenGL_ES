package com.richie.opengl.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.richie.easylog.ILogger;
import com.richie.easylog.LoggerFactory;
import com.richie.opengl.R;
import com.richie.opengl.util.GLESUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final ILogger logger = LoggerFactory.getLogger(MainActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean supportGL20 = GLESUtils.isSupportGL20(this);
        if (!supportGL20) {
            Toast.makeText(this, "不支持 GLES 2.0", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.btn_triangle).setOnClickListener(this);
        findViewById(R.id.btn_square).setOnClickListener(this);
        findViewById(R.id.btn_graph).setOnClickListener(this);
        findViewById(R.id.btn_camera1).setOnClickListener(this);
        findViewById(R.id.btn_camera2).setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // ok
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent;
        if (id == R.id.btn_triangle) {
            intent = new Intent(this, TriangleActivity.class);
        } else if (id == R.id.btn_square) {
            intent = new Intent(this, SquareActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        //} else if (id == R.id.btn_triangle) {
        //    Intent intent = new Intent(this, TriangleActivity.class);
        startActivity(intent);
        //} else if (id == R.id.btn_camera1) {
        //    Intent intent = new Intent(this, CameraActivity.class);
        //    startActivity(intent);
        //} else if (id == R.id.btn_camera2) {
        //    Intent intent = new Intent(this, Camera2Activity.class);
        //    startActivity(intent);
        //startActivity(intent);
    }
}
