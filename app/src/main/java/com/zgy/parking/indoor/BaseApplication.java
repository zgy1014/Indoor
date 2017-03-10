package com.zgy.parking.indoor;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.fengmap.android.FMMapSDK;
import org.litepal.LitePalApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseApplication
        extends LitePalApplication
{
    private static List<Activity> activities;

    private static final String TAG = "BaseApplication";
    private static BaseApplication application;
    private static Handler         mHandler;
    public static  float           angle;

    private static Context   context;


    public static BaseApplication getInstance() {

        if (application == null) {
            synchronized (BaseApplication.class) {
                if (application == null) {
                    application = new BaseApplication();
                }
            }
        }
        return application;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        context = getApplicationContext();
        activities = new ArrayList<Activity>();

        // 在使用 SDK 各组间之前初始化 context 信息，传入 Application
        FMMapSDK.init(this);


    }

    /**
     * 分割 Dex 支持
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }



    public static Context getContext() {
        return context;
    }

    public List<Activity> getActivityList() {
        return activities;
    }


}
