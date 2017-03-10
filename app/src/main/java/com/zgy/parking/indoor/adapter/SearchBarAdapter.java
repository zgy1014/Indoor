package com.zgy.parking.indoor.adapter;

import android.content.Context;
import com.fengmap.android.map.marker.FMModel;
import com.zgy.parking.indoor.R;

import java.util.ArrayList;

/**
 * @Email hezutao@fengmap.com
 * @Version 2.0.0
 * @Description 搜索列表适配
 */
public class SearchBarAdapter extends CommonFilterAdapter<FMModel> {

    public SearchBarAdapter(Context context, ArrayList<FMModel> mapModels) {
        super(context, mapModels, R.layout.layout_item_model_search);
    }

    @Override
    public void convert(ViewHolder viewHolder, FMModel mapNode, int position) {
        viewHolder.setText(R.id.txt_model_name, mapNode.getName());
        viewHolder.setText(R.id.txt_model_group, "F"+mapNode.getGroupId());
    }

}