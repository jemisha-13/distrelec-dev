package com.namics.distrelec.b2b.storefront.security.impl;

import com.namics.distrelec.b2b.storefront.attributes.Attributes;
import com.namics.distrelec.b2b.storefront.security.GUIDCookieStrategy;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.web.util.CookieGenerator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Default implementation of {@link DefaultDYServerCookieStrategy}
 */
public class DefaultDYServerCookieStrategy implements GUIDCookieStrategy {
    private static final Logger LOG = Logger.getLogger(DefaultDYServerCookieStrategy.class);

    private final SecureRandom random;
    private final MessageDigest sha;

    private CookieGenerator cookieGenerator;

    public DefaultDYServerCookieStrategy() throws NoSuchAlgorithmException {
        random = SecureRandom.getInstance("SHA1PRNG");
        sha = MessageDigest.getInstance("SHA-1");
        Assert.notNull(random);
        Assert.notNull(sha);
    }

    @Override
    public void setCookie(final HttpServletRequest request, final HttpServletResponse response) {
        if (!request.isSecure()) {
            // We must not generate the cookie for insecure requests, otherwise there is not point doing this at all
            throw new IllegalStateException("Cannot set DYCookie on an insecure request!");
        }

        if (request.getCookies() != null) {
            //DISTRELEC-21131: Copy _dyid and set it as _dyid_server cookie
            for (final Cookie cookie : request.getCookies()) {
                if (cookie.getName().equalsIgnoreCase(Attributes.DYID_COOKIE.getName())) {
                    final String dyid = cookie.getValue();
                    getCookieGenerator().addCookie(response, dyid);
                }
            }
        }
        
    }

    @Override
    public void deleteCookie(final HttpServletRequest request, final HttpServletResponse response) {
        if (!request.isSecure()) {
            LOG.error("Cannot remove secure DY Cookie during an insecure request. I should have been called from a secure page.");
        } else {
            // Its a secure page, we can delete the cookie
            getCookieGenerator().removeCookie(response);
        }
    }

    

    protected CookieGenerator getCookieGenerator() {
        return cookieGenerator;
    }

    @Required
    public void setCookieGenerator(final CookieGenerator cookieGenerator) {
        this.cookieGenerator = cookieGenerator;
    }

    protected SecureRandom getRandom() {
        return random;
    }

    protected MessageDigest getSha() {
        return sha;
    }

}
