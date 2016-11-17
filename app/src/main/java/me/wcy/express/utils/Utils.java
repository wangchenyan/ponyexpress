/**
 *
 */
package me.wcy.express.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import java.io.File;

import me.wcy.express.constants.Constants;
import me.wcy.express.model.SearchInfo;

/**
 * @author wcy
 */
public class Utils {

    public static String formatSearchUrl(SearchInfo searchInfo) {
        return String.format(Constants.URL_SEARCH, searchInfo.getCode(), searchInfo.getPost_id());
    }

    public static String formatLogoUrl(String logo) {
        return String.format(Constants.URL_LOGO, logo);
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

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * /sdcard/Pictures/
     */
    public static String getPictureDir() {
        String pictureDir = Environment.getExternalStorageDirectory() + "/Pictures/";
        File file = new File(pictureDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return pictureDir;
    }
}
