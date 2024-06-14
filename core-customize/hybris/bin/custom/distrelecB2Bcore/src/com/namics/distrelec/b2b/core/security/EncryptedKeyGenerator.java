/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

/**
 * Class to create the encrypted blowfish and hmac keys to save in the property-files
 * 
 * @author sivakumaran, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class EncryptedKeyGenerator {
    private static final Logger LOG = Logger.getLogger(EncryptedKeyGenerator.class);

    private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    private static final String PRIVATE_KEY = "2s0/de!sd[s9]sf2"; // Has to be the same as in
                                                                  // com.namics.distrelec.b2b.core.util.DistCryptography.PRIVATE_ENCRYPTION_KEY

    // Replace with the key you want to encrypt
    private static final String KEY = "";

    public static void main(final String[] args) {
        try {
            final SecretKeySpec secretKey = new SecretKeySpec(PRIVATE_KEY.getBytes("UTF-8"), "Blowfish");
            final Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            System.out.println(new String(encodeHex(cipher.doFinal(KEY.getBytes("UTF-8")))));
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    protected static char[] encodeHex(final byte[] data) {
        final int dataLength = data.length;
        char[] out = new char[dataLength << 1];
        int i = 0;
        for (int j = 0; i < dataLength; ++i) {
            out[(j++)] = DIGITS_LOWER[((0xF0 & data[i]) >>> 4)];
            out[(j++)] = DIGITS_LOWER[(0xF & data[i])];
        }
        return out;
    }

}
