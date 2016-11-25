package me.wcy.express.utils.permission;

public interface PermissionResult {
    void onGranted();

    void onDenied();
}
