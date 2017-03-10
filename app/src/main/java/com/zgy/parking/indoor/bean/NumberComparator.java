package com.zgy.parking.indoor.bean;

import java.util.Comparator;

/**
 * 作者：lenovo on 2017/1/17
 */

public class NumberComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        MapNumber t1=(MapNumber) o1;
        MapNumber t2=(MapNumber) o2;
        return t1.getSceneRoute().compareTo(t2.getSceneRoute());
    }

}
