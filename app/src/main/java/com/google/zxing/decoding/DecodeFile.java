package com.google.zxing.decoding;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.activity.Callback;
import com.google.zxing.common.HybridBinarizer;

import java.util.Hashtable;

public class DecodeFile {

    @SuppressLint("StaticFieldLeak")
    public static void decodeFile(ContentResolver resolver, Uri uri, Hashtable<DecodeHintType, Object> hints, final Callback<Result> callback) {
        new AsyncTask<Object, Void, Result>() {
            @Override
            protected Result doInBackground(Object... params) {
                ContentResolver resolver = (ContentResolver) params[0];
                Uri uri = (Uri) params[1];
                Hashtable<DecodeHintType, Object> hints = (Hashtable<DecodeHintType, Object>) params[2];

                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(resolver.openInputStream(uri), null, options);
                    int inSampleSize = options.outHeight / 500;
                    inSampleSize = Math.max(inSampleSize, 1);
                    options.inSampleSize = inSampleSize;
                    options.inJustDecodeBounds = false;
                    Bitmap bitmap = BitmapFactory.decodeStream(resolver.openInputStream(uri), null, options);

                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    int[] pixels = new int[width * height];
                    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                    RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                    BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
                    MultiFormatReader reader = new MultiFormatReader();
                    return reader.decode(binaryBitmap, hints);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Result result) {
                if (callback != null) {
                    callback.onEvent(result);
                }
            }
        }.execute(resolver, uri, hints);
    }
}
