package com.zgy.parking.indoor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import com.fengmap.android.FMErrorMsg;
import com.fengmap.android.FMMapSDK;
import com.fengmap.android.analysis.navi.FMNaviAnalyser;
import com.fengmap.android.analysis.search.FMSearchAnalyser;
import com.fengmap.android.analysis.search.FMSearchResult;
import com.fengmap.android.analysis.search.facility.FMSearchFacilityByTypeRequest;
import com.fengmap.android.data.OnFMDownloadProgressListener;
import com.fengmap.android.exception.FMObjectException;
import com.fengmap.android.map.FMMap;
import com.fengmap.android.map.FMMapUpgradeInfo;
import com.fengmap.android.map.FMMapView;
import com.fengmap.android.map.FMViewMode;
import com.fengmap.android.map.event.OnFMMapInitListener;
import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.layer.FMImageLayer;
import com.fengmap.android.map.marker.FMImageMarker;
import com.fengmap.android.map.marker.FMModel;
import com.zgy.parking.indoor.R;
import com.zgy.parking.indoor.adapter.SearchBarAdapter;
import com.zgy.parking.indoor.adapter.ViewHelper;
import com.zgy.parking.indoor.bean.MapCoord;
import com.zgy.parking.indoor.bean.MapNumber;
import com.zgy.parking.indoor.bean.NumberComparator;
import com.zgy.parking.indoor.bean.ParkingRecordInfo;
import com.zgy.parking.indoor.utils.AnalysisUtils;
import com.zgy.parking.indoor.utils.FileUtil;
import com.zgy.parking.indoor.utils.KeyBoardUtils;
import com.zgy.parking.indoor.utils.SearchBar;
import com.zgy.parking.indoor.utils.ToastUtil;
import com.zgy.parking.indoor.view.UITitleBackView;


import org.litepal.crud.DataSupport;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



/**
 * 作者：lenovo on 2017/1/11
 */

public class SearchFMapActivity extends BaseActivity implements SearchBar.OnSearchResultCallback,
        AdapterView.OnItemClickListener, OnFMMapInitListener, UITitleBackView.onBackImageClickListener, UITitleBackView.OnRightTextViewClickListener {

    FMMap mFMMap;

    @BindView(R.id.tv_parking_search_car)
    TextView mSearchCar;

    @BindView(R.id.mapview_search_fmap)
    FMMapView mapView;

    @BindView(R.id.search_map_uib)
    UITitleBackView mUib;

    private SearchBar mSearchBar;
    protected HashMap<Integer, FMImageLayer> mImageLayers = new HashMap<>();
    private FMModel mLastClicked;
    /**
     * 导航分析
     */
    protected FMNaviAnalyser mNaviAnalyser;
    /**
     * 搜索分析
     */
    protected FMSearchAnalyser mSearchAnalyser;
    private SearchBarAdapter mSearchAdapter;

    private String flagCar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_fmap);
        setStatusBlack(this);
        ButterKnife.bind(this);

        initHeader();

        mFMMap = mapView.getFMMap();       //获取地图操作对象
        flagCar = getIntent().getStringExtra("flag");

//        //加载离线数据
        String path = FileUtil.getDefaultMapPath(this);
    //    Log.e("test", "进行地图加载成功1==" + path);
    //    Log.e("test", "进行地图加载成功2==" + FMMapSDK.getSDKKey());
    //    Log.e("test", "进行地图加载成功3==" + FMMapSDK.getSha1Value());
        if (!TextUtils.isEmpty(path)) {
            mFMMap.openMapByPath(path);
        }

        mFMMap.setOnFMMapInitListener(this);
        //  mFMMap.showCompass();                   //显示指北针//
              mFMMap.hiddenCompass();                 //隐藏指北针
        //搜索框
        mSearchBar = ViewHelper.getView(SearchFMapActivity.this, R.id.search_title_bar_map);
        mSearchBar.setOnSearchResultCallback(this);
        mSearchBar.setOnItemClickListener(this);



    }

    private void initHeader() {
        mUib.setBackImageVisiale(true);
        mUib.setOnBackImageClickListener(this);
        mUib.setTitleText("附近停车场");
        mUib.setRightContentVisbile(true);
        mUib.setRigthTextView("确定");
        mUib.setOnRightTextViewClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (mFMMap != null) {
            mFMMap.onDestroy();
        }
        super.onBackPressed();
    }

    private FMModel model;
    private int groupId;

    private FMMapCoord mapCoord;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //关闭软键盘
        KeyBoardUtils.closeKeybord(mSearchBar.getCompleteText(), SearchFMapActivity.this);
        model = (FMModel) parent.getItemAtPosition(position);

        //切换楼层
        groupId = model.getGroupId();

        if (groupId != mFMMap.getFocusGroupId()) {
            mFMMap.setFocusByGroupId(groupId, null);
        }

        //移动至中心点
        mapCoord = model.getCenterMapCoord();

        mFMMap.moveToCenter(mapCoord, false);
        clearImageMarker();
//        //添加图片
        FMImageMarker imageMarker = ViewHelper.buildImageMarker(getResources(), mapCoord);
        mImageLayers.get(model.getGroupId()).addMarker(imageMarker);
        clearFocus(model);


    }


    @Override
    public void onSearchCallback(String keyword) {
        //地图未显示前，不执行搜索事件
        boolean isCompleted = mFMMap.getMapFirstRenderCompleted();
        if (!isCompleted) {
            return;
        }

        ArrayList<FMModel> models = AnalysisUtils.queryModelByKeyword(mFMMap, mSearchAnalyser, keyword);
        if (mSearchAdapter == null) {
            mSearchAdapter = new SearchBarAdapter(SearchFMapActivity.this, models);
            mSearchBar.setAdapter(mSearchAdapter);
        } else {
            mSearchAdapter.setDatas(models);
            mSearchAdapter.notifyDataSetChanged();
        }


    }

    /**
     * 清除图片标志
     */
    private void clearImageMarker() {
        for (FMImageLayer imageLayer : mImageLayers.values()) {
            imageLayer.removeAll();
        }

    }


    /**
     * 清除模型的聚焦效果
     *
     * @param model 模型
     */
    private void clearFocus(FMModel model) {
        if (!model.equals(mLastClicked)) {
            if (mLastClicked != null) {
                mLastClicked.setSelected(false);
            }
            this.mLastClicked = model;
            this.mLastClicked.setSelected(true);
        }
    }

    @Override
    public void onMapInitSuccess(String path) {
        //加载离线主题
    //    mFMMap.loadThemeByPath(FileUtil.getDefaultThemePath(this));
        mFMMap.setFMViewMode(FMViewMode.FMVIEW_MODE_2D); //设置地图2D显示模式
     //   Log.e("test", "进行地图加载成功==" + path);
        //得到搜索分析器
        try {
            mSearchAnalyser = FMSearchAnalyser.getFMSearchAnalyserByPath(path);
        } catch (FileNotFoundException pE) {
            pE.printStackTrace();
        } catch (FMObjectException pE) {
            pE.printStackTrace();
        }

        //图片图层
        int groupSize = mFMMap.getFMMapInfo().getGroupSize();
        for (int i = 0; i < groupSize; i++) {
            int groupId = mFMMap.getMapGroupIds()[i];
            FMImageLayer imageLayer = mFMMap.getFMLayerProxy().createFMImageLayer(groupId);
            mFMMap.addLayer(imageLayer);
            mImageLayers.put(groupId, imageLayer);
        }

        //导航分析
        try {
            mNaviAnalyser = FMNaviAnalyser.getFMNaviAnalyserByPath(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (FMObjectException e) {
            e.printStackTrace();
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
        String message = FMErrorMsg.getErrorMsg(errorCode);
   //     Log.e("test",path + "进行地图加载失败处理失败原因"+"==" + errorCode);
   //     Log.e("test","进行地图加载失败处理失败原因message"+"==" + message);

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
     * 公共设施搜索
     *
     * @param type 类型值
     * @return
     */
    private ArrayList<FMMapCoord> queryFacilityByType(int type) {
        ArrayList<FMMapCoord> list = new ArrayList<FMMapCoord>();
        int[] groupIds = {mFMMap.getFocusGroupId()};
        //对公共设施类型执行搜索
        FMSearchFacilityByTypeRequest request = new FMSearchFacilityByTypeRequest(groupIds, type);
        ArrayList<FMSearchResult> result = mSearchAnalyser.executeFMSearchRequest(request);
        //返回值获取坐标
        for (FMSearchResult r : result) {
            int eid = (int) r.get("eid");           //获取公共设施的eid
            int groupId = (int) r.get("groupId");   //获取公共设施的groupId
            FMMapCoord mapCoord = mSearchAnalyser.getFacilityCoord(groupId, eid);
            if (mapCoord != null) {
                list.add(mapCoord);
            }
        }
        return list;
    }

    /**
     * 获取公司设施的距离
     *
     * @param
     */
    private List<MapNumber> displayImageMarkers(ArrayList<FMMapCoord> coords, FMModel model) {
        //   clearImageMarker();
        List<MapNumber> list = new ArrayList<MapNumber>();
        int endGroupId = mFMMap.getFocusGroupId();
        //展示图片标注
        for (FMMapCoord endCoord : coords) {
            //    LogUtils.e("test","获取到的公共设置楼梯"+"==1   "+endCoord.toString());

            if (model != null) {
                int stGroupId = model.getGroupId();
                FMMapCoord stCoord = model.getCenterMapCoord();
                //      LogUtils.e("test","获取到的公共设置楼梯"+"==2   "+stGroupId);
                //       LogUtils.e("test","获取到的公共设置楼梯"+"==3  "+stCoord.toString());
                //这里第三个值是终点楼层
                Double sceneRoute = analyzeNavigation(stGroupId, stCoord, endGroupId, endCoord);
                MapNumber mapNumber = new MapNumber();
                //把搜索到的公共设施信息放入准备好的容器中
                mapNumber.setMapCoord(endCoord);
                mapNumber.setSceneRoute(sceneRoute);
                mapNumber.setEndGroupId(endGroupId);
                list.add(mapNumber);
            }
        }
        return list;

    }


    /**
     * 开始分析导航，计算起始点到公共设施的距离
     */
    private Double analyzeNavigation(int stGroupId, FMMapCoord stCoord, int endGroupId, FMMapCoord endCoord) {
        int type = mNaviAnalyser.analyzeNavi(stGroupId, stCoord, endGroupId, endCoord,
                FMNaviAnalyser.FMNaviModule.MODULE_SHORTEST);
        double sceneRouteLength = 0.00;
        if (type == FMNaviAnalyser.FMRouteCalcuResult.ROUTE_SUCCESS) {
            //  addLineMarker();
            //行走总距离
            sceneRouteLength = mNaviAnalyser.getSceneRouteLength();
        }

        return sceneRouteLength;

    }


    private Handler mHandler = new Handler();

    @Override
    public void onBackImageClick() {
        if (mFMMap != null) {
            mFMMap.onDestroy();
        }
        finish();
    }




    @Override
    public void onRightTextViewClick() {

        if (mFMMap != null) {

            if (model != null) {



                ParkingRecordInfo recordInfo = DataSupport.findFirst(ParkingRecordInfo.class);
                if (recordInfo == null) {
                    recordInfo = new ParkingRecordInfo();
                }

                recordInfo.setParkingName(model.getName());
                //判断是哪一个进入搜索页面的，如果搜索爱车的就直接去地图路径导航页面
                if (!TextUtils.isEmpty(flagCar)) {

                    recordInfo.setFloor2(model.getGroupId() + "");
                    recordInfo.setParkingNum2(model.getName());
                    recordInfo.save();

                    ShowFMapActivity.stCoord1 = new MapCoord(groupId, mapCoord);
                    MapCoord coord = ShowFMapActivity.endCoord;

                    //    LogUtils.e("test", "coord.getMapCoord().x" + coord.getMapCoord().x + mapCoord.x + "--yyy--" + coord.getMapCoord().y + mapCoord.y + "---zzz-");
                    if (coord != null) {
                        if (!coord.getMapCoord().equals(mapCoord)) {
                            Intent inten = new Intent(SearchFMapActivity.this, ShowFMapActivity.class);
                            startActivity(inten);

                            finish();
                            mFMMap.onDestroy();
                        } else {
                            Toast.makeText(SearchFMapActivity.this, "您选择的车位起点和终点一样哦，请重新选择", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {

                    recordInfo.setFloor1(model.getGroupId() + "");
                    recordInfo.setParkingNum1(model.getName());
                    recordInfo.save();


                    int type = FileUtil.DEFAULT_ZHITI;
                    final ArrayList<FMMapCoord> coords = queryFacilityByType(type);
                    if (coords != null) {

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                List<MapNumber> list = displayImageMarkers(coords, model);
                                if (list.size() != 0) {
                                    Collections.sort(list, new NumberComparator());

                                    //设置最近电梯口为结束的点
                                    ShowFMapActivity.endCoord1 = new MapCoord(list.get(0).getEndGroupId(), list.get(0).getMapCoord());
                                }
                            }
                        });

                    }

                    ShowFMapActivity.stOrendCoord = new MapCoord(model.getGroupId(), mapCoord);
                    finish();
                    mFMMap.onDestroy();
                }

            }


        } else {

            ToastUtil.showMessage(SearchFMapActivity.this, "您没有选择停车位！");
        }

    }
}
