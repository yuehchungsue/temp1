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
     * RSA�̤j�[�K����j�p
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA�̤j�ѱK�K��j�p
     * javax.crypto.BadPaddingException: Decryption error
     * �̤j�ѱK���פ����T�ɭP�����AMAX_DECRYPT_BLOCK������K�_����/8�]1byte=8bit�^�A
     * �ҥH��K�_��Ƭ�2048�ɡA�̤j�ѱK��������256.
     */
    private static final int MAX_DECRYPT_BLOCK = 256;

    /**
     * ������_��
     *
     * @return ���_��
     */
    private static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);//20220901 �սc���i��ĳ2048�줸�H�W�����_
        return generator.generateKeyPair();
    }

    /**
     * ����p�_
     *
     * @param privateKey �p�_�r��
     * @return
     */
    private static PrivateKey getPrivateKey(String privateKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodedKey = Base64.decodeBase64(privateKey.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * ������_
     *
     * @param publicKey ���_�r��
     * @return
     */
    private static PublicKey getPublicKey(String publicKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodedKey = Base64.decodeBase64(publicKey.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * RSA�[�K
     *
     * @param data �ݥ[�K���
     * @param publicKey ���_
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
        // ���Ƥ��q�[�K
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
        // ����[�K���e�ϥ�base64�i��s�X,�åHUTF-8���з���Ʀ��r��
        // �[�K�᪺�r��
        return new String(Base64.encodeBase64String(encryptedData));
    }

    /**
     * RSA�ѱK
     *
     * @param data �ݸѱK���
     * @param privateKey �p�_
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
        // ���Ƥ��q�ѱK
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
        // �ѱK�᪺���e
        return new String(decryptedData, "UTF-8");
    }

    /**
     * ñ�W
     *
     * @param data ��ñ�W���
     * @param privateKey �p�_
     * @return ñ�W
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
     * ����
     *
     * @param srcData ��l�r��
     * @param publicKey ���_
     * @param sign ñ�W
     * @return �O�_���ҳq�L
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
//            // �ͦ����_��
//            KeyPair keyPair = getKeyPair();
//            String privateKey = new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
//            String publicKey = new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));
//            System.out.println("�p�_:" + privateKey);
//            System.out.println("���_:" + publicKey);
//            // RSA�[�K
//            String data = "�ݥ[�K����r���e";
//            String encryptData = encrypt(data, getPublicKey(publicKey));
//            System.out.println("�[�K�᤺�e:" + encryptData);
//            // RSA�ѱK
//            String decryptData = decrypt(encryptData, getPrivateKey(privateKey));
//            System.out.println("�ѱK�᤺�e:" + decryptData);
//            // RSAñ�W
//            String sign = sign(data, getPrivateKey(privateKey));
//            System.out.println("�[ñ��G"+sign);
//            // RSA����
//            boolean result = verify(data, getPublicKey(publicKey), sign);
//            System.out.print("��ñ���G:" + result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.print("�[�ѱK���`");
//        }
//    }
