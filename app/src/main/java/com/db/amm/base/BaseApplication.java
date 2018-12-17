package com.db.amm.base;

import android.app.Application;
import android.content.Context;

/**
 * @描述：     @Application基类
 * @作者：     @Bin
 * @创建时间： @2018/12/17 17:46
 */
public class BaseApplication extends Application {

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }


}
