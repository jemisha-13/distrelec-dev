package com.namics.distrelec.b2b.storefront.util;

import javax.servlet.http.HttpServletRequest;

public interface CaptchaUtil {

    boolean validateCaptcha(final HttpServletRequest request);

    boolean validateReCaptcha(final HttpServletRequest request);

    boolean validateReCaptcha(final HttpServletRequest request, final String gCaptchaResponse);

    String getPublicKey(final HttpServletRequest request);

    String getPrivateKey(final HttpServletRequest request);

    void setSkipCaptcha(HttpServletRequest request);

    boolean isSkipCaptcha(HttpServletRequest request);
}
