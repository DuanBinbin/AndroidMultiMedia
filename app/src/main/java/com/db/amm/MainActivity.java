package com.db.amm;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.db.amm.permission.PermissionHolder;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions();

        findViewById(R.id.btn_audio_record).setOnClickListener(this);
        findViewById(R.id.btn_audio_action_play_audioTrack).setOnClickListener(this);
        findViewById(R.id.btn_audio_action_play_audioTrack_left).setOnClickListener(this);
        findViewById(R.id.btn_audio_action_play_audioTrack_right).setOnClickListener(this);
        findViewById(R.id.btn_audio_action_play_MediaPlayer).setOnClickListener(this);
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

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_audio_record:
                //录音
                break;

            case R.id.btn_audio_action_play_audioTrack:
                //AudioTrack播放
                break;

            case R.id.btn_audio_action_play_audioTrack_left:
                //AudioTrack左声道播放
                break;

            case R.id.btn_audio_action_play_audioTrack_right:
                //AudioTrack右声道播放
                break;

            case R.id.btn_audio_action_play_MediaPlayer:
                //MediaPlayer播放
                break;
        }
    }
}
