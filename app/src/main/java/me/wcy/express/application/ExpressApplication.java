package me.wcy.express.application;

import android.app.Application;

import im.fir.sdk.FIR;

/**
 * Created by wcy on 2016/4/3.
 */
public class ExpressApplication extends Application {
    private static ExpressApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        FIR.init(this);
    }

    public static ExpressApplication getInstance() {
        return sInstance;
    }
}
