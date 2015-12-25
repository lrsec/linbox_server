package com.medtree.im.server.connector.tcp.handler;


import com.medtree.im.exceptions.IMException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Created by lrsec on 7/10/15.
 */
public class AES {
    private static Logger logger = LoggerFactory.getLogger(AES.class);

    private static final String DEFAULT_PASSWORD = "medtree-im-passwmedtree-im-passw";
//    private static final String DEFAULT_PASSWORD = "medtree-im-passw";
    public static final String CIPHER_ALGORITHM="AES/CBC/PKCS5Padding";
    public static final String KEY_ALGORITHM="AES";
    protected static final String STRING_PATTERN = "UTF-8";

    private SecretKeySpec keypSpec;
    private Cipher cipher;
    private byte[] password;
    private byte[] iv;

    private Base64.Decoder base64Decoder = Base64.getDecoder();

    public AES() {
        password = DEFAULT_PASSWORD.getBytes();
        iv = generateIV(DEFAULT_PASSWORD);
        try {
            keypSpec = new SecretKeySpec(password ,KEY_ALGORITHM);
            cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        } catch (Exception e) {
            logger.error("init AES fail", e);
            throw new IMException("init AES fail");
        }
    }

    public void resetPassword(String pw) {
        if (StringUtils.isBlank(pw)) {
            logger.error("Get a empty password in resetPassword");
            return;
        }

        this.password = base64Decoder.decode(pw);
        this.iv = generateIV(pw);
        this.keypSpec = new SecretKeySpec(this.password, KEY_ALGORITHM);

        logger.debug("Reset password: {}. Password size: {}", pw, this.password.length * 8);
    }

    public byte[] encrypt(String content) {
        byte[] result = null;

        try {
            byte[] byteContent = content.getBytes(STRING_PATTERN);
            cipher.init(Cipher.ENCRYPT_MODE, keypSpec, new IvParameterSpec(iv));
            result = cipher.doFinal(byteContent);
        } catch (Exception e) {
            logger.error("encrypt AES fail", e);
            throw new IMException("encrypt AES fail");
        }

        return result;
    }

    public String decrypt(byte[] content) {
        String result = null;

        try {
            cipher.init(Cipher.DECRYPT_MODE, keypSpec, new IvParameterSpec(iv));
            result = new String(cipher.doFinal(content), STRING_PATTERN);
        } catch (Exception e) {
            logger.error("decrypt AES fail", e);
            throw new IMException("decrypt AES fail");
        }

        return result;
    }

    private byte[] generateIV(String pw) {
        byte[] result = null;

        try {
            result = pw.substring(0, 16).getBytes(STRING_PATTERN);
        } catch (Exception e) {
            logger.error("Create AES IV error ", e);
            throw new IMException("Create AES IV error");
        }

        return result;
    }

}
