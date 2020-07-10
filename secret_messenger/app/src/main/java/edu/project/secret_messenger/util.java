package edu.project.secret_messenger;

import java.io.PrintStream;
import java.security.InvalidKeyException;

public class util {
    public static final char[] HEX_DIGITS = {
            '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'
    };

    public static void byteToHex(PrintStream out, byte b) {
        char[] buf = {
                HEX_DIGITS[(b >>> 4) & 0x0F],
                HEX_DIGITS[ b        & 0x0F]
        };
        out.print(new String(buf));
    }
    public static byte[][] inputPlain(String plainText) throws InvalidKeyException {
        byte[][] plain_block = null;


        for (int i = 0; i < plainText.length()/16; i++) {
            if(plainText.length()>16){
                plain_block = new byte[plainText.length()/16][16];
                plain_block[i] = StrToByte(plainText.substring(i,i+3));
            }
            else{
                plain_block = new byte[1][16];
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
    public static byte[] StrToByte(String str) throws InvalidKeyException {
        byte[] result = str.getBytes();
        return result;

//        MessageDigest algorithm = null;
//        try{
//            algorithm = MessageDigest.getInstance("MD5");
//            algorithm.reset();
//            algorithm.update(str.getBytes());
//            byte[] messageDigest = algorithm.digest();
//            return messageDigest;
//        }catch(NoSuchAlgorithmException e){
//            throw new InvalidKeyException(e);
//        }
    }

}
