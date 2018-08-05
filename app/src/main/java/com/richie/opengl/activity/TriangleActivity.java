package com.richie.opengl.activity;

import android.os.Bundle;

import com.richie.opengl.R;
import com.richie.opengl.view.TriangleView;

public class TriangleActivity extends DetailActivity {

    private TriangleView mTriangleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triangle);
        mTriangleView = findViewById(R.id.triangle_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTriangleView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTriangleView.onPause();
    }
}
