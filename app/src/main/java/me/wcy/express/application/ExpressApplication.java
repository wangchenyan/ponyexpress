package me.wcy.express.application;

import android.app.Application;

import im.fir.sdk.FIR;

/**
 * Created by wcy on 2016/4/3.
 */
public class ExpressApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FIR.init(this);
    }
}
