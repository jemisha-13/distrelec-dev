package com.namics.distrelec.b2b.core.service.captcha;

public enum CaptchaType {

    CHECKOUT_LOGIN_PASSWORD_RESET("PasswordReset", "distrelec.maxPwReset.attempts"),
    GUEST_CHECKOUT("GuestCheckout", "distrelec.max.guest.checkout.attempts"),
    GUEST_CHECKOUT_REGISTRATION("GuestCheckoutRegistration","distrelec.max.guest.checkout.attempts");

    private String value;
    private String maxAttemptsKey;

    CaptchaType(final String value, String maxAttemptsKey) {
        this.value = value;
        this.maxAttemptsKey = maxAttemptsKey;
    }

    public String getValue() {
        return value;
    }

    public String getMaxAttemptsKey() {
        return maxAttemptsKey;
    }
}