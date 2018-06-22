package com.example.cam.demo;

import java.nio.ByteBuffer;

public class AnalysisUtil {
    /**
     * 将数据包向左移动length个bit，移动之后可能会损失精度
     *
     * @param data
     *需要移动的byte数组
     * @param length
     *移动的bit长度,最大为8
     * @return
     */
    // TODO 可能存在问题
    public static byte[] moveToLeft(byte[] data, int length) {
        if (length == 0) {
            return data;
        }

        byte end = data[data.length - 1];
        if (end < 0) {
            end = (byte) (end + 128);
        }
        end = (byte) (end << length);
        byte[] dataToRight = moveToRight(data, 8 - length);
        System.arraycopy(dataToRight, 1, data, 0, dataToRight.length - 1);
        data[data.length - 1] = end;

        return data;
    }

    /**
     * 将数据包向右移动length个bit，移动之后可能会损失精度
     *
     * @param data
     *            需要移动的byte数组
     * @param length
     *            移动的bit长度,最大为8
     * @return
     */
    public static byte[] moveToRight(byte[] data, int length) {
        byte[] end = { 0x00, 0x00 };
        for (int i = 0; i < data.length; i++) {
            end[1] = (byte) (data[i] << (8 - length));
            data[i] = (byte) (end[0] + data[i] >> length);
            end[0] = end[1];
        }
        return data;
    }

    /**
     * 将byte数组转换为long数据
     *
     * @param data
     *            length为8的byte数组
     * @return
     */
    public static long byteToLong(byte[] data) {
        ByteBuffer buffer = ByteBuffer.allocate(8);

        buffer.put(data, 0, 8);
        buffer.flip();

        return buffer.getLong();
    }

    /**
     * 将byte数组转为int数据
     *
     * @param data
     *            length为4的byte数组
     * @return
     */
    public static int byteToInt(byte[] data) {
        ByteBuffer buffer = ByteBuffer.allocate(4);

        buffer.put(data, 0, 4);
        buffer.flip();

        return buffer.getInt();
    }

    /**
     * 将byte数组转为short数据
     *
     * @param data
     *            length为2的byte数组
     * @return
     */
    public static short byteToShort(byte[] data) {
        ByteBuffer buffer = ByteBuffer.allocate(2);

        buffer.put(data, 0, 2);
        buffer.flip();

        return buffer.getShort();
    }

    public static String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null){
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int j = 0; j < byteArray.length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStrToByteArray(String str)
    {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return new byte[0];
        }
        byte[] byteArray = new byte[str.length() / 2];
        for (int i = 0; i < byteArray.length; i++){
            String subStr = str.substring(2 * i, 2 * i + 2);
            byteArray[i] = ((byte)Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }
}
