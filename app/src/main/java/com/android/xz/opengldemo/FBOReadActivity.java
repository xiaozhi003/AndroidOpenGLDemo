package com.android.xz.opengldemo;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.xz.opengldemo.view.FBOReadGLSurfaceView;

import java.io.File;

public class FBOReadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbo_read_activity);

        LinearLayout layout = findViewById(R.id.contentLayout);
        layout.addView(new FBOReadGLSurfaceView(this));

        TextView infoTv = findViewById(R.id.infoTv);
        infoTv.setText("灰度图存储路径：" + getExternalFilesDir("gles").getAbsolutePath() + File.separator + "gray.jpg");
    }
}