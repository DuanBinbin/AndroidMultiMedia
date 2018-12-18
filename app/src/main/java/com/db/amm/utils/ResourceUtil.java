package com.db.amm.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.db.amm.base.BaseApplication;

/**
 * @描述：     @获取资源工具类
 * @作者：     @Bin
 * @创建时间： @2018-12-18
 */
public final class ResourceUtil {

    /**
     * 根据资源ID返回字符串
     *
     * @param resid
     * @return
     */
    public static final String getString(int resid) {
        return BaseApplication.getContext().getString(resid);
    }

    /**
     * 获取dimens文件
     * @param resid
     * @return
     */
    public static final int getDimensionPixelOffset(int resid){
        return BaseApplication.getContext().
                getResources().getDimensionPixelOffset(resid);
    }

    /**
     * 根据资源ID和传入的参数进行格式化返回字符串
     *
     * @param resid      资源id
     * @param formatArgs 格式化参数数组
     * @return
     */
    public static final String getString(int resid, Object... formatArgs) {
        return BaseApplication.getContext().getString(resid, formatArgs);
    }

    /**
     * 获取颜色
     */
    public static final int getColor(int resid) {
        return BaseApplication.getContext().getResources().getColor(resid);
    }

    /**
     * 获取图片
     */
    public static final Drawable getDrawable(int resid) {
        return BaseApplication.getContext().getResources().getDrawable(resid);
    }

    /**
     * 获取dimems大小（单位为像素pix）
     *
     * @param resid
     * @return
     */
    public static final float getDimens(int resid) {
        return BaseApplication.getContext().getResources().getDimension(resid);
    }

    /**
     * 根据资源id返回字符串数组
     *
     * @param resid
     * @return
     */
    public static final String[] getStringArray(int resid) {
        return BaseApplication.getContext().getResources().getStringArray(resid);
    }

    /**
     * 获取资源中的整数数据
     *
     * @param resid
     * @return
     */
    public static final int getInteger(int resid) {
        return BaseApplication.getContext().getResources().getInteger(resid);
    }

    /**
     * 根据资源名获取资源id
     * @param name 资源名
     * @return
     */
    public static final int getDrawableResId(String name) {
        final Context context = BaseApplication.getContext();
        return context.getResources().getIdentifier(name, "drawable",
                context.getPackageName());
    }

}
