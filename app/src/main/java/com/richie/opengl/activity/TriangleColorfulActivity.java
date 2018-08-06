package com.richie.opengl.activity;

import android.os.Bundle;

import com.richie.opengl.R;
import com.richie.opengl.view.triangle.TriangleColorfulView;

public class TriangleColorfulActivity extends DetailActivity {

    private TriangleColorfulView mTriangleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triangle_colorful);
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
