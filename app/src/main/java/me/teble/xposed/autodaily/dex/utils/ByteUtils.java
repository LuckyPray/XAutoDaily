package me.teble.xposed.autodaily.dex.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

/**
 * @author teble
 * @date 2020/12/25 16:19
 */
public class ByteUtils {

    public static byte[] file2Bytes(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (Exception ignore) {
            throw new RuntimeException("异常");
        }
    }

    public static String bytes2HexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : src) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv).append(" ");
        }
        if (src.length > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    public static byte[] copyByte(byte[] src, int start, int len) {
        if (len < 0) {
            System.out.println("好耶，出事了");
        }
        byte[] resultByte = new byte[len];
        System.arraycopy(src, start, resultByte, 0, len);
        return resultByte;
    }

    public static int readInt(byte[] res, int index) {
        return (res[index] & 0xff) | ((res[index + 1] << 8) & 0xff00)
            | ((res[index + 2] << 24) >>> 8) | ((res[index + 3] & 0xff) << 24);
    }

    public static int readInt(byte[] res, int[] index) {
        return (res[index[0]++] & 0xff) | ((res[index[0]++] << 8) & 0xff00)
            | ((res[index[0]++] << 24) >>> 8) | ((res[index[0]++] & 0xff) << 24);
    }

    public static int readShort(byte[] res, int index) {
        return (res[index] & 0xff) | ((res[index + 1] & 0xff) << 8);
    }

    public static int readShort(byte[] res, int[] index) {
        return (res[index[0]++] & 0xff) | ((res[index[0]++] & 0xff) << 8);
    }

    public static int readByte(byte[] res, int[] index) {
        return res[index[0]++] & 0xff;
    }

    public static int readUleb128(byte[] src, int[] index) {
        int num = 0, size = 0;
        do {
            num |= (src[index[0]] & 0x7f) << 7 * size++;
        } while ((src[index[0]++] & 0x80) == 0x80);
        return num;
    }

    public static long bytes2num(byte[] src) {
        int num = 0;
        for (int i = 0; i < src.length; i++) {
            num |= (src[i] & 0xff) << i * 8;
        }
        return num;
    }

    public static String readString(byte[] src, int[] index) {
        byte[] bytes = readByteString(src, index);
        return new String(bytes);
    }

    public static byte[] readByteString(byte[] src, int[] index) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte bt;
        while ((bt = src[index[0]++]) != '\0') {
            stream.write(bt);
        }
        return Arrays.copyOf(stream.toByteArray(), stream.size());
    }

    public static int byte2int(byte[] res) {
        return (res[0] & 0xff) | ((res[1] << 8) & 0xff00)
            | ((res[2] << 24) >>> 8) | (res[3] << 24);
    }

    public static byte[] int2Byte(final int integer) {
        byte[] byteArray = new byte[4];
        int byteNum = (40 - Integer.numberOfLeadingZeros(integer < 0 ? ~integer : integer)) / 8;
        for (int n = 0; n < byteNum; n++) {
            byteArray[3 - n] = (byte) (integer >>> (n << 3));
        }
        return byteArray;
    }

    public static String int2Hex(final int integer) {
        return "0x" + Integer.toHexString(integer);
    }
}
