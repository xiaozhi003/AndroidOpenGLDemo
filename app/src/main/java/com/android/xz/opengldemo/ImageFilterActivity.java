package com.android.xz.opengldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.android.xz.opengldemo.gles.draw.filter.GrayFilter;
import com.android.xz.opengldemo.gles.draw.filter.HueFilter;
import com.android.xz.opengldemo.gles.draw.filter.InvertFilter;
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
                case R.id.originRadioBtn: // 原图
                    glSurfaceView.setImageFilter(new OriginFilter());
                    break;
                case R.id.grayRadioBtn: // 灰度
                    glSurfaceView.setImageFilter(new GrayFilter());
                    break;
                case R.id.invertRadioBtn: // 反相
                    glSurfaceView.setImageFilter(new InvertFilter());
                    break;
                case R.id.brightenRadioBtn: // 提亮
                    glSurfaceView.setImageFilter(new HueFilter(new float[]{0.1f, 0.1f, 0.1f}));
                    break;
                case R.id.darkenRadioBtn: // 变暗
                    glSurfaceView.setImageFilter(new HueFilter(new float[]{-0.1f, -0.1f, -0.1f}));
                    break;
                case R.id.warmRadioBtn: // 暖色
                    glSurfaceView.setImageFilter(new HueFilter(new float[]{0.1f, 0.1f, 0.0f}));
                    break;
                case R.id.coolRadioBtn: // 冷色
                    glSurfaceView.setImageFilter(new HueFilter(new float[]{0, 0, 0.1f}));
                    break;
                default:
                    break;
            }
        });
    }
}