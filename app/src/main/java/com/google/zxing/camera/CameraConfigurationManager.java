/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.camera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.util.List;

public final class CameraConfigurationManager {
    private static final String TAG = "CameraConfigurationMana";
    private final Context context;
    private Point screenResolution;
    private Point cameraResolution;

    CameraConfigurationManager(Context context) {
        this.context = context;
    }

    /**
     * Reads, one time, values from the camera that are needed by the app.
     */
    void initFromCameraParameters(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        screenResolution = new Point(display.getHeight(), display.getWidth());
        Log.d(TAG, "Screen resolution: " + screenResolution);
        cameraResolution = getCameraResolution(parameters, screenResolution);
        Log.d(TAG, "Camera resolution: " + cameraResolution);
    }

    /**
     * Sets the camera up to take preview images which are used for both preview
     * and decoding. We detect the preview format here so that
     * buildLuminanceSource() can build an appropriate LuminanceSource subclass.
     * In the future we may want to force YUV420SP as it's the smallest, and the
     * planar Y can be used for barcode scanning without a copy in some cases.
     */
    void setDesiredCameraParameters(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
        Log.d(TAG, "Setting preview size: " + cameraResolution);
        setZoom(parameters);
        camera.setDisplayOrientation(getDisplayOrientation());
        camera.setParameters(parameters);
    }

    Point getCameraResolution() {
        return cameraResolution;
    }

    Point getScreenResolution() {
        return screenResolution;
    }

    private static Point getCameraResolution(Camera.Parameters parameters, Point screenResolution) {
        Point cameraResolution = null;

        List<Camera.Size> cameraSizeList = parameters.getSupportedPreviewSizes();
        if (cameraSizeList != null) {
            Camera.Size bestSize = findBestPreviewSize(cameraSizeList, screenResolution);
            if (bestSize != null) {
                cameraResolution = new Point(bestSize.width, bestSize.height);
            }
        }

        if (cameraResolution == null) {
            // Ensure that the camera resolution is a multiple of 8, as the screen may not be.
            cameraResolution = new Point((screenResolution.x >> 3) << 3, (screenResolution.y >> 3) << 3);
        }

        return cameraResolution;
    }

    private static Camera.Size findBestPreviewSize(List<Camera.Size> cameraSizeList, Point screenResolution) {
        int diff = Integer.MAX_VALUE;
        Camera.Size bestSize = null;
        for (Camera.Size size : cameraSizeList) {
            int width = size.width;
            int height = size.height;

            int newDiff = Math.abs(width - screenResolution.x) + Math.abs(height - screenResolution.y);
            if (newDiff == 0) {
                return size;
            } else if (newDiff < diff) {
                bestSize = size;
                diff = newDiff;
            }
        }
        return bestSize;
    }

    private void setZoom(Camera.Parameters parameters) {
        if (!parameters.isZoomSupported()) {
            return;
        }

        int maxZoom = parameters.getMaxZoom();
        int bestZoom = maxZoom / 10;
        parameters.setZoom(bestZoom);
    }

    /**
     * https://developer.android.com/reference/android/hardware/Camera.html#setDisplayOrientation(int)
     */
    public int getDisplayOrientation() {
        Camera.CameraInfo info = getBackCameraInfo();
        if (info != null) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int rotation = windowManager.getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break;
                case Surface.ROTATION_90:
                    degrees = 90;
                    break;
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;
            }

            int result;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;  // compensate the mirror
            } else {  // back-facing
                result = (info.orientation - degrees + 360) % 360;
            }
            return result;
        }

        return 90;
    }

    private Camera.CameraInfo getBackCameraInfo() {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return cameraInfo;
            }
        }
        return null;
    }
}
