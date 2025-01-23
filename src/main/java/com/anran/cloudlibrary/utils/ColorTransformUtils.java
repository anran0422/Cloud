package com.anran.cloudlibrary.utils;

/**
 * 颜色转换工具
 */
public class ColorTransformUtils {

    private ColorTransformUtils() {
        // 工具类不需要实例化
    }

    public static String getStandardColor(String color) {
        // 每一种 RGB 色值都可能只有一个0，转换为 00
        // 如果是 6 位不需要转换，5 位，在第 3 位后加个 0
        // 0x080e0 -> 0x0800e0
        if (color.length() == 7) {
            color = color.substring(0, 4) + "0" + color.substring(4, 7);
        }
        return color;
    }

}
