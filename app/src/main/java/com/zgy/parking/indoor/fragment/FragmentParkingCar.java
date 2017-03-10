package com.zgy.parking.indoor.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.marker.FMModel;
import com.zgy.parking.indoor.R;
import com.zgy.parking.indoor.activity.SearchFMapActivity;
import com.zgy.parking.indoor.activity.ShowFMapActivity;
import com.zgy.parking.indoor.bean.MapCoord;
import com.zgy.parking.indoor.bean.ParkingRecordInfo;
import com.zgy.parking.indoor.view.AlertButtonDialog;

import org.litepal.crud.DataSupport;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：lenovo on 2017/2/13
 * <p>
 * 车位找车
 */

public class FragmentParkingCar extends Fragment {

    @BindView(R.id.tv_clear_flag)
    TextView mClear;

    @BindView(R.id.parking_car_back)
    TextView mBack;

    @BindView(R.id.tv_parking_find_car)
    TextView mParkingName;

    @BindView(R.id.linear_parking_car_show)
    LinearLayout mLinearParkingCar;

    @BindView(R.id.linear_search1)
    LinearLayout mLinearSearch;

    @BindView(R.id.linear_find_car)
    LinearLayout mLinearFindCar;

    @BindView(R.id.tv_recoed_car)
    TextView mRecordCar;


    @BindView(R.id.bt_where_move)
    TextView mWhere;

    @BindView(R.id.tv_parking_my_car)
    TextView mParkingMyCar;

    @BindView(R.id.tv_parking_gone)
    TextView mParkingGone;


    @BindView(R.id.tv_find_car)
    TextView mFindCar;

    @BindView(R.id.parking_car_line_sow)
    ImageView mLineCarShow;

    private View rootView;

    private boolean flagClick = false;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_parking_car, null);

        } else {
            ViewParent parent = rootView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(rootView);
            }
        }

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    private void initDate() {
        ParkingRecordInfo recordInfo = DataSupport.findFirst(ParkingRecordInfo.class);

        if(recordInfo !=null){
            mClear.setVisibility(View.VISIBLE);

            if(!TextUtils.isEmpty(recordInfo.getParkingNum1())){

                mParkingMyCar.setText("爱车位置");
                mParkingGone.setVisibility(View.INVISIBLE);
                mLineCarShow.setVisibility(View.VISIBLE);

                mRecordCar.setText(recordInfo.getParkingNum1());

                if(!TextUtils.isEmpty(recordInfo.getParkingNum2())){
                    //设置电梯口可点击
                    mWhere.setClickable(false);
                    mWhere.setEnabled(false);
                    mLinearSearch.setClickable(false);
                    mLinearSearch.setEnabled(false);


                    mFindCar.setText(recordInfo.getFloor2()+"层 "+recordInfo.getParkingNum2());

                    mLinearSearch.setBackgroundResource(R.drawable.round_group_grey_find_car);
                    mWhere.setBackgroundResource(R.drawable.round_group_grey_smail_find_car);
                    mLinearFindCar.setBackgroundResource(R.drawable.round_group_yellow_find_car);
                }else{
                    //
                    mLinearSearch.setClickable(false);
                    mLinearSearch.setEnabled(false);
                    //设置电梯口可点击
                    mWhere.setClickable(true);
                    mWhere.setEnabled(true);
                    mLinearFindCar.setClickable(true);
                    mLinearFindCar.setEnabled(true);

                    mLinearSearch.setBackgroundResource(R.drawable.round_group_grey_find_car);
                    mWhere.setBackgroundResource(R.drawable.round_group_yellow_smail_find_car);
                    mLinearFindCar.setBackgroundResource(R.drawable.round_group_yellow_find_car);

                }
            }



        }else{

            mClear.setVisibility(View.GONE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        initDate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    private void showDialog() {

        new AlertButtonDialog(getActivity()).builder()
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {

                        DataSupport.deleteAll(ParkingRecordInfo.class);
                        ShowFMapActivity.stCoord = new MapCoord(0, new FMMapCoord(0, 0));
                        ShowFMapActivity.endCoord = new MapCoord(0, new FMMapCoord(0, 0));

                        mWhere.setClickable(false);
                        mWhere.setEnabled(false);


                        mLinearFindCar.setClickable(false);
                        mLinearFindCar.setEnabled(false);

                        mLinearSearch.setClickable(true);
                        mLinearSearch.setEnabled(true);

                        mLinearSearch.setBackgroundResource(R.drawable.round_group_yellow_find_car);
                        mWhere.setBackgroundResource(R.drawable.round_group_grey_smail_find_car);
                        mLinearFindCar.setBackgroundResource(R.drawable.round_group_grey_find_car);

                        mLinearParkingCar.setVisibility(View.GONE);
                        mParkingGone.setVisibility(View.VISIBLE);
                        mLineCarShow.setVisibility(View.GONE);
                        mParkingMyCar.setText("输入车位号");
                        mParkingName.setText("停车场");
                        mRecordCar.setText("点击记录车位");
                        mWhere.setText("最近电梯口");
                        mFindCar.setText("点击寻找爱车");
                        mClear.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                    }
                })
                .setTitle("提示")
                .setMsg("是否要清除标记?")
                .show();
    }


    @OnClick({R.id.parking_car_back,R.id.linear_parking_car_show,R.id.tv_clear_flag, R.id.linear_search1, R.id.bt_where_move, R.id.linear_find_car})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.parking_car_back:
                getActivity().finish();
                break;
            case R.id.linear_parking_car_show:

                break;
            case R.id.tv_clear_flag:
                showDialog();
                break;
            case R.id.linear_search1:
                flagClick = false;
                Intent intenSearch = new Intent(getActivity(), SearchFMapActivity.class);
                startActivity(intenSearch);
                break;
            case R.id.bt_where_move:
                ParkingRecordInfo  recordInfo = DataSupport.findFirst(ParkingRecordInfo.class);
                if(recordInfo !=null){
                    if(!TextUtils.isEmpty(recordInfo.getParkingNum1())){
                        Intent intenShow = new Intent(getActivity(), ShowFMapActivity.class);
                        intenShow.putExtra("mapCoord", "mapCoord");
                        startActivity(intenShow);
                    }

                }


                break;
            case R.id.linear_find_car:

                ParkingRecordInfo  recordInfo2 = DataSupport.findFirst(ParkingRecordInfo.class);
                if(recordInfo2 !=null){
                    if(!TextUtils.isEmpty(recordInfo2.getParkingNum1())){
                        if (!flagClick) {
                            flagClick = true;
                        }

                        if(!TextUtils.isEmpty(recordInfo2.getParkingNum2())){
                            Intent inten = new Intent(getActivity(), ShowFMapActivity.class);
                            startActivity(inten);
                        }else{
                            Intent inten = new Intent(getActivity(), SearchFMapActivity.class);
                            inten.putExtra("flag", "flag");
                            startActivity(inten);
                        }

                    }


                }

                break;
        }
    }
}
