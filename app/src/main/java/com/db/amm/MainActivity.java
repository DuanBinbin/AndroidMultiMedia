package com.db.amm;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.cokus.wavelibrary.draw.WaveCanvas;
import com.cokus.wavelibrary.utils.SamplePlayer;
import com.cokus.wavelibrary.utils.SoundFile;
import com.cokus.wavelibrary.view.WaveSurfaceView;
import com.cokus.wavelibrary.view.WaveformView;
import com.db.amm.base.BaseActivity;
import com.db.amm.base.BaseApplication;
import com.db.amm.demo2.AudioRecorderUtil;
import com.db.amm.log.LogHelper;
import com.db.amm.utils.AudioSplitter;
import com.db.amm.utils.FileUtils;
import com.db.amm.utils.MusicSimilarityUtil;
import com.db.amm.utils.PermissionUtils;
import com.db.amm.utils.ResourceUtil;
import com.db.amm.utils.ToastUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import butterknife.BindView;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;

public class MainActivity extends BaseActivity {

    private final static String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.waveview)WaveformView waveView;
    @BindView(R.id.wavesfv)WaveSurfaceView waveSfv;

    private WaveCanvas waveCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_audio_record).setOnClickListener(this);
        findViewById(R.id.btn_audio_action_play_audioTrack).setOnClickListener(this);
        findViewById(R.id.btn_audio_action_play_audioTrack_left).setOnClickListener(this);
        findViewById(R.id.btn_audio_action_play_audioTrack_right).setOnClickListener(this);
        findViewById(R.id.btn_audio_action_play_MediaPlayer).setOnClickListener(this);

        findViewById(R.id.btn_compare_mono_channel).setOnClickListener(this);
        findViewById(R.id.btn_audio_action_stop_audioTrack_left).setOnClickListener(this);
        findViewById(R.id.btn_audio_action_stop_audioTrack_right).setOnClickListener(this);
        findViewById(R.id.btn_audio_action_restore).setOnClickListener(this);

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
                    //开始录音
                    btn.setText(ResourceUtil.getString(R.string.audio_action_stop));
                    AudioRecorderUtil.getInstance().startRecord();
                } else {
                    //结束录音
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

            case R.id.btn_compare_mono_channel:
                getScoreByCompareMonoFile();
                break;

            case R.id.btn_audio_action_stop_audioTrack_left:
                AudioRecorderUtil.getInstance().setChannel(false,true);
                break;

            case R.id.btn_audio_action_stop_audioTrack_right:
                AudioRecorderUtil.getInstance().setChannel(true,false);
                break;

            case R.id.btn_audio_action_restore:
                AudioRecorderUtil.getInstance().setChannel(true,true);
                break;

                default:
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

    private boolean mLoadingKeepGoing;
    private Thread mLoadSoundFileThread;
    private SoundFile mSoundFile;
    private SamplePlayer mPlayer;

    /**
     * 载入wav文件显示波形
     */
    private void loadFromFile(File loadFile) {
        final File mFile = loadFile;
        try {
            Thread.sleep(300);//让文件写入完成后再载入波形 适当的休眠下
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mLoadingKeepGoing = true;
        // Load the sound file in a background thread
        mLoadSoundFileThread = new Thread() {
            public void run() {
                try {
                    mSoundFile = SoundFile.create(mFile.getAbsolutePath(),null);
                    if (mSoundFile == null) {
                        return;
                    }
                    mPlayer = new SamplePlayer(mSoundFile);
                } catch (final Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (mLoadingKeepGoing) {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            finishOpeningSoundFile();
                            waveSfv.setVisibility(View.INVISIBLE);
                            waveView.setVisibility(View.VISIBLE);
                        }
                    };
                    MainActivity.this.runOnUiThread(runnable);
                }
            }
        };
        mLoadSoundFileThread.start();
    }

    private float mDensity;

    /**waveview载入波形完成*/
    private void finishOpeningSoundFile() {
        waveView.setSoundFile(mSoundFile);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDensity = metrics.density;
        waveView.recomputeHeights(mDensity);
    }

    private void getScoreByCompareMonoFile(){
        try {
            //split stereo audio file
            AudioSplitter.splitStereoPcm(FileUtils.fileToBytes(AudioRecorderUtil.getInstance().getPCMFile()));

            //get mono channel file data
            final byte[] leftByte = AudioSplitter.getLeftData();
            final byte[] rightByte = AudioSplitter.getRightData();
            InputStream leftBis = new ByteArrayInputStream(leftByte);
            InputStream rightBis = new ByteArrayInputStream(rightByte);

            // compare
            final float score = MusicSimilarityUtil.getScoreByCompareFile(leftBis,rightBis);
            ToastUtils.show(BaseApplication.getContext(),score + "");

            LogHelper.s("getScoreByCompareMonoFile() --> score = " + score);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
