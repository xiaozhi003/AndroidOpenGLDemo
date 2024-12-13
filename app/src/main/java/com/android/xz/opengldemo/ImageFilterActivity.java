package com.android.xz.opengldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.android.xz.opengldemo.gles.draw.filter.BlendBlurFilter;
import com.android.xz.opengldemo.gles.draw.filter.BlurFilter;
import com.android.xz.opengldemo.gles.draw.filter.GrayFilter;
import com.android.xz.opengldemo.gles.draw.filter.OriginFilter;
import com.android.xz.opengldemo.view.ImageFilterGLSurfaceView;

public class ImageFilterActivity extends AppCompatActivity {

    private FrameLayout mContentLayout;
    private RadioGroup mFilterRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_filter);

        mContentLayout = findViewById(R.id.contentLayout);
        ImageFilterGLSurfaceView glSurfaceView = new ImageFilterGLSurfaceView(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        mContentLayout.addView(glSurfaceView);

        mFilterRadioGroup = findViewById(R.id.filterRadioGroup);
        mFilterRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.originRadioBtn:
                    glSurfaceView.setImageFilter(new OriginFilter());
                    break;
                case R.id.grayRadioBtn:
                    glSurfaceView.setImageFilter(new GrayFilter());
                    break;
                case R.id.horizontalBlurRadioBtn:
                    glSurfaceView.setImageFilter(new BlurFilter());
                    break;
                case R.id.blendBlurRadioBtn:
                    glSurfaceView.setImageFilter(new BlendBlurFilter());
                    break;
                default:
                    break;
            }
        });
    }
}