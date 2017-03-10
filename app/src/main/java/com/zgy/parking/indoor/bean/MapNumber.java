package com.zgy.parking.indoor.bean;

import com.fengmap.android.map.geometry.FMMapCoord;

/**
 * @Email hezutao@fengmap.com
 * @Version 2.0.0
 * @Description
 */
public class MapNumber {

    private Double sceneRoute;
    private FMMapCoord mapCoord;
    private int endGroupId;

    public Double getSceneRoute() {
        return sceneRoute;
    }

    public void setSceneRoute(Double sceneRoute) {
        this.sceneRoute = sceneRoute;
    }

    public FMMapCoord getMapCoord() {
        return mapCoord;
    }

    public void setMapCoord(FMMapCoord mapCoord) {
        this.mapCoord = mapCoord;
    }

    public int getEndGroupId() {
        return endGroupId;
    }

    public void setEndGroupId(int endGroupId) {
        this.endGroupId = endGroupId;
    }

    @Override
    public String toString() {
        return "MapNumber{" +
                "sceneRoute=" + sceneRoute +
                ", mapCoord=" + mapCoord +
                ", endGroupId=" + endGroupId +
                '}';
    }
}
