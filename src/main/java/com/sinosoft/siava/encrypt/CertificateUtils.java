package com.sinosoft.siava.encrypt;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * 非非对等加密协议加密工具类(暂时无法使用)
 * @author zhangxin
 */
@Deprecated
public class CertificateUtils {
    private static Base64 base64 = new Base64();
    /**
     * 加密算法RSA
     */
    private static final String KEY_ALGORITHM = "RSA";

    /**
     * 签名算法
     */
    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * <p>
     * 生成密钥对(公钥和私钥)
     * </p>
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * <p>
     * 用私钥对信息生成数字签名
     * </p>
     *
     * @param data 已加密数据
     * @param privateKey 私钥(BASE64编码)
     *
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = base64.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return new String(base64.encode(signature.sign()));
    }

    /**
     * <p>
     * 校验数字签名
     * </p>
     *
     * @param data 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign 数字签名
     *
     * @return
     * @throws Exception
     *
     */
    public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {
        byte[] keyBytes = base64.decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(base64.decode(sign));
    }

    /**
     * <p>
     * 获取私钥
     * </p>
     *
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return new String(base64.encode(key.getEncoded()));
    }

    /**
     * <p>
     * 获取公钥
     * </p>
     *
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return new String(base64.encode(key.getEncoded()));
    }

    /**
     *
     * @param data
     * @param keyBytes
     * @return
     */
    public static ByteArrayOutputStream dataSubEncrypt(byte[] data,byte[] keyBytes)
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            byte[] cache;
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
            cipher.init(Cipher.ENCRYPT_MODE, privateK);
            int i = 0;
            int inputLen = data.length;

            int offSet = 0;
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return out;
    }

    /**
     *
     * @param encryptedData
     * @param keyBytes
     * @return
     */
    public static ByteArrayOutputStream dataSubDecrypt(byte[] encryptedData,byte[] keyBytes)
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key publicK = keyFactory.generatePublic(x509KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, publicK);
            int inputLen = encryptedData.length;
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * <P>
     * 秘钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param key 秘钥(BASE64编码)，公钥私钥同一方法
     * @return 解密后的Byte数组
     * @throws IOException 流可能关闭失败
     */
    public static byte[] keyDecrypt(byte[] encryptedData, String key) throws IOException {
        byte[] keyBytes = base64.decode(key);
        ByteArrayOutputStream out = dataSubDecrypt(encryptedData,keyBytes);
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * <P>
     * 秘钥加密
     * </p>
     *
     * @param encryptedData 要加密数据
     * @param key 秘钥(BASE64编码)，公钥私钥同一方法
     * @return 加密后的Byte数组
     * @throws IOException 流可能关闭失败
     */
    public static byte[] keyEncrypt(byte[] encryptedData, String key) throws IOException {
        byte[] keyBytes = base64.decode(key);
        ByteArrayOutputStream out = dataSubEncrypt(encryptedData,keyBytes);
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }
}
