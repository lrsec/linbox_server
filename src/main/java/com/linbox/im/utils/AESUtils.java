package com.linbox.im.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

/**
 * Created by lrsec on 7/13/15.
 */
public class AESUtils {
    private static Logger logger = LoggerFactory.getLogger(AESUtils.class);
    private static final String KEY_ALGORITHM="AES";
    private static final int KEY_LENGTH=128;

    private static Base64.Encoder base64Encoder = Base64.getEncoder();

    public static String generatePassword() {
        byte[] pw;

        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(KEY_ALGORITHM);
            keyGen.init(KEY_LENGTH); // for example
            SecretKey s=keyGen.generateKey();

            pw = s.getEncoded();

            logger.debug("New AES password created: {}. Bit size: {}", pw, pw.length * 8 );

        } catch (Exception e) {
            logger.error("Fail to generate key.", e);
            throw new RuntimeException("Fail to generate key.");
        }

        return base64Encoder.encodeToString(pw);
    }
}
