package me.wcy.express.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.Locale;

import im.fir.sdk.FIR;
import im.fir.sdk.VersionCheckCallback;
import me.wcy.express.BuildConfig;
import me.wcy.express.R;
import me.wcy.express.activity.AboutActivity;
import me.wcy.express.api.KeyStore;
import me.wcy.express.model.UpdateInfo;

public class UpdateUtils {

    public static void checkUpdate(final Activity activity) {
        FIR.checkForUpdateInFIR(KeyStore.getKey(KeyStore.FIR_KEY), new VersionCheckCallback() {
            @Override
            public void onStart() {
                if (activity instanceof AboutActivity) {
                    SnackbarUtils.show(activity, "正在检查更新");
                }
            }

            @Override
            public void onSuccess(String versionJson) {
                if (activity.isFinishing()) {
                    return;
                }
                Gson gson = new Gson();
                UpdateInfo updateInfo;
                try {
                    updateInfo = gson.fromJson(versionJson, UpdateInfo.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    return;
                }
                int version = Integer.valueOf(updateInfo.version);
                if (version > BuildConfig.VERSION_CODE) {
                    updateDialog(activity, updateInfo);
                } else {
                    if (activity instanceof AboutActivity) {
                        SnackbarUtils.show(activity, "已是最新版本");
                    }
                }
            }

            @Override
            public void onFail(Exception exception) {
            }

            @Override
            public void onFinish() {
            }
        });
    }

    private static void updateDialog(final Activity activity, final UpdateInfo updateInfo) {
        String fileSize = b2mb(updateInfo.binary.fsize) + "MB";
        String message = "v " + updateInfo.versionShort + "(" + fileSize + ")" + "\n\n" + updateInfo.changelog;
        new AlertDialog.Builder(activity)
                .setTitle("发现新版本")
                .setMessage(message)
                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        download(activity, updateInfo);
                    }
                })
                .setNegativeButton("稍后提醒", null)
                .show();
    }

    private static void download(Activity activity, UpdateInfo updateInfo) {
        String fileName = String.format("PonyExpress_%s.apk", updateInfo.versionShort);
        DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(updateInfo.installUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(activity.getString(R.string.app_name));
        request.setDescription("正在更新…");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        request.setMimeType(MimeTypeMap.getFileExtensionFromUrl(updateInfo.installUrl));
        request.allowScanningByMediaScanner();
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);// 不允许漫游
        downloadManager.enqueue(request);
        SnackbarUtils.show(activity, "正在后台下载");
    }

    private static float b2mb(int b) {
        String mb = String.format(Locale.getDefault(), "%.2f", (float) b / 1024 / 1024);
        return Float.valueOf(mb);
    }
}
