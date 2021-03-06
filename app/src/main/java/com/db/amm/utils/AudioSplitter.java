package com.db.amm.utils;

import com.db.amm.base.BaseApplication;

/**
 * @描述：     @音频拆分工具类，将Android立体声pcm的数据结构，左右声道拆分，左右声道反转
 * @作者：     @Bin
 * @创建时间： @2018/12/18 10:14
 * @参考文档   @Android立体声pcm的数据结构，左右声道拆分、左右声道反转 https://blog.csdn.net/hi_ugly/article/details/80977850
 */
public final class AudioSplitter {

    private static byte[] leftData;
    private static byte[] rightData;

    public static byte[] getLeftData() {
        return leftData;
    }

    public static byte[] getRightData() {
        return rightData;
    }

    /**
     * 立体声拆分
     * @param data 输入数据
     */
    public static final void splitStereoPcm(byte[] data) {
        try {
            int monoLength = data.length / 2;
            leftData = new byte[monoLength];
            rightData = new byte[monoLength];
            for (int i = 0; i < monoLength; i++) {
                if (i % 2 == 0) {
                    System.arraycopy(data, i * 2, leftData, i, 2);
                } else {
                    System.arraycopy(data, i * 2, rightData, i - 1, 2);
                }
            }
            ToastUtils.show(BaseApplication.getContext(),"立体声拆分成功");
        } catch (Exception e){
            e.printStackTrace();
            ToastUtils.show(BaseApplication.getContext(),"立体声拆分失败,原因：" + e.getMessage());
        }
    }

    /**
     * 左右声道进行反转
     * @param data  输入数据
     * @return      反转后的数据
     */
    public static final byte[] getReversedData(byte[] data) {
        byte[] reversed = new byte[data.length];
        for (int i = 0; i < data.length - 3; i = i + 4) {
            reversed[i] = data[i+2];
            reversed[i+1] = data[i+3];
            reversed[i+2] = data[i];
            reversed[i+3] = data[i+1];
        }
        return reversed;
    }

    /**
     * 合成声音
     * @param data 输入数据
     */
    public static final void monoToStereo(byte[] data) {
        try {
            int monoLength = data.length * 2;
            leftData = new byte[monoLength];
            rightData = new byte[monoLength];
            for (int i = 0; i < monoLength; i++) {
                if (i % 2 == 0) {
                    leftData[2 * i] = data[i];
                    leftData[2 * i + 1] = data[i + 1];
                } else {
                    rightData[2 * i] = data[i - 1];
                    rightData[2 * i + 1] = data[i];
                }
            }
            ToastUtils.show(BaseApplication.getContext(),"声音合成成功");
        } catch (Exception e){
            e.printStackTrace();
            ToastUtils.show(BaseApplication.getContext(),"声音合成失败,原因：" + e.getMessage());
        }
    }
}
