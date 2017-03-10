package com.zgy.parking.indoor.bean;

import com.fengmap.android.map.marker.FMModel;

/**
 * @Email hezutao@fengmap.com
 * @Version 2.0.0
 * @Description 对楼层id和FMMapCoord封装的实体类
 */
public class MapCoordShow {

    private FMModel model;
    private MapNumber mapNumber;

    public FMModel getModel() {
        return model;
    }

    public void setModel(FMModel model) {
        this.model = model;
    }

    public MapNumber getMapNumber() {
        return mapNumber;
    }

    public void setMapNumber(MapNumber mapNumber) {
        this.mapNumber = mapNumber;
    }
}
