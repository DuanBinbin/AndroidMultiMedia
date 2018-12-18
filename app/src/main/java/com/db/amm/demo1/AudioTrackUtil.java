package com.db.amm.demo1;

import android.app.Activity;

/**
 * @描述：     @音频文件左右声道播放，主要用来播放已经存在的音频文件
 * @作者：     @Bin
 * @创建时间： @2018/12/17 17:58
 */
public final class AudioTrackUtil {

    private AudioTrackThread mAudioTrackThread;
    private AudioTrackThread mChannelLeftPlayer;
    private AudioTrackThread mChannelRightPlayer;

    private String mPlayFileName;
    private Activity mActivity;

    public void setPlayFileName(String mPlayFileName) {
        this.mPlayFileName = mPlayFileName;
    }

    public AudioTrackUtil(Activity activity){
        this.mActivity = activity;
    }

    /**
     * 开始播放
     */
    public void start(){
        if (null != mAudioTrackThread) {
            mAudioTrackThread.stopp();
            mAudioTrackThread = null;
        }
        mAudioTrackThread = new AudioTrackThread(mActivity, mPlayFileName);
        mAudioTrackThread.start();
    }

    /**
     * 暂停播放
     */
    public void pause(){
        if (null != mAudioTrackThread) {
            mAudioTrackThread.pause();
        }
    }

    /**
     * 继续播放
     */
    public void continuePlay(){
        if (null != mAudioTrackThread) {
            mAudioTrackThread.play();
        }
    }

    /**
     * 停止播放
     */
    public void stop(){
        if (null != mAudioTrackThread) {
            mAudioTrackThread.stopp();
            mAudioTrackThread = null;
        }
    }

    /**
     * 禁用左声道
     */
    public void disableChannelLeft(){
        if (null != mAudioTrackThread){
            mAudioTrackThread.setChannel(false, true);
        }
    }


    /**
     * 禁用右声道
     */
    public void disableChannelRight(){
        if (null != mAudioTrackThread){
            mAudioTrackThread.setChannel(true, false);
        }
    }


    /**
     * 恢复双声道
     */
    public void restoreDualChannels(){
        if (null != mAudioTrackThread){
            mAudioTrackThread.setChannel(true, true);
        }
    }

    /**
     * 左右声道播放不同的数据
     * @param leftFileName
     * @param rightFileName
     */
    public void playDifferentFiles(String leftFileName,String rightFileName){
        if (null != mChannelLeftPlayer) {
            mChannelLeftPlayer.stopp();
            mChannelLeftPlayer = null;
        }
        if (null != mChannelRightPlayer) {
            mChannelRightPlayer.stopp();
            mChannelRightPlayer = null;
        }

        mChannelLeftPlayer = new AudioTrackThread(mActivity, leftFileName);
        mChannelRightPlayer = new AudioTrackThread(mActivity, rightFileName);

        mChannelLeftPlayer.setChannel(true, false);
        mChannelRightPlayer.setChannel(false, true);

        mChannelLeftPlayer.start();
        mChannelRightPlayer.start();
    }

    /**
     * 设置左右声道平衡
     * @param max
     * @param balance
     */
    public void  setBalance(int max,int balance){
        if (null != mAudioTrackThread){
            mAudioTrackThread.setBalance(max,balance);
        }
    }
}
