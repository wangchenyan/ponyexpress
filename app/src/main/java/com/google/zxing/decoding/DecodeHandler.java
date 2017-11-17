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

package com.google.zxing.decoding;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.camera.CameraManager;
import com.google.zxing.common.HybridBinarizer;

import java.util.Hashtable;

import me.wcy.express.R;

final class DecodeHandler extends Handler {
    private static final String TAG = "DecodeHandler";
    private final CaptureActivityHandler captureHandler;
    private final MultiFormatReader multiFormatReader;

    DecodeHandler(CaptureActivityHandler captureHandler, Hashtable<DecodeHintType, Object> hints) {
        this.captureHandler = captureHandler;
        multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case R.id.decode:
                decode((byte[]) message.obj, message.arg1, message.arg2);
                break;
            case R.id.quit:
                Looper.myLooper().quit();
                break;
        }
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it
     * took. For efficiency, reuse the same reader objects from one decode to
     * the next.
     *
     * @param data   The YUV preview frame.
     * @param width  The width of the preview frame.
     * @param height The height of the preview frame.
     */
    private void decode(byte[] data, int width, int height) {
        long start = System.currentTimeMillis();
        Result rawResult = null;

        byte[] rotatedData = new byte[data.length];
        int degree = CameraManager.get().getConfigManager().getDisplayOrientation();
        if (degree == 90 || degree == 270) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (degree == 90) {
                        rotatedData[x * height + (height - y - 1)] = data[x + y * width];
                    } else {
                        rotatedData[(width - x - 1) * height + y] = data[x + y * width];
                    }
                }
            }

            int tmp = width;
            width = height;
            height = tmp;
        } else {
            rotatedData = data;
        }

        try {
            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(rotatedData, width, height, 0, 0, width, height, false);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            rawResult = multiFormatReader.decodeWithState(bitmap);
        } catch (Exception e) {
            // continue
        } finally {
            multiFormatReader.reset();
        }

        if (rawResult != null) {
            long end = System.currentTimeMillis();
            Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString());
            Message message = Message.obtain(captureHandler, R.id.decode_succeeded, rawResult);
            message.sendToTarget();
        } else {
            Message message = Message.obtain(captureHandler, R.id.decode_failed);
            message.sendToTarget();
        }
    }
}
