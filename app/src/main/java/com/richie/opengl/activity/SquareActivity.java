package com.richie.opengl.activity;

import android.os.Bundle;

import com.richie.opengl.R;
import com.richie.opengl.view.square.SquareView;

public class SquareActivity extends DetailActivity {

    private SquareView mSquareView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square);
        mSquareView = findViewById(R.id.square_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSquareView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSquareView.onPause();
    }
}
