package com.medtree.im.server.connector.tcp.handler;


import com.medtree.im.exceptions.IMException;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;

/**
 * Created by lrsec on 7/10/15.
 */
public class AES {
    private static Logger logger = LoggerFactory.getLogger(AES.class);

//    private static final String DEFAULT_PASSWORD = "medtree-im-passwmedtree-im-passw";
    private static final String DEFAULT_PASSWORD = "medtree-im-passw";
    public static final String CIPHER_ALGORITHM="AES/ECB/PKCS7Padding";
    public static final String KEY_ALGORITHM="AES";
    protected static final String STRING_PATTERN = "UTF-8";

    private SecretKeySpec keypSpec;
    private Cipher cipher;
    private byte[] password;


    public AES() {
        password = DEFAULT_PASSWORD.getBytes();
        init();
    }

    public void resetPassword(String pw) {
        if (StringUtils.isBlank(pw)) {
            logger.error("Get a empty password in resetPassword");
            return;
        }

        this.password = Base64.decode(pw);

        logger.debug("Reset password: {}. Password size: {}", pw, this.password.length * 8);

        init();
    }

    private void init(){
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

            keypSpec = new SecretKeySpec(password ,KEY_ALGORITHM);
            cipher=Cipher.getInstance(CIPHER_ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);
        } catch (Exception e) {
            logger.error("init AES fail", e);
            throw new IMException("init AES fail");
        }
    }

    public byte[] encrypt(String content) {
        byte[] result = null;

        try {

            byte[] byteContent = content.getBytes(STRING_PATTERN);
            cipher.init(Cipher.ENCRYPT_MODE, keypSpec);

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
            cipher.init(Cipher.DECRYPT_MODE, keypSpec);

            result = new String(cipher.doFinal(content), STRING_PATTERN);

        } catch (Exception e) {
            logger.error("decrypt AES fail", e);
            throw new IMException("decrypt AES fail");
        }

        return result;
    }

}
