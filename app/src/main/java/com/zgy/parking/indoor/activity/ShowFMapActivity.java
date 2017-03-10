package com.zgy.parking.indoor.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengmap.android.FMErrorMsg;
import com.fengmap.android.analysis.navi.FMNaviAnalyser;
import com.fengmap.android.analysis.navi.FMNaviResult;
import com.fengmap.android.data.OnFMDownloadProgressListener;
import com.fengmap.android.exception.FMObjectException;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.FMMapUpgradeInfo;
import com.fengmap.android.map.FMMapView;
import com.fengmap.android.map.FMViewMode;
import com.fengmap.android.map.event.OnFMMapInitListener;
import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.layer.FMImageLayer;
import com.fengmap.android.map.layer.FMLineLayer;
import com.fengmap.android.map.layer.FMLocationLayer;
import com.fengmap.android.map.marker.FMImageMarker;
import com.fengmap.android.map.marker.FMLineMarker;
import com.fengmap.android.map.marker.FMLocationMarker;
import com.fengmap.android.map.marker.FMSegment;
import com.fengmap.android.utils.FMMath;
import com.zgy.parking.indoor.R;
import com.zgy.parking.indoor.adapter.ViewHelper;
import com.zgy.parking.indoor.bean.MapCoord;
import com.zgy.parking.indoor.utils.ConvertUtils;
import com.zgy.parking.indoor.utils.FMLocationAPI;
import com.zgy.parking.indoor.utils.FileUtil;
import com.zgy.parking.indoor.utils.LogUtils;
import com.zgy.parking.indoor.view.UITitleBackView;


import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;


/**
 * 作者：lenovo on 2017/1/11
 */

public class ShowFMapActivity extends BaseActivity implements OnFMMapInitListener, FMLocationAPI.OnFMLocationListener, View.OnClickListener, UITitleBackView.onBackImageClickListener, UITitleBackView.OnRightTextViewClickListener {

    /**
     * 定位切换楼层
     */
    protected static final int WHAT_LOCATE_SWITCH_GROUP = 1;
    /**
     * 两个点相差最大距离20米
     */
    protected static final double MAX_BETWEEN_LENGTH = 20;

    /**
     * 进入地图显示级别
     */
    protected static final int MAP_NORMAL_LEVEL = 20;

    FMMap mFMMap;

    protected HashMap<Integer, FMImageLayer> mImageLayers = new HashMap<>();

    /**
     * 终点图层
     */
    protected FMImageLayer mEndImageLayer;
    /**
     * 默认起点
     */
    public static MapCoord stCoord = new MapCoord(0, new FMMapCoord(12961647.576796599, 4861814.63807118));
    /**
     * 默认终点
     */
    public static MapCoord endCoord = new MapCoord(0, new FMMapCoord(12961699.79823795, 4861826.46384646));
    /**
     * 停车的点
     */
    public static MapCoord stOrendCoord = new MapCoord(0, new FMMapCoord(12961647.576796599, 4861814.63807118));
    /**
     * 最近电梯口的位置
     */
    public static MapCoord endCoord1 = new MapCoord(0, new FMMapCoord(12961647.576796599, 4861814.63807118));
    /**
     * 当前位置
     */
    public static MapCoord stCoord1 = new MapCoord(0, new FMMapCoord(12961647.576796599, 4861814.63807118));


    /**
     * 导航行走点集合
     */
    protected ArrayList<ArrayList<FMMapCoord>> mNaviPoints = new ArrayList<>();
    /**
     * 导航行走的楼层集合
     */
    protected ArrayList<Integer> mNaviGroupIds = new ArrayList<>();

    /**
     * 起点图层
     */
    protected FMImageLayer mStartImageLayer;
    /**
     * 导航分析
     */
    protected FMNaviAnalyser mNaviAnalyser;

    /**
     * 线图层
     */
    protected FMLineLayer mLineLayer;

    /**
     * 定位图层
     */
    protected FMLocationLayer mLocationLayer;


    /**
     * 导航行走索引
     */
    protected int mCurrentIndex = 0;
    /**
     * 差值动画
     */
    protected FMLocationAPI mLocationAPI;

    /**
     * 真实定位标注
     */
    private FMLocationMarker mRealMarker;
    /**
     * 约束过定位标注
     */
    private FMLocationMarker mHandledMarker;
    /**
     * 记录上一次行走坐标
     */
    private FMMapCoord mLastMoveCoord;

    private TextView mControl;

    private TextView mStartNav;

    /**
     * 当有2层的时候，第二层的楼层
     */
    private int groupIdShow2 = 0;

    /**
     * 当只要一层或者在这一层的时候
     */
    private int groupIdShow1 = 0;

    private  String mapCoordIntent;

    UITitleBackView mUib;


    /**
     * 处理UI消息
     */
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_LOCATE_SWITCH_GROUP:
                    updateLocateGroupView();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_fmap);
        setStatusBlack(this);
        ButterKnife.bind(this);
        initHeader();

        FMMapView mapView = (FMMapView) findViewById(R.id.mapview);
        mControl = (TextView) findViewById(R.id.btn_group_control);
        mStartNav = (TextView) findViewById(R.id.btn_start_navigation);
        mFMMap = mapView.getFMMap();       //获取地图操作对象
        mapCoordIntent = getIntent().getStringExtra("mapCoord");
        if (!TextUtils.isEmpty(mapCoordIntent)) {
            stCoord = stOrendCoord;
            endCoord = endCoord1;
        }else{
            stCoord = stCoord1;
            endCoord = stOrendCoord;
        }

//        //加载离线数据
        String path = FileUtil.getDefaultMapPath(this);
      //  String path = PhotoUtil.SDPATHFMAP+FileUtil.DEFAULT_MAP_ID+FileUtil.FILE_TYPE_MAP;

        if(!TextUtils.isEmpty(path)){
            mFMMap.openMapByPath(path);
        }


        mFMMap.setOnFMMapInitListener(this);
        //  mFMMap.showCompass();                   //显示指北针//
   //     mFMMap.hiddenCompass();                 //隐藏指北针
        mStartNav.setOnClickListener(this);

    }

    private void initHeader() {
        mUib = (UITitleBackView) findViewById(R.id.show_map_uib);
        mUib.setBackImageVisiale(true);
        mUib.setOnBackImageClickListener(this);
        mUib.setTitleText("寻车路线");
        mUib.setRightContentVisbile(true);
        mUib.setRigthTextView("重新定位");
        mUib.setOnRightTextViewClickListener(this);

    }

    /**
     * 开始点击导航
     */
    public void startWalkingRouteLine() {
        //行走索引初始为0
        mCurrentIndex = 0;
        //     setStartAnimationEnable(false);

        //缩放地图状态
        setZoomLevel();
        //开始进行模拟行走
        groupIdShow1 = getWillWalkingGroupId();

        mControl.setText("F"+groupIdShow1);

        setFocusGroupId(groupIdShow1);
    }

    /**
     * 设置缩放动画
     *
     * @return
     */
    protected void setZoomLevel() {
        if (mFMMap.getZoomLevel() != MAP_NORMAL_LEVEL) {
            mFMMap.setZoomLevel(MAP_NORMAL_LEVEL, true);
        }
    }

    @Override
    public void onBackPressed() {

        //停止模拟轨迹动画
        if (mLocationAPI != null) {
            mLocationAPI.destroy();
        }

        if (mFMMap != null) {
            mFMMap.onDestroy();
        }
        super.onBackPressed();
    }


    @Override
    public void onMapInitSuccess(String s) {
        //加载离线主题
     //   mFMMap.loadThemeByPath(FileUtil.getDefaultThemePath(this));
        mFMMap.setFMViewMode(FMViewMode.FMVIEW_MODE_2D); //设置地图2D显示模式
        //线图层
        mLineLayer = mFMMap.getFMLayerProxy().getFMLineLayer();
        mFMMap.addLayer(mLineLayer);
        //定位层
        mLocationLayer = mFMMap.getFMLayerProxy().getFMLocationLayer();
        mFMMap.addLayer(mLocationLayer);
        //导航分析
        try {
            mNaviAnalyser = FMNaviAnalyser.getFMNaviAnalyserById(FileUtil.DEFAULT_MAP_ID);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (FMObjectException e) {
            e.printStackTrace();
        }
        if (stCoord != null && endCoord != null) {
            analyzeNavigation(stCoord, endCoord);
            LogUtils.e("test","stCoord.x"+stCoord.getMapCoord().x +endCoord.getMapCoord().x+"----"+stCoord.getMapCoord().y+endCoord.getMapCoord().y+"----");
            //地图返回成功以后才可以加载路径规划
            if(!stCoord.equals(endCoord)){
                //差值动画
                mLocationAPI = new FMLocationAPI();
                mLocationAPI.setFMLocationListener(this);
                startWalkingRouteLine();
            }else{
                mStartNav.setText("爱车寻找完毕");
                Toast.makeText(ShowFMapActivity.this,"您选择的车位起点和终点一样哦，请重新选择",Toast.LENGTH_SHORT).show();
            }

        }

    }

    /**
     * 路径规划,导航分析
     */
    private void analyzeNavigation(MapCoord stPoint, MapCoord endPoint) {
        clearImageLayer();
        // 添加起点图层
        mStartImageLayer = new FMImageLayer(mFMMap, stPoint.getGroupId());
        mFMMap.addLayer(mStartImageLayer);
        // 标注物样式
        FMImageMarker imageMarker = ViewHelper.buildImageMarker(getResources(), stPoint.getMapCoord(), R.mipmap.looking_men);
        mStartImageLayer.addMarker(imageMarker);

        // 添加终点图层
        mEndImageLayer = new FMImageLayer(mFMMap, endPoint.getGroupId());
        mFMMap.addLayer(mEndImageLayer);
        // 标注物样式
        if (!TextUtils.isEmpty(mapCoordIntent)) {
            imageMarker = ViewHelper.buildImageMarker(getResources(), endPoint.getMapCoord(), R.mipmap.looking_elevator);
        }else{
            imageMarker = ViewHelper.buildImageMarker(getResources(), endPoint.getMapCoord(), R.mipmap.looking_car);
        }

        mEndImageLayer.addMarker(imageMarker);
        // Log.e("test","-------"+stPoint.toString());
        analyzeNavigation2(stPoint.getGroupId(), stPoint.getMapCoord(), endPoint.getGroupId(), endPoint.getMapCoord());

    }

    /**
     * 导航分析。
     *
     * @param startGroupId 起点所在层
     * @param startPt      起点坐标
     * @param endGroupId   终点所在层
     * @param endPt        终点坐标
     */
    private void analyzeNavigation2(int startGroupId, FMMapCoord startPt, int endGroupId, FMMapCoord endPt) {
        int type = mNaviAnalyser.analyzeNavi(startGroupId, startPt, endGroupId, endPt, FMNaviAnalyser.FMNaviModule.MODULE_SHORTEST);
        if (type == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_SUCCESS) {
            fillWithPoints();
        //    addLineMarker();
            updateLineMarker(startGroupId);
        }
    }


    /**
     * 填充导航线段点
     */
    private void fillWithPoints() {
        clearWalkPoints();
        //获取路径规划上点集合数据
        ArrayList<FMNaviResult> results = mNaviAnalyser.getNaviResults();
        int focusGroupId = Integer.MIN_VALUE;
        for (FMNaviResult r : results) {
            int groupId = r.getGroupId();
            ArrayList<FMMapCoord> points = r.getPointList();
            //点数据小于2，则为单个数据集合
            if (points.size() < 2) {
                continue;
            }
            //判断是否为同层导航数据，非同层数据即其他层数据
            if (focusGroupId == Integer.MIN_VALUE || focusGroupId != groupId) {
                focusGroupId = groupId;
                //添加即将行走的楼层与点集合
                mNaviGroupIds.add(groupId);
                mNaviPoints.add(points);
            //    Log.e("test", mNaviGroupIds.size() + "=====mNaviPoints:===" + groupId + "==points==" + points.size());
            } else {
                mNaviPoints.get(mNaviPoints.size() - 1).addAll(points);
            }

            if (mNaviGroupIds != null) {

                if (mNaviGroupIds.size() == 1) {
                    mStartNav.setText("爱车寻找完毕");
                } else {
                    mStartNav.setText("您当前在F" + mNaviGroupIds.get(0) + "层， 请继续前往F" + mNaviGroupIds.get(1) + "层");
                }


            }


        }
    }


    /**
     * 清空行走的点集合数据
     */
    private void clearWalkPoints() {
        mCurrentIndex = 0;
        mNaviPoints.clear();
        mNaviGroupIds.clear();
    }

    /**
     * 添加线标注
     */
    protected void addLineMarker() {
        clearLineLayer();

        ArrayList<FMNaviResult> results = mNaviAnalyser.getNaviResults();
        // 填充导航线数据
        ArrayList<FMSegment> segments = new ArrayList<>();
        for (FMNaviResult r : results) {
            int groupId = r.getGroupId();
            FMSegment s = new FMSegment(groupId, r.getPointList());
            segments.add(s);
        }

        //添加LineMarker
        FMLineMarker lineMarker = ViewHelper.buildLineMarker(segments);
        mLineLayer.addMarker(lineMarker);

    }




    /**
     * 清除线图层
     */
    public void clearLineLayer() {
        if (mLineLayer != null) {
            mLineLayer.removeAll();
        }
    }



    /**
     * 地图加载失败回调事件
     *
     * @param path      地图所在sdcard路径
     * @param errorCode 失败加载错误码，可以通过{@link FMErrorMsg#getErrorMsg(int)}获取加载地图失败详情
     */
    @Override
    public void onMapInitFailure(String path, int errorCode) {
        //TODO 可以提示用户地图加载失败原因，进行地图加载失败处理
    }

    /**
     * 当{@link FMMap#openMapById(String, boolean)}设置openMapById(String, false)时地图不自动更新会
     * 回调此事件，可以调用{@link FMMap#upgrade(FMMapUpgradeInfo, OnFMDownloadProgressListener)}进行
     * 地图下载更新
     *
     * @param upgradeInfo 地图版本更新详情,地图版本号{@link FMMapUpgradeInfo#getVersion()},<br/>
     *                    地图id{@link FMMapUpgradeInfo#getMapId()}
     * @return 如果调用了{@link FMMap#upgrade(FMMapUpgradeInfo, OnFMDownloadProgressListener)}地图下载更新，
     * 返回值return true,因为{@link FMMap#upgrade(FMMapUpgradeInfo, OnFMDownloadProgressListener)}
     * 会自动下载更新地图，更新完成后会加载地图;否则return false。
     */
    @Override
    public boolean onUpgrade(FMMapUpgradeInfo upgradeInfo) {
        //TODO 获取到最新地图更新的信息，可以进行地图的下载操作
        return false;
    }


    /**
     * 清除图片标注
     */
    private void clearImageLayer() {
        //清理起点图层
        if (mStartImageLayer != null) {
            mStartImageLayer.removeAll();
            mStartImageLayer = null;
        }

        //清理终点图层
        if (mEndImageLayer != null) {
            mEndImageLayer.removeAll();
            mEndImageLayer = null;
        }
    }


    /**
     * 更新真实定位点
     *
     * @param mapCoord 坐标
     */
    private void updateRealMarker(FMMapCoord mapCoord) {
        if (mRealMarker == null) {
            mRealMarker = ViewHelper.buildLocationMarker(mFMMap.getFocusGroupId(),
                    mapCoord, R.mipmap.looking_men);
            mLocationLayer.addMarker(mRealMarker);
        } else {
            mRealMarker.updatePosition(mapCoord);
        }
    }

    /**
     * 更新处理过定位点
     *
     * @param coord 坐标
     */
    private void updateHandledMarker(FMMapCoord coord) {
        if (mHandledMarker == null) {
            mHandledMarker = ViewHelper.buildLocationMarker(mFMMap.getFocusGroupId(),
                    coord, R.mipmap.looking_men);
            mLocationLayer.addMarker(mHandledMarker);
        } else {
            FMMapCoord mapCoord = makeConstraint(coord);
            mHandledMarker.updatePosition(mapCoord);
        }
        //上次真实行走坐标
        mLastMoveCoord = coord.clone();

        moveToCenter(mLastMoveCoord);
    }

    /**
     * 移动至中心点,如果中心与屏幕中心点距离大于20米，将移动
     *
     * @param mapCoord 坐标
     */
    private void moveToCenter(final FMMapCoord mapCoord) {
        FMMapCoord centerCoord = mFMMap.getMapCenter();
        double length = FMMath.length(centerCoord, mapCoord);
        if (length > MAX_BETWEEN_LENGTH) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mFMMap.moveToCenter(mapCoord, true);
                }
            });
        }
    }

    /**
     * 路径约束
     *
     * @param mapCoord 地图坐标点
     * @return
     */
    private FMMapCoord makeConstraint(FMMapCoord mapCoord) {
        FMMapCoord currentCoord = mapCoord.clone();
        int groupId = mFMMap.getFocusGroupId();
        //获取当层行走结果集合
        ArrayList<FMMapCoord> coords = ConvertUtils.getMapCoords(mNaviAnalyser.getNaviResults(), groupId);
        //路径约束
        mNaviAnalyser.naviConstraint(groupId, coords, mLastMoveCoord, currentCoord);
        return currentCoord;
    }

    @Override
    public void onAnimationStart() {

    }

    @Override
    public void onAnimationUpdate(FMMapCoord mapCoord, double distance, double angle) {
        updateRealMarker(mapCoord);

        FMMapCoord cloneCoord = mapCoord.clone();
        updateHandledMarker(cloneCoord);
    }

    @Override
    public void onAnimationEnd() {
        // 已经行走过终点
        if (isWalkComplete()) {
            //   setStartAnimationEnable(true);
            return;
        }

        groupIdShow2 = getWillWalkingGroupId();

         //   setFocusGroupId(groupIdShow2);
        //    Log.e("test", groupIdShow2 + "--楼层与点集合1-----");
    }

    /**
     * 判断是否行走到终点
     *
     * @return
     */
    protected boolean isWalkComplete() {
        if (mCurrentIndex > mNaviGroupIds.size() - 1) {
            return true;
        }
        return false;
    }

//    /**
//     * 设置动画按钮是否可以使用
//     *
//     * @param enable true 可以执行, false 不可以执行
//     */
//    protected void setStartAnimationEnable(final boolean enable) {
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                ViewHelper.setViewEnable(ShowFMapActivity.this, R.id.btn_start_navigation, enable);
//            }
//        });
//    }

    /**
     * 获取即将行走的下一层groupId
     *
     * @return
     */
    protected int getWillWalkingGroupId() {
        if (mCurrentIndex > mNaviGroupIds.size() - 1) {
            return mFMMap.getFocusGroupId();
        } else {
            return mNaviGroupIds.get(mCurrentIndex);
        }
    }

    /**
     * 切换楼层行走
     *
     * @param groupId 楼层id
     */
    protected void setFocusGroupId(int groupId) {
        if (groupId != mFMMap.getFocusGroupId()) {
            mFMMap.setFocusByGroupId(groupId, null);
            mHandler.sendEmptyMessage(WHAT_LOCATE_SWITCH_GROUP);
        }
        updateLineMarker(groupId);
        setupTargetLine(groupId);
    }

    /**
     * 开始模拟行走路线
     *
     * @param groupId 楼层id
     */
    protected void setupTargetLine(int groupId) {
        ArrayList<FMMapCoord> points = getWillWalkingPoints();
        mLocationAPI.setupTargetLine(points, groupId);
        mLocationAPI.start();
    }

    /**
     * 获取即将行
     * 走的下一层点集合
     *
     * @return
     */
    protected ArrayList<FMMapCoord> getWillWalkingPoints() {
        if (mCurrentIndex > mNaviGroupIds.size() - 1) {
            return null;
        }
        return mNaviPoints.get(mCurrentIndex++);
    }

    private void updateLocateGroupView() {
        String groupName = ConvertUtils.convertToFloorName(mFMMap, mFMMap.getFocusGroupId());
        ViewHelper.setViewText(ShowFMapActivity.this, R.id.btn_group_control, groupName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.iv_map_calel:    //返回键
//                backOnDestory();
//                break;
//
//            case R.id.tv_relocation:    //重新定位
//
//
//                break;

            case R.id.btn_start_navigation:    //是否下一步以及完成的点击

                if (mNaviGroupIds != null) {

                    if (mNaviGroupIds.size() == 1) {
                        backOnDestory();
                    } else {
                        if (stCoord != null && endCoord != null) {
                            //地图返回成功以后才可以加载路径规划
                            if(stCoord.getMapCoord().x != endCoord.getMapCoord().x){
                                if (mLocationAPI != null) {
                                    mLocationAPI.stop();
                                }

                                if (!flagFocus) {
                                    flagFocus = true;
                                    // 跳转至下一层
                                    setFocusGroupId(mNaviGroupIds.get(1));
                                    mStartNav.setText("返回到F" + mNaviGroupIds.get(0) + "层");

                                } else {
                                    flagFocus = false;
                                    //
                                    mStartNav.setText("您当前在F" + mNaviGroupIds.get(0) + "层， 请继续前往F" + mNaviGroupIds.get(1) + "层");
                                    startWalkingRouteLine();
                                }

                            }else{

                                backOnDestory();
                            }
                        }
                    }
                }

                break;
        }

    }

    /**
     * 关闭当前页面
     */
    private void backOnDestory() {
        if (mLocationAPI != null) {
            mLocationAPI.destroy();
        }

        if (mFMMap != null) {
            mFMMap.onDestroy();
        }

        finish();
    }


    /**
     * 当有多个楼层的时候，点击切换不同的楼层选择
     */
    private boolean flagFocus = false;


    /**
     * 更新当层显示的导航线
     *
     * @param
     */
    private void updateLineMarker(int groupId) {
        clearLineLayer();

        //当前层非焦点层,不显示
        if (mFMMap.getFocusGroupId() != groupId) {
            return;
        }

        int index = mNaviGroupIds.indexOf(groupId);
        ArrayList<FMMapCoord> points = mNaviPoints.get(index);

        if (points == null) {
            return;
        }
        ArrayList<FMSegment> segments = new ArrayList<>();
        FMSegment s = new FMSegment(groupId, points);
        segments.add(s);

        //添加LineMarker
        FMLineMarker lineMarker = ViewHelper.buildLineMarker(segments);
        mLineLayer.addMarker(lineMarker);

    }


    @Override
    public void onBackImageClick() {
        backOnDestory();
    }

    @Override
    public void onRightTextViewClick() {
        backOnDestory();
        Intent inten = new Intent(ShowFMapActivity.this, SearchFMapActivity.class);
        inten.putExtra("flag", "flag");
        startActivity(inten);
    }
}
