package com.db.amm.demo1;

import android.app.Activity;

/**
 * @描述：     @音频文件左右声道播放
 * @作者：     @Bin
 * @创建时间： @2018/12/17 17:58
 */
public final class DemoOne {

    private PlayThread mPlayThread;
    private PlayThread mChannelLeftPlayer;
    private PlayThread mChannelRightPlayer;

    private String mPlayFileName;
    private Activity mActivity;

    public void setPlayFileName(String mPlayFileName) {
        this.mPlayFileName = mPlayFileName;
    }

    public DemoOne(Activity activity){
        this.mActivity = activity;
    }

    /**
     * 开始播放
     */
    public void start(){
        if (null != mPlayThread) {
            mPlayThread.stopp();
            mPlayThread = null;
        }
        mPlayThread = new PlayThread(mActivity, mPlayFileName);
        mPlayThread.start();
    }

    /**
     * 暂停播放
     */
    public void pause(){
        if (null != mPlayThread) {
            mPlayThread.pause();
        }
    }

    /**
     * 继续播放
     */
    public void continuePlay(){
        if (null != mPlayThread) {
            mPlayThread.play();
        }
    }

    /**
     * 停止播放
     */
    public void stop(){
        if (null != mPlayThread) {
            mPlayThread.stopp();
            mPlayThread = null;
        }
    }

    /**
     * 禁用左声道
     */
    public void disableChannelLeft(){
        if (null != mPlayThread){
            mPlayThread.setChannel(false, true);
        }
    }


    /**
     * 禁用右声道
     */
    public void disableChannelRight(){
        if (null != mPlayThread){
            mPlayThread.setChannel(true, false);
        }
    }


    /**
     * 恢复双声道
     */
    public void restoreDualChannels(){
        if (null != mPlayThread){
            mPlayThread.setChannel(true, true);
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

        mChannelLeftPlayer = new PlayThread(mActivity, leftFileName);
        mChannelRightPlayer = new PlayThread(mActivity, rightFileName);

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
        if (null != mPlayThread){
            mPlayThread.setBalance(max,balance);
        }
    }
}
