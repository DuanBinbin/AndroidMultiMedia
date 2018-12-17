package com.db.amm.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @描述：     @自定义Activity基类，实现一些基础共有方法
 * @作者：     @Bin
 * @创建时间： @2018/11/6 16:44
 */
public class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "BaseActivity";
    private List<Activity> activityList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"The current activity is " + getClass().getSimpleName());
        addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }

    /**
     * 添加Activity
     * @param activity
     */
    protected final void addActivity(Activity activity){
        activityList.add(activity);
    }

    /**
     * 移除Activity
     * @param activity
     */
    protected final void removeActivity(Activity activity){
        activityList.remove(activity);
    }

    /**
     * 结束所有Activity
     */
    protected final void finishAll(){
        for (Activity activity : activityList){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
        activityList.clear();
    }

    @Override
    public void onClick(View view) {

    }
}


