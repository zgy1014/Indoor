package com.zgy.parking.indoor.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.zgy.parking.indoor.R;
import com.zgy.parking.indoor.bean.PhotoImageInfo;

import java.util.List;

/**
 * 作者：lenovo on 2016/12/6
 */
public class PhotoImageAdapter extends PagerAdapter {

    protected Context mContext;
    private List<PhotoImageInfo> mList;

    public PhotoImageAdapter(Context context, List<PhotoImageInfo> mList){
        this.mContext = context;
        this.mList = mList;
    }

    public void setList(List<PhotoImageInfo> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mList != null)
            return mList.size();
        else
            return 0;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View     view = View.inflate(mContext, R.layout.item_photo_img, null);
        ImageView  image=(ImageView)view.findViewById(R.id.iv_photo_imageview);
        TextView mPhotoDate=(TextView)view.findViewById(R.id.tv_photo_date);
        TextView  mPhotoSreet=(TextView)view.findViewById(R.id.tv_photo_street);
        PhotoImageInfo mInfo= mList.get(position);
        Glide.with(mContext)
                .load(mInfo.getCover())
                .into(image);

        if (!TextUtils.isEmpty(mInfo.getLocation())) {
            mPhotoSreet.setText(mInfo.getLocation());
        }
        if(!TextUtils.isEmpty(mInfo.getTime())){
            mPhotoDate.setText(mInfo.getTime());
        }
        container.addView(view);
        return view;
    }




}
