package com.medtree.im.utils;

import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Security;

/**
 * Created by lrsec on 7/13/15.
 */
public class AESUtils {
    private static Logger logger = LoggerFactory.getLogger(AESUtils.class);
    public static final String KEY_ALGORITHM="AES";

    public static String generatePassword() {
        byte[] pw;

        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            KeyGenerator kg= KeyGenerator.getInstance(KEY_ALGORITHM, "BC");
            kg.init(128);
            SecretKey s=kg.generateKey();

            pw = s.getEncoded();

            logger.debug("New AES password created: {}. Bit size: {}", pw, pw.length * 8 );

        } catch (Exception e) {
            logger.error("Fail to generate key.", e);
            throw new RuntimeException("Fail to generate key.");
        }

        return Base64.toBase64String(pw);
    }
}
