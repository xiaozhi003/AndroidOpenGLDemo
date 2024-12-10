package com.android.xz.opengldemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.android.xz.opengldemo.permission.IPermissionsResult;
import com.android.xz.opengldemo.permission.PermissionUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mContext = this;

        Log.i(MainActivity.class.getSimpleName(), getOpenGLESVersion());

        findViewById(R.id.envBtn).setOnClickListener(this);
        findViewById(R.id.triangleBtn).setOnClickListener(this);
        findViewById(R.id.squareBtn).setOnClickListener(this);
        findViewById(R.id.circleBtn).setOnClickListener(this);
        findViewById(R.id.cameraBtn).setOnClickListener(this);
        findViewById(R.id.imageBtn).setOnClickListener(this);
    }

    private String getOpenGLESVersion() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
            return "OpenGL ES Version: " + configurationInfo.getGlEsVersion();
        }
        return "OpenGL ES Version: Unknown";
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cameraBtn) {
            PermissionUtils.getInstance().requestPermission(this, new String[]{Manifest.permission.CAMERA}, new IPermissionsResult() {
                @Override
                public void passPermissions() {
                    startActivity(new Intent(mContext, GLCameraActivity.class));
                }

                @Override
                public void forbidPermissions() {
                }
            });
        } else {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.envBtn:
                    intent = new Intent(mContext, GLActivity.class);
                    intent.putExtra(GLActivity.EXTRA_GL_VIEW_TYPE, GLActivity.TYPE_ENV);
                    break;
                case R.id.triangleBtn:
                    intent = new Intent(mContext, GLActivity.class);
                    intent.putExtra(GLActivity.EXTRA_GL_VIEW_TYPE, GLActivity.TYPE_TRIANGLE);
                    break;
                case R.id.squareBtn:
                    intent = new Intent(mContext, GLActivity.class);
                    intent.putExtra(GLActivity.EXTRA_GL_VIEW_TYPE, GLActivity.TYPE_SQUARE);
                    break;
                case R.id.circleBtn:
                    intent = new Intent(mContext, GLActivity.class);
                    intent.putExtra(GLActivity.EXTRA_GL_VIEW_TYPE, GLActivity.TYPE_CIRCLE);
                    break;
                case R.id.imageBtn:
                    intent = new Intent(mContext, GLActivity.class);
                    intent.putExtra(GLActivity.EXTRA_GL_VIEW_TYPE, GLActivity.TYPE_IMAGE);
                    break;
            }
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}