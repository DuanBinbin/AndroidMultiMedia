package com.db.amm.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * @描述：     @Toast工具类
 * @作者：     @Bin
 * @创建时间： @2018/12/17 18:57
 */
public class ToastUtils {

    public static void show(Context context, String des) {
        if (!TextUtils.isEmpty(des)) {
            Toast.makeText(context, des, Toast.LENGTH_SHORT).show();
        }
    }
}
