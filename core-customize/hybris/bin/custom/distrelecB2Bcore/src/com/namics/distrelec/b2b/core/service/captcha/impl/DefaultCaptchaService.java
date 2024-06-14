package com.namics.distrelec.b2b.core.service.captcha.impl;

import com.namics.distrelec.b2b.core.service.captcha.CaptchaService;
import com.namics.distrelec.b2b.core.service.captcha.CaptchaType;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

public class DefaultCaptchaService implements CaptchaService {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public int getCurrentNumberOfAttempts(CaptchaType captchaType) {
        return sessionService.getAttribute(captchaType.getValue()) == null ? 0 : Integer.parseInt(sessionService.getAttribute(captchaType.getValue()).toString());
    }

    @Override
    public boolean isLimitExceeded(CaptchaType captchaType) {
        final int maxPasswordResetAttempts = configurationService.getConfiguration().getInt(captchaType.getMaxAttemptsKey(), 3);
        return getCurrentNumberOfAttempts(captchaType) > maxPasswordResetAttempts;
    }

    @Override
    public void increaseCurrentAttempt(CaptchaType captchaType) {
        final int currentPasswordResetAttempt = getCurrentNumberOfAttempts(captchaType);
        sessionService.setAttribute(captchaType.getValue(), currentPasswordResetAttempt + 1);
    }

    @Override
    public boolean shouldDisplayCaptcha(CaptchaType... captchaTypes) {
        return Arrays
                .stream(captchaTypes)
                .anyMatch(captchaType -> getCurrentNumberOfAttempts(captchaType) >= getMaxAttempts(captchaType.getMaxAttemptsKey()));
    }

    private int getMaxAttempts(String maxAttemptsKey) {
        return configurationService.getConfiguration().getInt(maxAttemptsKey, 3);
    }
}
