package com.zgy.parking.indoor.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.zgy.parking.indoor.BaseApplication;
import com.zgy.parking.indoor.R;
import com.zgy.parking.indoor.utils.IPermission;

import java.util.ArrayList;
import java.util.List;


public class BaseActivity
        extends AppCompatActivity
{

    /**
     * gson,不解释
     */
    private static Toast   mToast;
    public         Context context;
    private static Handler mhandler = new Handler();
    protected LinearLayout request_failure_layout;
    protected LinearLayout ll_nodata;
    private final static int REQUEST_CODE = 1;
    private static IPermission mListener;
    private static BaseApplication application;

    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        application = (BaseApplication) getApplication();
        application.getActivityList()
                   .add(this);
    }

    protected void setStatusBlack(Activity activity) {
       // StatusBarUtil.setColor(activity, getResources().getColor(R.color.color_e5e5e5), 255);
    }

    protected void hideStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window                     win       = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int                  bits      = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if (true) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
            }
            win.setAttributes(winParams);
        }
    }

    @SuppressLint("ShowToast")
    public void showToast(String msg) {

        mhandler.removeCallbacks(r);
        if (null != mToast) {
            mToast.setText(msg);
        } else {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        }
        mhandler.postDelayed(r, 1000);
        mToast.show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.new_push_left_in, R.anim.new_push_left_out);// 从右向左推出动画效果
    }

    public static void requestRunTimePermission(String[] permissions, IPermission listener) {
        Activity topActivity = getTopActivity();

        if (topActivity == null) {
            return;
        }
        mListener = listener;
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(topActivity,
                                                  permission) != PackageManager.PERMISSION_GRANTED)
            {
                permissionList.add(permission);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(topActivity,
                                              permissionList.toArray(new String[permissionList.size()]),
                                              REQUEST_CODE);
        } else {
            //doSomething
            mListener.onGranted();
        }
    }

    private static Activity getTopActivity() {

        if (application.getActivityList()
                       .isEmpty())
        {
            return null;
        } else {
            return application.getActivityList()
                              .get(application.getActivityList()
                                              .size() - 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0) {
                    List<String> deniedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int    grantResult = grantResults[i];
                        String permission  = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                        }
                    }
                    if (deniedPermissions.isEmpty()) {
                        mListener.onGranted();
                    } else {
                        mListener.onDenied(deniedPermissions);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication application = (BaseApplication) getApplication();
        application.getActivityList()
                   .remove(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }


}
