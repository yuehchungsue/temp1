package com.cybersoft.common;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

//https://www.gushiciku.cn/pl/gxSy/zh-tw

public class EnRSA {
	/**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     * javax.crypto.BadPaddingException: Decryption error
     * 最大解密長度不正確導致報錯，MAX_DECRYPT_BLOCK應等於密鑰長度/8（1byte=8bit），
     * 所以當密鑰位數為2048時，最大解密長度應為256.
     */
    private static final int MAX_DECRYPT_BLOCK = 256;

    /**
     * 獲取金鑰對
     *
     * @return 金鑰對
     */
    private static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);//20220901 白箱報告建議2048位元以上的金鑰
        return generator.generateKeyPair();
    }

    /**
     * 獲取私鑰
     *
     * @param privateKey 私鑰字串
     * @return
     */
    private static PrivateKey getPrivateKey(String privateKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodedKey = Base64.decodeBase64(privateKey.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 獲取公鑰
     *
     * @param publicKey 公鑰字串
     * @return
     */
    private static PublicKey getPublicKey(String publicKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodedKey = Base64.decodeBase64(publicKey.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * RSA加密
     *
     * @param data 待加密資料
     * @param publicKey 公鑰
     * @return
     */
    private static String encrypt(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int inputLen = data.getBytes().length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // 對資料分段加密
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data.getBytes(), offset, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data.getBytes(), offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        // 獲取加密內容使用base64進行編碼,並以UTF-8為標準轉化成字串
        // 加密後的字串
        return new String(Base64.encodeBase64String(encryptedData));
    }

    /**
     * RSA解密
     *
     * @param data 待解密資料
     * @param privateKey 私鑰
     * @return
     */
    private static String decrypt(String data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] dataBytes = Base64.decodeBase64(data);
        int inputLen = dataBytes.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        byte[] cache;
        int i = 0;
        // 對資料分段解密
        while (inputLen - offset > 0) {
            if (inputLen - offset > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(dataBytes, offset, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(dataBytes, offset, inputLen - offset);
            }
            out.write(cache, 0, cache.length);
            i++;
            offset = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        // 解密後的內容
        return new String(decryptedData, "UTF-8");
    }

    /**
     * 簽名
     *
     * @param data 待簽名資料
     * @param privateKey 私鑰
     * @return 簽名
     */
    public static String sign(String data, PrivateKey privateKey) throws Exception {
        byte[] keyBytes = privateKey.getEncoded();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(key);
        signature.update(data.getBytes());
        return new String(Base64.encodeBase64(signature.sign()));
    }

    /**
     * 驗籤
     *
     * @param srcData 原始字串
     * @param publicKey 公鑰
     * @param sign 簽名
     * @return 是否驗籤通過
     */
    public static boolean verify(String srcData, PublicKey publicKey, String sign) throws Exception {
        byte[] keyBytes = publicKey.getEncoded();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(key);
        signature.update(srcData.getBytes());
        return signature.verify(Base64.decodeBase64(sign.getBytes()));
    }
    
    private static KeyPair keyPair ;
    private static String privateKey;
    private static String publicKey;
	
    static {
    	try {
			keyPair = getKeyPair();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	privateKey = new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
    	publicKey = new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));
    }
    
    public static String encrypt(String Str) {
    	String encryptStr = null;
    	try {
    		encryptStr = encrypt(Str, getPublicKey(publicKey));
    		return encryptStr;
    	} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptStr;
    }
    
    public static String decrypt(String encryptedStr) {
    	String decryptedStr = null;
    	try {
			decryptedStr=decrypt(encryptedStr, getPrivateKey(privateKey));
			return decryptedStr;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decryptedStr;
    }
}  
//    public static void main(String[] args) {
//        try {
//            // 生成金鑰對
//            KeyPair keyPair = getKeyPair();
//            String privateKey = new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
//            String publicKey = new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));
//            System.out.println("私鑰:" + privateKey);
//            System.out.println("公鑰:" + publicKey);
//            // RSA加密
//            String data = "待加密的文字內容";
//            String encryptData = encrypt(data, getPublicKey(publicKey));
//            System.out.println("加密後內容:" + encryptData);
//            // RSA解密
//            String decryptData = decrypt(encryptData, getPrivateKey(privateKey));
//            System.out.println("解密後內容:" + decryptData);
//            // RSA簽名
//            String sign = sign(data, getPrivateKey(privateKey));
//            System.out.println("加簽後："+sign);
//            // RSA驗籤
//            boolean result = verify(data, getPublicKey(publicKey), sign);
//            System.out.print("驗簽結果:" + result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.print("加解密異常");
//        }
//    }
