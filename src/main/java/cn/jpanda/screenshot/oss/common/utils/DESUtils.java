package cn.jpanda.screenshot.oss.common.utils;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * DES 加解密工具类
 *
 * @author Hanqi <jpanda@aliyun.com>
 * @since 2018/11/20 17:17
 */
public class DESUtils {

    public static String encrypt(byte[] bytes, byte[] keys) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, BadPaddingException, IllegalBlockSizeException {

        // Base64编码 被加密的数据 。
        bytes = Base64.getEncoder().encode(bytes);
        // Base64编码 秘钥 。
        keys = Base64.getEncoder().encode(keys);

        keys = to8Multiple(keys);

        // 创建加密随机数生成器
        SecureRandom secureRandom = new SecureRandom();

        // 创建一个DES 秘钥
        DESKeySpec desKeySpec = new DESKeySpec(keys);

        // 获取DES 秘钥工厂类
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

        // 通过DES 秘钥工厂类获取秘钥
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

        // 获取DES 加密对象
        Cipher cipher = Cipher.getInstance("DES");

        // 执行加密操作
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, secureRandom);

        // Base64 [编码]  最终生成的密文
        return new String(Base64.getEncoder().encode(cipher.doFinal(bytes)));
    }

    public static String decrypt(byte[] data, byte[] keys) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        //Base64 [解码]  最终生成的密文
        data = Base64.getDecoder().decode(data);
        // Base64编码 秘钥 。
        keys = Base64.getEncoder().encode(keys);

        keys = to8Multiple(keys);

        // 创建加密随机数生成器
        SecureRandom secureRandom = new SecureRandom();
        // 创建一个DES 秘钥
        DESKeySpec desKey = new DESKeySpec(keys);
        // 获取DES 秘钥工厂类
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 通过DES 秘钥工厂类获取秘钥
        SecretKey secureKey = keyFactory.generateSecret(desKey);
        // 获取DES 加密对象
        Cipher cipher = Cipher.getInstance("DES");
        // 执行加密操作
        cipher.init(Cipher.DECRYPT_MODE, secureKey, secureRandom);

        // Base64 [解码]  原始内容
        return new String(Base64.getDecoder().decode(cipher.doFinal(data)));
    }


    private static byte[] to8Multiple(byte[] bytes) {
        int size = 8 - bytes.length % 8;
        if (bytes.length > 24 && size == 8) {
            return bytes;
        }
        int newSize = (bytes.length > 24 ? bytes.length + size : 24);
        byte[] result = new byte[newSize + size];
        for (int i = 0; i < result.length; i++) {
            if (i < bytes.length) {
                result[i] = bytes[i];
                continue;
            }
            result[i] = (byte) ((127 - i) % 127);
        }
        return result;
    }
}
