package com.namics.distrelec.b2b.core.service.captcha;

public interface CaptchaService {

    int getCurrentNumberOfAttempts(CaptchaType captchaType);

    boolean isLimitExceeded(CaptchaType captchaType);

    void increaseCurrentAttempt(CaptchaType captchaType);

    boolean shouldDisplayCaptcha(CaptchaType... captchaType);
}
