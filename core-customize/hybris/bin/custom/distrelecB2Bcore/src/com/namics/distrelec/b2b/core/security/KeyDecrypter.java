/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * KeyDecrypter
 * 
 * @author dsivakumaran, Namics AG
 * @since Distrelec 1.0
 * 
 */
public class KeyDecrypter {

    private static final Logger LOG = LoggerFactory.getLogger(KeyDecrypter.class);

    private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    private static final String PRIVATE_KEY = "2s0/de!sd[s9]sf2";

    // Replace with the key you want to decrypt
    private static final String KEY = "";

    public static void main(final String[] args) {
        try {
            final SecretKeySpec secretKey = new SecretKeySpec(PRIVATE_KEY.getBytes("UTF-8"), "Blowfish");
            final Cipher cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            if (LOG.isDebugEnabled()) {
                LOG.debug(StringUtils.substring(new String(cipher.doFinal(new Hex().decode(KEY.getBytes("UTF-8"))), "UTF-8"), 0, 16));
            }
        } catch (Exception e) {
            LOG.warn("Exception occurred during decryption", e);
        }
    }

    protected static char[] encodeHex(final byte[] data) {
        final int length = data.length;
        char[] out = new char[length << 1];
        int counter = 0;
        for (int j = 0; counter < length; ++counter) {
            out[(j++)] = DIGITS_LOWER[((0xF0 & data[counter]) >>> 4)];
            out[(j++)] = DIGITS_LOWER[(0xF & data[counter])];
        }
        return out;
    }

}
