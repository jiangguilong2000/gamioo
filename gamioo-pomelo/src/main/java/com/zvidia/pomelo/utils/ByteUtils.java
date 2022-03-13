package com.zvidia.pomelo.utils;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: jiangzm
 * Date: 13-8-4
 * Time: 下午1:48
 * To change this template use File | Settings | File Templates.
 */
public class ByteUtils {


    //字符到字节转换
    public static byte[] charToBytes(char charValue) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putChar(charValue);
        return buffer.array();
    }

    //字节到字符转换
    public static char bytesToChar(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getChar();
    }

    //整数到字节数组的转换
    public static byte[] intToBytes(int intValue) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(intValue);
        return buffer.array();
    }

    //字节数组到整数的转换
    public static int bytesToInt(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getInt();
    }

    //浮点数到字节数组的转换
    public static byte[] floatToBytes(float floatValue) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putFloat(floatValue);
        return buffer.array();
    }

    //字节数组到浮点数的转换
    public static float bytesToFloat(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getFloat();
    }

    //长整型到字节数组的转换
    public static byte[] longToBytes(long longValue) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(longValue);
        return buffer.array();
    }

    //字节数组到长整型的转换
    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }


    //双精度到字节转换
    public static byte[] doubleToBytes(double doubleValue) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putDouble(0, doubleValue);
        return buffer.array();
    }

    //字节到双精度转换
    public static double byteToDouble(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getDouble();
    }
}
