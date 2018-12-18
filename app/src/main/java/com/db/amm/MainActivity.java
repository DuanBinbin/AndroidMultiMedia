package com.db.amm;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.db.amm.demo2.AudioRecorderUtil;
import com.db.amm.utils.AudioSplitter;
import com.db.amm.utils.FileUtils;
import com.db.amm.utils.PermissionUtils;
import com.db.amm.utils.ResourceUtil;
import com.db.amm.utils.ToastUtils;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_audio_record).setOnClickListener(this);
        findViewById(R.id.btn_audio_action_play_audioTrack).setOnClickListener(this);
        findViewById(R.id.btn_audio_action_play_audioTrack_left).setOnClickListener(this);
        findViewById(R.id.btn_audio_action_play_audioTrack_right).setOnClickListener(this);
        findViewById(R.id.btn_audio_action_play_MediaPlayer).setOnClickListener(this);

        //申请权限
        PermissionUtils.checkPermission(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int temp : grantResults) {
            if (temp == PERMISSION_DENIED) {
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("申请权限").setMessage("这些权限很重要").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ToastUtils.show(MainActivity.this, "取消");
                    }
                }).setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                        MainActivity.this.startActivity(intent);
                    }
                }).create();
                dialog.show();
                break;
            }
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        final int id = v.getId();
        Button btn = (Button) v;
        switch (id){
            case R.id.btn_audio_record:
                if (TextUtils.equals(btn.getText(),ResourceUtil.getString(R.string.audio_action_record)))
                {
                    //录音
                    btn.setText(ResourceUtil.getString(R.string.audio_action_stop));
                    AudioRecorderUtil.getInstance().startRecord();
                } else {
                    btn.setText(ResourceUtil.getString(R.string.audio_action_record));
                    AudioRecorderUtil.getInstance().stopRecord();
                }
                break;

            case R.id.btn_audio_action_play_audioTrack:
                //AudioTrack播放
                AudioRecorderUtil.getInstance().playWithAudioTrack();
                break;

            case R.id.btn_audio_action_play_audioTrack_left:
                //AudioTrack左声道播放
                playWithAudioTrackLeft();
                break;

            case R.id.btn_audio_action_play_audioTrack_right:
                //AudioTrack右声道播放
                playWithAudioTrackRight();
                break;

            case R.id.btn_audio_action_play_MediaPlayer:
                //MediaPlayer播放
                AudioRecorderUtil.getInstance().playWithMediaPlayer();
                break;
        }
    }

    private void playWithAudioTrackLeft(){
        try {
            AudioSplitter.splitStereoPcm(FileUtils.fileToBytes(AudioRecorderUtil.getInstance().getPCMFile()));
            AudioRecorderUtil.getInstance().playWithAudioTrack(AudioSplitter.getLeftData());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void playWithAudioTrackRight(){
        try{
            AudioSplitter.splitStereoPcm(FileUtils.fileToBytes(AudioRecorderUtil.getInstance().getPCMFile()));
            AudioRecorderUtil.getInstance().playWithAudioTrack(AudioSplitter.getRightData());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
