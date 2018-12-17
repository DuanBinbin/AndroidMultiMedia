package com.db.amm;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;

import com.db.amm.permission.PermissionHolder;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions();
    }

    /**
     * 申请权限
     */
    private void requestPermissions(){
        PermissionHolder.requestPermissions(this,
                PermissionHolder.getRequestCode(),
                getRequestPermissions(),
                new PermissionHolder.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                    }

                    @Override
                    public void onPermissionDenied(String[] deniedPermissions) {

                    }
                });
    }

    /**
     * 获取请求权限
     *
     * @return
     */
    private final String[] getRequestPermissions(){
        return new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
    }
}
