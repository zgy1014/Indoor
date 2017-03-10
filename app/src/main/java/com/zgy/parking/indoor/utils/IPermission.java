package com.zgy.parking.indoor.utils;

import java.util.List;

/**
 * Created by Limuyang on 2016/12/29.
 */

public interface IPermission {

    void onGranted();

    void onDenied(List<String> deniedPermission);
}
