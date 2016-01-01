/**
 *
 */
package me.wcy.express.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import java.io.File;

import me.wcy.express.model.ExpressInfo;

/**
 * @author wcy
 */
@SuppressWarnings("deprecation")
public class Utils {
    private static final String URL = "http://www.kuaidi100.com/query";

    public static String getQueryUrl(ExpressInfo expressInfo) {
        return URL + "?type=" + expressInfo.getCompany_param() + "&postid=" + expressInfo.getPost_id();
    }

    public static String getVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        String versionName = "1.0.0";
        try {
            versionName = manager.getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "V " + versionName;
    }

    /**
     * 检查网络连接
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager localConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        int k;
        if (localConnectivityManager != null) {
            NetworkInfo[] arrayOfNetworkInfo = localConnectivityManager.getAllNetworkInfo();
            if (arrayOfNetworkInfo != null) {
                int j = arrayOfNetworkInfo.length;
                for (k = 0; k < j; k++) {
                    if (arrayOfNetworkInfo[k].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据手机的分辨率从dp的单位转成为px
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * /sdcard/Pictures/
     */
    public static String getPictureDir() {
        String pictureDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/";
        File file = new File(pictureDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return pictureDir;
    }
}
