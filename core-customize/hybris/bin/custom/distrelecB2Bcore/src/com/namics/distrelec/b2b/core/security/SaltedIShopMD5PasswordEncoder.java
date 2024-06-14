/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.security;

import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.persistence.security.MD5PasswordEncoder;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;


/**
 * {@code SaltedIShopMD5PasswordEncoder}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class SaltedIShopMD5PasswordEncoder extends MD5PasswordEncoder {

    private String salt;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelService modelService;

    /**
     * Calculate the MD5 hash of the specified password concatenated to the salt
     * 
     * @param password
     * @return the MD5 hash
     */
    private String encode(final String password) {
        return calculateMD5(getSaltedPassword(password));
    }

    /** {@inheritDoc} */
    @Override
    public String encode(final String uid, final String password) {
        final String encodedPassword = encode(password);
        if (uid != null) {
            try {
                final UserModel user = userService.getUserForUID(uid);
                if (user.getEncodedPassword() != null && user.getEncodedPassword().equals(encodedPassword)) {
                    userService.setPassword(user, password, "sha_256");
                    modelService.save(user);
                }
            } catch (final UnknownIdentifierException uie) {
                // NOP
            }
        }

        return encodedPassword;
    }

    /**
     * Check whether the password is already salted
     * 
     * @param password
     * @return {@code true} if the the password is already salted, else returns {@code false}.
     */
    protected boolean isSaltedAlready(final String password) {
        if (password == null) {
            return false;
        }

        return password.startsWith(getSystemSpecificSalt());
    }

    /**
     * Return the configured salt
     * 
     * @return the configured salt
     */
    protected String getSystemSpecificSalt() {
        final String config_salt = Config.getParameter("password.md5.salt");
        if (config_salt == null || config_salt.trim().length() == 0) {
            return ((getSalt() != null) ? getSalt() : "hybris blue pepper can be used for creating delicious noodle meals");
        }

        return config_salt;
    }

    /**
     * Concatenate the salt to the {@code plaintext}
     * 
     * @param plaintext
     * @return a concatenation of the salt plus the plain text
     */
    private String getSaltedPassword(final String plaintext) {
        return getSystemSpecificSalt().concat(plaintext != null ? plaintext : "");
    }

    /* Getter and Setter */

    public String getSalt() {
        return salt;
    }

    public void setSalt(final String salt) {
        this.salt = salt;
    }
}
