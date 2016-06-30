package com.google.zxing.encoding;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

/**
 * @author Ryan Tang
 */
public final class EncodingHandler {
    private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xffffffff;

    /**
     * 根据字符串生成二维码图片
     *
     * @param str    要生成的字符串
     * @param length 生成的图片边长
     * @return 生成的图片
     * @throws WriterException
     */
    public static Bitmap createQRCode(String str, int length) throws WriterException {
        Hashtable<EncodeHintType, String> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix matrix = new QRCodeWriter().encode(str, BarcodeFormat.QR_CODE, length, length, hints);
        int[] pixels = new int[length * length];
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < length; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * length + x] = BLACK;
                } else {
                    pixels[y * length + x] = WHITE;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, length, 0, 0, length, length);
        return bitmap;
    }
}
