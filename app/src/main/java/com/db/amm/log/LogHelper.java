package com.db.amm.log;

import android.provider.Settings;
import android.util.Log;

import com.db.amm.BaseApplication;
import com.db.amm.BuildConfig;

/**
 * Log工具类
 */
public final class LogHelper {

    public static void i(String tag, String arg){
        if(BuildConfig.DEBUG || isADBEnabled()){
            Log.v(tag,arg);
        }
    }

    public static void d(String tag, String arg){
        if(BuildConfig.DEBUG || isADBEnabled()){
            Log.d(tag,arg);
        }
    }

    public static void v(String tag, String arg){
        if(BuildConfig.DEBUG || isADBEnabled()){
            Log.v(tag,arg);
        }
    }

    public static void e(String tag, String arg){
        if(BuildConfig.DEBUG || isADBEnabled()){
            Log.e(tag,arg);
        }
    }

    public static void e(String tag, String arg, Throwable e){
        if(BuildConfig.DEBUG || isADBEnabled()){
            Log.e(tag,arg,e);
        }
    }

    public static void w(String tag, String arg){
        if(BuildConfig.DEBUG|| isADBEnabled()){
            Log.w(tag,arg);
        }
    }

    public static void s(String message){
        if(BuildConfig.DEBUG || isADBEnabled()){
            System.out.println(message);
        }
    }

    /**
     * 判断adb是否启用
     *
     * @return
     */
    private static boolean isADBEnabled(){
        return Settings.Secure.getInt(
                BaseApplication.getContext().getContentResolver(),
                Settings.Global.ADB_ENABLED, 0) > 0;
    }
}
