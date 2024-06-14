/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.inout.erp.CustomerService;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.persistence.security.SaltedPasswordEncoder;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.util.Config;

/**
 * {@code ElfaSHA1PasswordEncoder}
 * 
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class ElfaSHA1PasswordEncoder extends SaltedPasswordEncoder {

    @Autowired
    private UserService userService;
    @Autowired
    private ModelService modelService;
    @Autowired
    @Qualifier("erp.customerService")
    private CustomerService customerService;

    /**
     * Calculate the MD5 hash of the specified password concatenated to the salt
     * 
     * @param password
     * @return the MD5 hash
     */
    private String encode(final String password) {
        return calculateHash(getSaltedPassword(password));
    }

    @Override
    public String encode(final String uid, final String password) {
        ServicesUtil.validateParameterNotNull(uid, "The user UID must not be null");

        final String encodedPassword = encode(password);
        if (uid != null) {
            try {
                final UserModel user = userService.getUserForUID(uid);
                boolean loginOk = false;
                loginOk = customerService.checkElfaAuthentication(uid, password);

                if (loginOk) {
                    final String oldHash = user.getEncodedPassword();
                    userService.setPassword(user, password, "sha_256");
                    modelService.save(user);
                    return oldHash;
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
    @Override
    protected String getSystemSpecificSalt() {
        final String config_salt = Config.getParameter("password.sha1.salt");
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

    /**
     * Concatenate the {@code salt} to the {@code plaintext}
     * 
     * @param plaintext
     * @param salt
     * @return a concatenation of the salt plus the plain text
     */
    private String getSaltedPassword(final String plaintext, final String salt) {
        return (salt == null ? "" : salt).concat(plaintext != null ? plaintext : "");
    }

    /* Getters and Setters */

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public void setCustomerService(final CustomerService customerService) {
        this.customerService = customerService;
    }

}
