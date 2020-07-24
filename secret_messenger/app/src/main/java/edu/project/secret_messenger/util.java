package edu.project.secret_messenger;

import android.util.Log;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class util {
    private static final String TAG = "util";
    public static final char[] HEX_DIGITS = {
            '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'
    };
    public static String strToHex(String s) {
        String result = "";

        for (int i = 0; i < s.length(); i++) {
            result += String.format(Integer.toHexString(s.charAt(i)));
        }
        result.replaceAll(" ", "");

        if(result.length()%32<32)
            for(int i=result.length()%32;i<32;i++){
                result+="0";
            }
        return result;
    }

    public static void byteToHex(PrintStream out, byte b) {
        char[] buf = {
                HEX_DIGITS[(b >>> 4) & 0x0F],
                HEX_DIGITS[ b        & 0x0F]
        };
        out.print(new String(buf));
    }
    public static byte[][] strToByteArr(String plainText) throws UnsupportedEncodingException {
        byte[][] plain_block = null;
        String hexStr = strToHex(plainText).replaceAll(" ", "");

        Log.e(TAG,"16진수 변환 스트링 : "+hexStr);
        for (int i = 0; i < hexStr.length()/32; i++) {
            if(hexStr.length()>32){
                plain_block = new byte[hexStr.length()/32][16];
                plain_block[i] = hexStringToByteArray(hexStr.substring(i*32,i*32+32));
            }
            else {
                plain_block = new byte[1][16];
                plain_block[0] = hexStringToByteArray(hexStr);
            }
        }

        return plain_block;
    }
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();

        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public static String byteArrayToHexString(byte[] bytes){

        StringBuilder sb = new StringBuilder();

        for(byte b : bytes){

            sb.append(String.format("%02X", b&0xff));
        }

        return sb.toString();
    }
    public static String hashStr(String str) throws InvalidKeyException {
        MessageDigest algorithm = null;
        try{
            String result;
            algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(str.getBytes());
            byte[] messageDigest = algorithm.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                sb.append(Integer.toString((messageDigest[i] & 0xff) + 0x100, 16).substring(1));
            }
            result = sb.toString();

            return result;
        }catch(NoSuchAlgorithmException e){
            throw new InvalidKeyException(e);
        }
    }
    public static String byteArrayToBinaryString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            sb.append(byteToBinaryString(b[i]));
        }
        return sb.toString();
    }
    public static String byteToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }

    public static byte[] binaryStringToByteArray(String s) {
        int count = s.length() / 8;
        byte[] b = new byte[count];
        for (int i = 1; i < count; ++i) {
            String t = s.substring((i - 1) * 8, i * 8);
            b[i - 1] = binaryStringToByte(t);
        }
        return b;
    }
    public static byte binaryStringToByte(String s) {
        byte ret = 0, total = 0;
        for (int i = 0; i < 8; ++i) {
            ret = (s.charAt(7 - i) == '1') ? (byte) (1 << i) : 0;
            total = (byte) (ret | total);
        }
        return total;
    }


    public long DateToMill(String date) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        Date trans_date = null;
        try {
            trans_date = formatter.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block e.printStackTrace(); }
        }
        return trans_date.getTime();
    }
}
