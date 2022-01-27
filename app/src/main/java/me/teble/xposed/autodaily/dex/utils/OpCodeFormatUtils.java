package me.teble.xposed.autodaily.dex.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author teble
 * @date 2020/12/31 15:13
 */
public class OpCodeFormatUtils {

    private final static String[] opcodeFormat = new String[]{
        "10x", "12x", "22x", "32x", "12x", "22x", "32x", "12x", "22x", "32x",
        "11x", "11x", "11x", "11x", "10x", "11x", "11x", "11x", "11n", "21s",
        "31i", "21h", "21s", "31i", "51l", "21h", "21c", "31c", "21c", "11x",
        "11x", "21c", "22c", "12x", "21c", "22c", "35c", "3rc", "31t", "11x",
        "10t", "20t", "30t", "31t", "31t", "23x", "23x", "23x", "23x", "23x",
        "22t", "22t", "22t", "22t", "22t", "22t", "21t", "21t", "21t", "21t",
        "21t", "21t", "10x", "10x", "10x", "10x", "10x", "10x", "23x", "23x",
        "23x", "23x", "23x", "23x", "23x", "23x", "23x", "23x", "23x", "23x",
        "23x", "23x", "22c", "22c", "22c", "22c", "22c", "22c", "22c", "22c",
        "22c", "22c", "22c", "22c", "22c", "22c", "21c", "21c", "21c", "21c",
        "21c", "21c", "21c", "21c", "21c", "21c", "21c", "21c", "21c", "21c",
        "35c", "35c", "35c", "35c", "35c", "10x", "3rc", "3rc", "3rc", "3rc",
        "3rc", "10x", "10x", "12x", "12x", "12x", "12x", "12x", "12x", "12x",
        "12x", "12x", "12x", "12x", "12x", "12x", "12x", "12x", "12x", "12x",
        "12x", "12x", "12x", "12x", "23x", "23x", "23x", "23x", "23x", "23x",
        "23x", "23x", "23x", "23x", "23x", "23x", "23x", "23x", "23x", "23x",
        "23x", "23x", "23x", "23x", "23x", "23x", "23x", "23x", "23x", "23x",
        "23x", "23x", "23x", "23x", "23x", "23x", "12x", "12x", "12x", "12x",
        "12x", "12x", "12x", "12x", "12x", "12x", "12x", "12x", "12x", "12x",
        "12x", "12x", "12x", "12x", "12x", "12x", "12x", "12x", "12x", "12x",
        "12x", "12x", "12x", "12x", "12x", "12x", "12x", "12x", "22s", "22s",
        "22s", "22s", "22s", "22s", "22s", "22s", "22b", "22b", "22b", "22b",
        "22b", "22b", "22b", "22b", "22b", "22b", "22b", "10x", "10x", "10x",
        "10x", "10x", "10x", "10x", "10x", "10x", "10x", "10x", "10x", "10x",
        "10x", "10x", "10x", "10x", "10x", "10x", "10x", "10x", "10x", "10x",
        "45cc", "4rcc", "35c", "3rc", "21c", "21c"
    };

    public final static Map<String, Integer> formatMap = new HashMap<String, Integer>() {
        {
            put("10x", 0x02);
            put("12x", 0x02);
            put("11n", 0x02);
            put("11x", 0x02);
            put("10t", 0x02);
            put("20t", 0x04);
            put("20bc", 0x04);
            put("22x", 0x04);
            put("21t", 0x04);
            put("21s", 0x04);
            put("21h", 0x04);
            put("21c", 0x04);
            put("23x", 0x04);
            put("22b", 0x04);
            put("22t", 0x04);
            put("22s", 0x04);
            put("22c", 0x04);
            put("22cs", 0x04);
            put("30t", 0x06);
            put("32x", 0x06);
            put("31i", 0x06);
            put("31t", 0x06);
            put("31c", 0x06);
            put("35c", 0x06);
            put("35ms", 0x06);
            put("35mi", 0x06);
            put("3rc", 0x06);
            put("3rms", 0x06);
            put("3rmi", 0x06);
            put("45cc", 0x08);
            put("4rcc", 0x08);
            put("51l", 0x0a);
        }
    };

    public static Integer getOpSize(int hex) {
        return formatMap.get(opcodeFormat[hex & 0xff00 >> 8]);
    }

    public static Integer getOpSize(byte[] src, int index) {
        return formatMap.get(opcodeFormat[src[index] & 0xff]);
    }

    public static Integer getOpSize(byte op) {
        return formatMap.get(opcodeFormat[op & 0xff]);
    }
}
