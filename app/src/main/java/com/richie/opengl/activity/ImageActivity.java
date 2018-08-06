package com.richie.opengl.activity;

import android.os.Bundle;

import com.richie.opengl.R;
import com.richie.opengl.view.image.ImageGLView;

public class ImageActivity extends DetailActivity {

    private ImageGLView mImageGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        mImageGLView = findViewById(R.id.image_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImageGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageGLView.onPause();
    }
}
