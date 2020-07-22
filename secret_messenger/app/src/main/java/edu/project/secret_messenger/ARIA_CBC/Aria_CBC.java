package edu.project.secret_messenger.ARIA_CBC;

import java.io.PrintStream;
import java.security.InvalidKeyException;
import java.util.Scanner;
import static edu.project.secret_messenger.util.*;

public class Aria_CBC {
    final static int keySize = 256;
    protected static byte[][] CBC_enc(ARIAEngine aria, byte[] primeVec, byte[][] plain_block) throws InvalidKeyException {
        byte[] xor = new byte[16];
        byte[][] cipher_block = new byte[10][16];
        for (int i = 0; i < plain_block.length; i++) { // 초기 벡터와 이전 암호문을 이용한 xor.
            if(i==0) {
                for (int j = 0; j < 16; j++) {
                    xor[j] = (byte) (primeVec[j] ^ plain_block[i][j]);
                }
                aria.encrypt(xor, 0, cipher_block[i], 0);
            }
            else{
                for (int j = 0; j < 16; j++) {
                    xor[j] = (byte) (cipher_block[i-1][j] ^ plain_block[i][j]);
                }
                aria.encrypt(xor, 0, cipher_block[i], 0);
            }
        }
        return plain_block;
    }
    protected static byte[][] CBC_dec(ARIAEngine aria, byte[] primeVec, byte[][] cipher_block) throws InvalidKeyException {
        byte[] xor = new byte[16];
        byte[][] plain_block = new byte[10][16];
        for(int i=0;i<cipher_block.length;i++){
            aria.decrypt(cipher_block[i],0,xor,0);
            if (i == 0)
                for(int j=0;j<16;j++)
                    plain_block[i][j] = (byte) (primeVec[j] ^ cipher_block[i][j]);
            else
                for(int j=0;j<16;j++)
                    plain_block[i][j] = (byte) (cipher_block[i-1][j] ^ xor[j]);
        }
        return plain_block;
    }
    public static void CBC(String plainText, String key, String primeVector) throws InvalidKeyException {
        byte[][] plain_block;
        byte[][] cipher_block;

        byte[] primeVec = hexStringToByteArray(primeVector.replaceAll(" ", ""));

        plain_block = inputPlain(plainText);

        ARIAEngine aria = new ARIAEngine(keySize,key);
        aria.setupRoundKeys();

        //CBC모드 암호화
        cipher_block = CBC_enc(aria,primeVec,plain_block);



        //CBC모드 복호화
        plain_block = CBC_dec(aria,primeVec,cipher_block);

    }
}
