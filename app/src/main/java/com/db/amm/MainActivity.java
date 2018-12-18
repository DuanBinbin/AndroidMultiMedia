package com.db.amm;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.db.amm.demo2.AudioRecorderUtil;
import com.db.amm.log.LogHelper;
import com.db.amm.permission.PermissionHolder;
import com.db.amm.utils.FileUtils;
import com.db.amm.utils.MediaUtils;
import com.db.amm.utils.PermissionUtils;
import com.db.amm.utils.ResourceUtil;
import com.db.amm.utils.ToastUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
     * 申请权限
     */
    private void requestPermissions(){
        PermissionHolder.requestPermissions(this,
                PermissionHolder.getRequestCode(),
                getRequestPermissions(),
                new PermissionHolder.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        LogHelper.d(TAG,"onPermissionGranted()");
                    }

                    @Override
                    public void onPermissionDenied(String[] deniedPermissions) {
                        LogHelper.e(TAG,"onPermissionDenied() --> " + deniedPermissions);
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
        final int id = v.getId();
        Button btn = (Button) v;
        switch (id){
            case R.id.btn_audio_record:
                if (TextUtils.equals(btn.getText(),ResourceUtil.getString(R.string.audio_action_record)))
                {
                    //录音
                    btn.setText(ResourceUtil.getString(R.string.audio_action_stop));
//                    startRecord();
                    AudioRecorderUtil.getInstance().startRecord();
                } else {
                    btn.setText(ResourceUtil.getString(R.string.audio_action_record));
//                    stopRecord();
                    AudioRecorderUtil.getInstance().stopRecord();
                }
                break;

            case R.id.btn_audio_action_play_audioTrack:
                //AudioTrack播放
//                playWithAudioTrack();
                AudioRecorderUtil.getInstance().playWithAudioTrack();
                break;

            case R.id.btn_audio_action_play_audioTrack_left:
                //AudioTrack左声道播放
                break;

            case R.id.btn_audio_action_play_audioTrack_right:
                //AudioTrack右声道播放
                break;

            case R.id.btn_audio_action_play_MediaPlayer:
                //MediaPlayer播放
//                playWithMediaPlayer();
                AudioRecorderUtil.getInstance().playWithMediaPlayer();
                break;
        }
    }

    /*****************AudioRecord使用的参数**************************/
    enum Status { //录音的状态
        RUNNING, //正在录音
        END //录音停止
    }

    private AudioRecord mAudioRecord;
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    private final static int AUDIO_SAMPLE_RATE = 44100;    // 采样率44100是目前的标准，但是某些设备仍然支持22050，16000，11025， 采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_STEREO;// 音频通道，双通道
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT; // 音频格式：PCM编码，16位，这里的值是编号，写入wav不要搞混了
    private int mBufferSizeInBytes = 0;// 缓冲区大小：缓冲区字节大小，使用AudioRecord.getMinBufferSize，获取
    private File mPCMFile;
    private String mPCMPath; //pcm存储地址
    private String mWavString;
    private Status mStatus = Status.END;
    FileOutputStream fos;

    /**
     * 使用AudioRecord录制音频
     * 1. 设置参数
     * 2. 开始录制
     * 3. 停止录制
     * 4. 转换为wav格式，同时存在pcm，wav两种文件
     * blog:
     * http://www.jianshu.com/p/90c4071c7768 AudioRecord基本使用
     * http://www.jianshu.com/p/af7787b409a2，声音中采样位数，采样频率，回声，降噪的概念，回声消除
     * http://www.jianshu.com/p/bee958826a9e，AudioTrack源码分析
     */
    private void startRecord() {
        mBufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING); //获取默认最小缓冲区大小
        mAudioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, mBufferSizeInBytes); //初始化
        mAudioRecord.startRecording(); //开始录音
        new Thread(new Runnable() { //录音转存为pcm文件需要在子线程
            @Override
            public void run() { //需要加入Status，控制状态，处理
                byte[] audioData = new byte[mBufferSizeInBytes]; //可以暂停录音，中间文件保存在这里，生成多个文件，最后再合成所有录音：http://blog.csdn.net/imhxl/article/details/52190451
                mPCMPath = FileUtils.createFilePath("pcm", 1);
                mPCMFile = new File(mPCMPath);
                try {
                    fos = new FileOutputStream(mPCMFile); //创建输出文件流
                    int readSize = 0;
                    mStatus = Status.RUNNING;
                    while (mStatus == Status.RUNNING) { //正在录音
                        readSize = mAudioRecord.read(audioData, 0, mBufferSizeInBytes); //从缓冲区中读数据
                        if (AudioRecord.ERROR_INVALID_OPERATION != readSize && fos != null) {
                            fos.write(audioData); //数据保存到文件，同时这里可以对数据进行处理，例如推流，变声等
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close(); //关闭流
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    private String mOutputPath;

    /**
     * 停止录制音频
     * 1. 可以对录音文件进行转换，pcm->wav
     * 2. 如果有暂停录音，对多份录音文件进行合并
     */
    private void stopRecord() {
        mAudioRecord.stop();
        mStatus = Status.END; //设置状态，正在录音的循环会终止
        if (mAudioRecord != null) {
            mAudioRecord.release();
            mAudioRecord = null;
        }
        new Thread(new Runnable() {  //pcm->wav
            @Override
            public void run() {
                mOutputPath = FileUtils.createCommonDir() + "AUD_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".wav";
                MediaUtils.pcm2wav(
                        44100,
                        16,
                        2,
                        mBufferSizeInBytes,
                        mPCMFile.getAbsolutePath(),
                        mOutputPath);
            }
        }).start();
    }

    /**
     * 使用AudioTrack播放pcm音频
     */
    private void playWithAudioTrack() {
        if (mBufferSizeInBytes == 0) {
            ToastUtils.show(this, "先录制，才能播放");
            return;
        }
        final AudioTrack audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                44100,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                mBufferSizeInBytes,
                AudioTrack.MODE_STREAM);
        //读取pcm文件
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataInputStream dis = null;
                try {
                    dis = new DataInputStream(new FileInputStream(mPCMPath));
                    byte[] buffer = new byte[mBufferSizeInBytes]; //设置读取缓冲区
                    int length;
                    while ((length = dis.read(buffer, 0, buffer.length)) > 0) {
                        audioTrack.write(buffer, 0, length);
                        audioTrack.play();
                    }
                    audioTrack.stop();
                    audioTrack.release();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (dis != null) {
                        try {
                            dis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 使用MediaPlayer播放wav音频
     */
    private void playWithMediaPlayer() {
        if (!TextUtils.isEmpty(mOutputPath)) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(mOutputPath);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare(); //准备播放
                mediaPlayer.start();//开始播放
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
