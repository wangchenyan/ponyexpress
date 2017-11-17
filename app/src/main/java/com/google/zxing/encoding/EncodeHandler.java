package com.google.zxing.encoding;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.activity.Callback;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

public final class EncodeHandler {

    /**
     * 根据字符串生成二维码图片
     *
     * @param text   要生成的字符串
     * @param length 生成的图片边长
     */
    @SuppressLint("StaticFieldLeak")
    public static void createQRCode(String text, int length, final Callback<Bitmap> callback) {
        new AsyncTask<Object, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Object... params) {
                String text = (String) params[0];
                int length = (int) params[1];

                try {
                    Hashtable<EncodeHintType, String> hints = new Hashtable<>();
                    hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                    BitMatrix matrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, length, length, hints);
                    int[] pixels = new int[length * length];
                    for (int y = 0; y < length; y++) {
                        for (int x = 0; x < length; x++) {
                            if (matrix.get(x, y)) {
                                pixels[y * length + x] = Color.BLACK;
                            } else {
                                pixels[y * length + x] = Color.WHITE;
                            }
                        }
                    }
                    Bitmap bitmap = Bitmap.createBitmap(length, length, Bitmap.Config.RGB_565);
                    bitmap.setPixels(pixels, 0, length, 0, 0, length, length);
                    return bitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (callback != null) {
                    callback.onEvent(bitmap);
                }
            }
        }.execute(text, length);
    }
}
