package com.android.xz.opengldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.xz.opengldemo.gles.draw.filter.BlendBlurFilter;
import com.android.xz.opengldemo.gles.draw.filter.BlurFilter;
import com.android.xz.opengldemo.view.ImageBlurGLSurfaceView;

import org.w3c.dom.Text;

public class ImageBlurActivity extends AppCompatActivity {

    private static final String TAG = ImageBlurActivity.class.getSimpleName();
    private FrameLayout mContentLayout;
    private RadioGroup mFilterRadioGroup;
    private SeekBar mRadiusSeekBar;
    private TextView mSeekTv;
    private TextView mInfoTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_blur);


        mContentLayout = findViewById(R.id.contentLayout);
        mRadiusSeekBar = findViewById(R.id.radiusSeekBar);
        mSeekTv = findViewById(R.id.seekTv);
        mInfoTv = findViewById(R.id.infoTv);

        ImageBlurGLSurfaceView glSurfaceView = new ImageBlurGLSurfaceView(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        mContentLayout.addView(glSurfaceView);

        glSurfaceView.setDrawListener(millis -> runOnUiThread(() -> mInfoTv.setText("耗时：" + millis)));

        mFilterRadioGroup = findViewById(R.id.filterRadioGroup);
        mFilterRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Log.i(TAG, "OnCheckedChange...");
            switch (checkedId) {
                case R.id.horizontalBlurRadioBtn:
                    glSurfaceView.setImageFilter(new BlurFilter(BlurFilter.HORIZONTAL_BLUR_SHADER));
                    break;
                case R.id.verticalBlurRadioBtn:
                    glSurfaceView.setImageFilter(new BlurFilter(BlurFilter.VERTICAL_BLUR_SHADER));
                    break;
                case R.id.blendBlurRadioBtn:
                    glSurfaceView.setImageFilter(new BlendBlurFilter());
                    break;
                default:
                    break;
            }
            mRadiusSeekBar.setProgress(5);
        });

        mRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSeekTv.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int radius = seekBar.getProgress();
                glSurfaceView.setBlurRadius(radius);
                mSeekTv.setText("" + radius);
            }
        });
        mRadiusSeekBar.setProgress(5);
        mSeekTv.setText("" + 5);
    }
}