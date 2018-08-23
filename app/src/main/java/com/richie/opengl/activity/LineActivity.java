package com.richie.opengl.activity;

import android.os.Bundle;

import com.richie.opengl.R;
import com.richie.opengl.view.line.LineView;

public class LineActivity extends DetailActivity {

    private LineView mLineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);
        mLineView = findViewById(R.id.line_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLineView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLineView.onPause();
    }
}
