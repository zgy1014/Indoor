package com.zgy.parking.indoor.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.fengmap.android.map.geometry.FMMapCoord;
import com.fengmap.android.map.marker.FMImageMarker;
import com.fengmap.android.map.marker.FMLineMarker;
import com.fengmap.android.map.marker.FMLocationMarker;
import com.fengmap.android.map.marker.FMSegment;
import com.zgy.parking.indoor.R;

import java.util.ArrayList;

/**
 * @Email hezutao@fengmap.com
 * @Version 2.0.0
 * @Description 控件控制帮助类
 */
public class ViewHelper {

    /**
     * 获取控件
     *
     * @param activity Activity
     * @param id       控件id
     * @param <T>
     * @return
     */
    public static <T extends View> T getView(Activity activity, int id) {
        return (T) activity.findViewById(id);
    }

    /**
     * 获取控件
     *
     * @param view 视图
     * @param id   控件id
     * @param <T>
     * @return
     */
    public static <T extends View> T getView(View view, int id) {
        return (T) view.findViewById(id);
    }

    /**
     * 设置控件的点击事件
     *
     * @param activity Activity
     * @param id       控件id
     * @param listener 点击监听事件
     */
    public static void setViewClickListener(Activity activity, int id, View.OnClickListener listener) {
        View view = getView(activity, id);
        view.setOnClickListener(listener);
    }

    /**
     * 设置控件文字
     *
     * @param activity Activity
     * @param id       控件id
     * @param text     文字
     */
    public static void setViewText(Activity activity, int id, String text) {
        TextView view = getView(activity, id);
        view.setText(text);
    }

    /**
     * 设置控件的状态改变事件
     *
     * @param activity Activity
     * @param id       控件id
     * @param listener CheckBox选中状态改变事件
     */
    public static void setViewCheckedChangeListener(Activity activity, int id,
                                                    CompoundButton.OnCheckedChangeListener listener) {
        CheckBox view = getView(activity, id);
        view.setOnCheckedChangeListener(listener);
    }

    /**
     * 设置控件的点击事件
     *
     * @param activity   Activity
     * @param id         控件id
     * @param visibility 控件显示状态
     */
    public static void setViewVisibility(Activity activity, int id, int visibility) {
        View view = getView(activity, id);
        view.setVisibility(visibility);
    }

    /**
     * 添加图片标注
     *
     * @param resources 资源
     * @param mapCoord  坐标
     * @param resId     资源id
     */
    public static FMImageMarker buildImageMarker(Resources resources, FMMapCoord mapCoord, int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(resources, resId);
        FMImageMarker imageMarker = new FMImageMarker(mapCoord, bitmap);
        //设置图片宽高
        imageMarker.setMarkerWidth(50);
        imageMarker.setMarkerHeight(50);
        //设置图片在模型之上
        imageMarker.setFMImageMarkerOffsetMode(FMImageMarker.FMImageMarkerOffsetMode.FMNODE_MODEL_ABOVE);
        return imageMarker;
    }

    /**
     * 添加图片标注
     *
     * @param resources 资源
     * @param mapCoord  坐标点
     */
    public static FMImageMarker buildImageMarker(Resources resources, FMMapCoord mapCoord) {
        return buildImageMarker(resources, mapCoord, R.mipmap.ic_marker_blue);
    }

    /**
     * 创建定位标注
     *
     * @param groupId  定位标注所在楼层
     * @param mapCoord 定位标注坐标
     * @return
     */
    public static FMLocationMarker buildLocationMarker(int groupId, FMMapCoord mapCoord) {
        FMLocationMarker mLocationMarker = new FMLocationMarker(groupId, mapCoord);
        //设置定位点图片
        mLocationMarker.setActiveImageFromAssets("active.png");
        //设置定位图片宽高
        mLocationMarker.setMarkerWidth(90);
        mLocationMarker.setMarkerHeight(90);
        return mLocationMarker;
    }


    /**
     * 创建线
     *
     * @param segments 线段集合
     * @return
     */
    public static FMLineMarker buildLineMarker(ArrayList<FMSegment> segments) {
        FMLineMarker lineMarker = new FMLineMarker(segments);
        lineMarker.setLineWidth(3.0f);
        return lineMarker;
    }

    /**
     * 创建定位标注
     *
     * @param groupId  楼层id
     * @param mapCoord 坐标点
     * @param resID    资源文件
     * @return
     */
    public static FMLocationMarker buildLocationMarker(int groupId, FMMapCoord mapCoord, int resID) {
        FMLocationMarker locationMarker = new FMLocationMarker(groupId, mapCoord);
        //设置定位点图片
        locationMarker.setActiveImageFromRes(resID);
        //设置定位图片宽高
        locationMarker.setMarkerWidth(50);
        locationMarker.setMarkerHeight(50);
        return locationMarker;
    }

    /**
     * 设置控件是否可用
     *
     * @param activity Activity
     * @param id       控件id
     * @param enabled  true 可以使用 false 不可使用
     */
    public static void setViewEnable(Activity activity, int id, boolean enabled) {
        View view = getView(activity, id);
        view.setEnabled(enabled);
    }

}
