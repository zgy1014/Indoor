package com.zgy.parking.indoor.activity;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zgy.parking.indoor.R;
import com.zgy.parking.indoor.fragment.FragmentParkingCar;
import com.zgy.parking.indoor.fragment.FragmentPhoto;
import com.zgy.parking.indoor.utils.IPermission;
import com.zgy.parking.indoor.utils.LogUtils;
import com.zgy.parking.indoor.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 反向停车
 */
public class ParkingFindCarActivity
        extends BaseActivity {


    @BindView(R.id.fragment_page)
    LinearLayout fragmentPage;
    @BindView(R.id.main_tab_photo_icon)
    ImageView mainTabPhotoIcon;
    @BindView(R.id.main_tab_photo)
    TextView mainTabPhoto;
    @BindView(R.id.main_tab_photo_layout)
    LinearLayout mainTabPhotoLayout;
    @BindView(R.id.main_tab_car_icon)
    ImageView mainTabCarIcon;
    @BindView(R.id.main_tab_car)
    TextView mainTabCar;
    @BindView(R.id.main_tab_car_layout)
    LinearLayout mainTabCarLayout;
    /**
     * 两个页面
     */
    private ArrayList<Fragment> fragments;
    private FragmentParkingCar fragmentCar;
    private FragmentPhoto fragmentPhoto;
    private FragmentTransaction fragmentTransaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parking_find_car);

        // 获取控件
        ButterKnife.bind(this);
        fragments = new ArrayList<Fragment>();
        fragmentCar = new FragmentParkingCar();
        fragmentPhoto = new FragmentPhoto();

        fragments.add(fragmentPhoto);
        fragments.add(fragmentCar);
        String fragment = getIntent().getStringExtra("fragment");
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_page, fragmentPhoto, "FragmentPhoto");
        fragmentTransaction.add(R.id.fragment_page, fragmentCar, "FragmentParkingCar");
        fragmentTransaction.hide(fragmentCar)
                .hide(fragmentPhoto);
        if (!TextUtils.isEmpty(fragment)) {
            if (fragment.equals("0")) {
                setCheckedTab(0);
            } else {
                setCheckedTab(1);
            }
        }


    }


    private void setCheckedTab(int position) {

        if (position == 0) {
            fragmentTransaction.show(fragmentPhoto)
                    .commit();
            mainTabPhotoIcon.setImageResource(R.mipmap.looking_picture_recording);
            mainTabPhoto.setTextColor(getResources().getColor(R.color.color_status));
            mainTabCarIcon.setImageResource(R.mipmap.looking_park_record);
            mainTabCar.setTextColor(getResources().getColor(R.color.color_gray));
        } else if (position == 1) {
            fragmentTransaction.show(fragmentCar)
                    .commit();
            mainTabPhotoIcon.setImageResource(R.mipmap.looking_picture_record);
            mainTabPhoto.setTextColor(getResources().getColor(R.color.color_gray));
            mainTabCarIcon.setImageResource(R.mipmap.looking_park_recording);
            mainTabCar.setTextColor(getResources().getColor(R.color.color_status));
        }
    }

    @OnClick({R.id.main_tab_photo_layout, R.id.main_tab_car_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_tab_photo_layout:
                String[] permission = new String[]{Manifest.permission.CAMERA};
                requestRunTimePermission(permission, new PermisionListenr());

                if (ContextCompat.checkSelfPermission(ParkingFindCarActivity.this,
                        permission[0]) != PackageManager.PERMISSION_GRANTED) {
                    ToastUtil.showMessage(ParkingFindCarActivity.this,"您还满意添加相机权限");
                }else{
                    fragmentTransaction = getSupportFragmentManager().beginTransaction()
                            .hide(fragmentCar)
                            .hide(fragmentPhoto);
                    setCheckedTab(0);
                }

                break;
            case R.id.main_tab_car_layout:
                fragmentTransaction = getSupportFragmentManager().beginTransaction()
                        .hide(fragmentCar)
                        .hide(fragmentPhoto);
                setCheckedTab(1);
                break;
        }
    }


    private class PermisionListenr
            implements IPermission
    {
        @Override
        public void onGranted() {
            LogUtils.e("test", "onGranted");
        }

        @Override
        public void onDenied(List<String> deniedPermission) {
            LogUtils.e("test", "onDenied");
        }
    }



}
