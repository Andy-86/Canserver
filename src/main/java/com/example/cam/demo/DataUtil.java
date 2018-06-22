package com.example.cam.demo;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DataUtil {
    //private static Logger gLogger = Logger.getLogger("DataUtil");
    /**
     * 将数值移动
     * @param data1 数值
     * @param data2 数值
     * @param number 希望data1最高位保留几个位数
     * return long[2]，第一个值为移动后的data1，第二个值为移动后的data2
     */
    public static long[] move(long data1,long data2,int number) {
        long[] l = new long[2];
        l[0] = data1 << (64 - number);
        l[0] = l[0] + data2 >> number;
        l[1] = data2 << (64 - number);
        return l;
    }

    /**
     * 将long转为byte数组
     * @param number
     * @return
     */
    public static byte[] longToByte(long number) {
        byte[] b = new byte[8];

        b[0] = (byte) (number >> 56);
        b[1] = (byte) (number >> 48);
        b[2] = (byte) (number >> 40);
        b[3] = (byte) (number >> 32);
        b[4] = (byte) (number >> 24);
        b[5] = (byte) (number >> 16);
        b[6] = (byte) (number >> 8);
        b[7] = (byte) number;

        return b;
    }

    /**
     * 将short转为byte数组
     * @param number
     * @return
     */
    public static byte[] shortToByte(short number) {
        byte[] b = new byte[2];

        b[0] = (byte) (number >> 8);
        b[1] = (byte) number;

        return b;
    }

    public static short ByteToShort(byte byte0,byte byte1) {
        return (short) (((byte0 & 0xff) << 8) | (byte1 & 0xff));
    }

    /**
     * 将int转为byte数组
     * @param number
     * @return
     */
    public static byte[] intToByte(int number) {
        byte[] b = new byte[4];

        b[0] = (byte) (number >> 24);
        b[1] = (byte) (number >> 16);
        b[2] = (byte) (number >> 8);
        b[3] = (byte) number;

        return b;
    }

    /**
     * 将byte转换为一个长度为8的byte数组，数组每个值代表bit
     * @param b
     * @return
     */
    public static byte[] getBitArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte)(b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    /**
     * 把byte转为字符串的bit
     * @param b
     * @return
     */
    public static String byteToBit(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }

    /**
     * bit 转换为byte
     * @param byteStr  位字符串
     * @return
     */
    public static byte BitToByte(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {//4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }


    //public static long getIMEI(){return IMEI;}

    //bytes to string
    public static String getHexString(byte[] bs) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (bs == null || bs.length <= 0) {
            return null;
        }
        for (int i = 0; i < bs.length; i++) {
            int v = bs[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv + " ");
        }
        return stringBuilder.toString();
    }

    //Hex String to bytes
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
    public static String getString(byte[] bytes) {
        return getString(bytes, "GBK");
    }
    public static String getUTFString(byte[] bytes){
        return new String(bytes);
    }
    public static String getString(byte[] bytes, String charsetName) {
        return new String(bytes, Charset.forName(charsetName));
    }
    public static String bcd2Str(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
                .toString().substring(1) : temp.toString();
    }

    /**
     * Ascii转换为字符串
     * @param value
     * @return
     */
    public static String asciiToString(String value)
    {
        StringBuilder sbu = new StringBuilder();
        String[] chars = value.split(",");
        for (int i = 0; i < chars.length; i++) {
            sbu.append((char) Integer.parseInt(chars[i]));
        }
        return sbu.toString();
    }

    /**
     * 浮点转换为字节
     *
     * @param f
     * @return
     */
    public static byte[] floatTobyte(float f) {

        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }

        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }
        return dest;

    }

    /**
     * 对数据进行MD5后进行校验
     * @param data
     * @return
     */
    public static void validate(byte[] data,byte[] checkcode,int len){
        try{
            byte[] datas=new byte[len];
            System.arraycopy(data,0,datas,0,len);
            byte[] four=EncoderByMd5(datas);
            checkcode[0]=four[0];
            checkcode[1]=four[4];
            checkcode[2]=four[8];
            checkcode[3]=four[12];
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }catch (UnsupportedEncodingException a){
            a.printStackTrace();
        }
    }

    /**
     * md5加密
     * @param data packet bytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static byte[] EncoderByMd5(byte[] data) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5= MessageDigest.getInstance("MD5");
        return md5.digest(data);
//		byte[] code = md5.digest(data);
//		return code;
    }
}
