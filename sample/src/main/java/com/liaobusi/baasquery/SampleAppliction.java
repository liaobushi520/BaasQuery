package com.liaobusi.baasquery;

import android.app.Application;

import com.droi.sdk.core.Core;

/**
 * Created by liaozhongjun on 2017/10/19.
 */

public class SampleAppliction extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Core.initialize(this);
    }
}
