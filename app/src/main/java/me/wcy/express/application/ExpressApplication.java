package me.wcy.express.application;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import im.fir.sdk.FIR;
import me.wcy.express.request.FakeX509TrustManager;

/**
 * Created by wcy on 2016/4/3.
 */
public class ExpressApplication extends Application {
    private static ExpressApplication sInstance;
    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        FIR.init(this);
        FakeX509TrustManager.allowAllSSL();
    }

    public static ExpressApplication getInstance() {
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }
}
