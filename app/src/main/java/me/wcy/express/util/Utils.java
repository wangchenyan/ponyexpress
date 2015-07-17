/**
 * 
 */
package me.wcy.express.util;

import java.io.UnsupportedEncodingException;

import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

/**
 * @author wcy
 * 
 */
@SuppressWarnings("deprecation")
public class Utils {
	public static final String GB2312 = "GB2312";
	public static final String PICTURE_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/";
	private static final String URL = "http://www.kuaidi100.com/query";

	public static String getQueryUrl(String comParam, String postId) {
		return URL + "?type=" + comParam + "&postid=" + postId;
	}

	public static String getVersion(Context context) {
		PackageManager manager = context.getPackageManager();
		String versionName = "1.0.0";
		try {
			versionName = manager.getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 检查网络连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager localConnectivityManager = (ConnectivityManager) context
				.getSystemService("connectivity");
		int k;
		if (localConnectivityManager != null) {
			NetworkInfo[] arrayOfNetworkInfo = localConnectivityManager
					.getAllNetworkInfo();
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
	 * 根据手机的分辨率从dp的单位转成为px(像素)
	 * 
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 解决二维码包含中文显示乱码的问题
	 * 
	 * @param resultStr
	 * @return
	 */
	public static String formatString(String resultStr) {
		String UTF_Str = "";
		String GB_Str = "";
		boolean is_cN = false;
		try {
			UTF_Str = new String(resultStr.getBytes(HTTP.ISO_8859_1),
					HTTP.UTF_8);
			is_cN = isChineseCharacter(UTF_Str);
			// 防止有人特意使用乱码来生成二维码来判断的情况
			boolean b = isSpecialCharacter(resultStr);
			if (b) {
				is_cN = true;
			}
			if (!is_cN) {
				GB_Str = new String(resultStr.getBytes(HTTP.ISO_8859_1), GB2312);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (is_cN) {
			return UTF_Str;
		} else {
			return GB_Str;
		}
	}

	public static final boolean isChineseCharacter(String chineseStr) {
		char[] charArray = chineseStr.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			// 是否是Unicode编码,除了"�"这个字符.这个字符要另外处理
			if ((charArray[i] >= '\u0000' && charArray[i] < '\uFFFD')
					|| ((charArray[i] > '\uFFFD' && charArray[i] < '\uFFFF'))) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

	public static final boolean isSpecialCharacter(String str) {
		// 是"�"这个特殊字符的乱码情况
		if (str.contains("ï¿½")) {
			return true;
		}
		return false;
	}

}
