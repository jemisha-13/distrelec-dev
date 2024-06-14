/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util;

import java.beans.Introspector;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * Cryptography methods for the Distrelec project.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class DistCryptography {

    private static final Logger LOG = Logger.getLogger(DistCryptography.class);

    public static final String DATA_CRYPTION_KEY = ".payment.blowfish.key";
    public static final String HMAC_KEY = ".payment.hmac.key";
    public static final String WEBTREKK_KEY = "webtrekk.crypto.key";
    public static final String WEB_SESSION_KEY = "web.session.crypto.key";
    public static final String DIBS_SECRET_KEY = "payment.dibs.mac.key";

    private static final String PRIVATE_ENCRYPTION_KEY = "2s0/de!sd[s9]sf2";

    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    private static final int FOUR = 4;
    private static final int HEX_F0 = 0xF0;
    private static final int HEX_0F = 0x0F;

    // Blowfish Key "sB!87=pZf2D(Yz]6"
    // HMAC Key "6Bo*)w5ReW(7!z3SH2_jx[L49Kb?Ym=8"
    // Webtrekk Key "3vY!Q[l&"
    // Websession Key "6zK8YoWa"

    public static String encryptString(final String plainText, final String secretKey) {
        Assert.hasText(plainText, "The attribute [plainText] cannot be empty.");

        try {
            final SecretKeySpec key = new SecretKeySpec(getDecryptedKeyFromConfig(secretKey).getBytes("UTF-8"), "Blowfish");
            final Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Hex.encodeHexString(cipher.doFinal(plainText.getBytes("UTF-8")));
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            throw new IllegalArgumentException("Plain text could not be encrypted", e);
        }
    }

    public static String decryptString(final String encryptedText, final String secretKey, final String length) {
        Assert.hasText(encryptedText, "The attribute [encryptedText] cannot be empty.");
        return decrypt(encryptedText, getDecryptedKeyFromConfig(secretKey), Integer.parseInt(length));
    }

    public static String encodeString(final String plainText, final String secretKey) {
        Assert.hasText(plainText, "The attribute [plainText] cannot be empty.");

        try {
            final Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            final SecretKeySpec secret_key = new SecretKeySpec(getDecryptedKeyFromConfig(secretKey).getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            return Hex.encodeHexString(sha256_HMAC.doFinal(plainText.getBytes("UTF-8")));
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            throw new IllegalArgumentException("HMAC value could not be encoded", e);
        }
    }

    protected static String getDecryptedKeyFromConfig(final String propertyKey) {
        final String key = getEncryptedKey(propertyKey);
        return decryptKey(key, getConfigurationService().getConfiguration().getInt(propertyKey + ".length"));
    }

    protected static String decryptKey(final String key, final int length) {
        return decrypt(key, PRIVATE_ENCRYPTION_KEY, length);
    }

    protected static String decrypt(final String text, final String key, final int length) {
        try {
            final Hex hex = new Hex();
            final byte[] encryptedData = hex.decode(text.getBytes("UTF-8"));
            final SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "Blowfish");
            final Cipher cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            final byte[] decrypted = cipher.doFinal(encryptedData);
            final String decryptedString = new String(decrypted, "UTF-8");
            return StringUtils.substring(decryptedString, 0, length);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            throw new IllegalArgumentException("Encrypted text could not be decrypted", e);
        }
    }

    protected static String getEncryptedKey(final String propertyKey) {
        final CMSSiteService baseStoreService = (CMSSiteService) getBean("cmsSiteService", CMSSiteService.class);
        final String value = getConfigurationService().getConfiguration().getString(baseStoreService.getCurrentSite().getUid() + "." + propertyKey);
        return StringUtils.isNotBlank(value) ? value : getConfigurationService().getConfiguration().getString(propertyKey);
    }

    public static String getDibsSecretKey() {
        return getDecryptedKeyFromConfig(DIBS_SECRET_KEY);
    }

    public static String toSHA1(String plainText) {
        return toHash("SHA-1", plainText);
    }

    public static String toSHA256(String plainText) {
        return toHash("SHA-256", plainText);
    }

    public static String toMD5(String plainText) {
        return toHash("MD5", plainText);
    }

    private static String toHash(String algorithm, String plainText){
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            final byte[] buf = messageDigest.digest(plainText.getBytes());
            char[] chars = new char[2 * buf.length];
            for (int i = 0; i < buf.length; ++i) {
                chars[2 * i] = HEX_CHARS[(buf[i] & HEX_F0) >>> FOUR];
                chars[2 * i + 1] = HEX_CHARS[buf[i] & HEX_0F];
            }
            return new String(chars);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Could not create sha1 hash.", e);
        }
        return null;
    }

    protected static ConfigurationService getConfigurationService() {
        return (ConfigurationService) getBean(ConfigurationService.class);
    }

    protected static Object getBean(final Class beanClass) {
        return getBean(Introspector.decapitalize(ClassUtils.getShortName(beanClass)), beanClass);
    }

    protected static Object getBean(final String name, final Class beanClass) {
        return Registry.getApplicationContext().getBean(name, beanClass);
    }

}
