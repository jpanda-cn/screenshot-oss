package cn.jpanda.screenshot.oss.common.utils;

import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class DESUtilsTest {
    /**
     * 生成的密文
     */
    private static final String CIPHERTEXT = "9x//QlP1QZ4Z29iaDmlGJBOWndvM/tnRth3frBM30hNlX95XlYnkQA==";

    /**
     * 被加密的原始内容
     */
    private static final String CONTENT = "这是一段原始内容";

    /**
     * 加密秘钥
     */
    private static final String SECRET_KEY = "123";

    @Test
    public void encrypt() throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        System.out.println(DESUtils.encrypt(CONTENT.getBytes(), SECRET_KEY.getBytes()));
        assert CIPHERTEXT.equals(DESUtils.encrypt(CONTENT.getBytes(), SECRET_KEY.getBytes()));
    }

    @Test
    public void decrypt() throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {

        assert CONTENT.equals(DESUtils.decrypt(CIPHERTEXT.getBytes(), SECRET_KEY.getBytes()));
    }

}