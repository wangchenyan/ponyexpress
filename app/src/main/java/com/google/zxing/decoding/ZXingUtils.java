package com.google.zxing.decoding;

import java.io.UnsupportedEncodingException;

/**
 * Created by wcy on 2015/8/17.
 */
public class ZXingUtils {
    public static final String ISO_8859_1 = "ISO-8859-1";
    public static final String UTF_8 = "UTF-8";
    public static final String GB2312 = "GB2312";

    /**
     * 解决二维码包含中文显示乱码的问题
     */
    public static String formatString(String resultStr) {
        String UTF_Str = "";
        String GB_Str = "";
        boolean is_cN = false;
        try {
            UTF_Str = new String(resultStr.getBytes(ISO_8859_1), UTF_8);
            is_cN = isChineseCharacter(UTF_Str);
            // 防止有人特意使用乱码来生成二维码来判断的情况
            boolean b = isSpecialCharacter(resultStr);
            if (b) {
                is_cN = true;
            }
            if (!is_cN) {
                GB_Str = new String(resultStr.getBytes(ISO_8859_1), GB2312);
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

    public static boolean isChineseCharacter(String chineseStr) {
        char[] charArray = chineseStr.toCharArray();
        for (char aCharArray : charArray) {
            // 是否是Unicode编码,除了"�"这个字符.这个字符要另外处理
            if (aCharArray == '\uFFFD' || aCharArray >= '\uFFFF') {
                return false;
            }
        }
        return true;
    }

    public static boolean isSpecialCharacter(String str) {
        // 是"�"这个特殊字符的乱码情况
        return str.contains("ï¿½");
    }

}
