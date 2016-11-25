package me.wcy.express.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import me.wcy.express.utils.permission.PermissionReq;

/**
 * Created by hzwangchenyan on 2016/11/25.
 */
public class PermissionActivity extends AppCompatActivity {
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionReq.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
