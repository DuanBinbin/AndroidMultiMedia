package com.db.amm.utils;

import com.db.amm.base.BaseApplication;

import java.io.IOException;
import java.io.InputStream;

/**
 * @描述：     @assets文件夹工具类
 * @作者：     @Bin
 * @创建时间： @2018/12/18 15:16
 */
public final class AssetsUtil {

    /**
     * 获取assets目录下文件的InputStream
     * @param fileName      assets目录下文件名
     * @return
     * @throws IOException
     */
    public static InputStream getInputStream(String fileName) throws IOException {
        return BaseApplication.getContext().getResources().getAssets().open(fileName);
    }
}
