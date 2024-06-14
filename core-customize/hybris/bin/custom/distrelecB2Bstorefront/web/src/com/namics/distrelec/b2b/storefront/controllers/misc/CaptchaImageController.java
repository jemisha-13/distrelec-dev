/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.misc;

import com.namics.distrelec.b2b.storefront.controllers.AbstractController;
import de.hybris.platform.servicelayer.session.SessionService;
import nl.captcha.Captcha;
import nl.captcha.servlet.CaptchaServletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class CaptchaImageController extends AbstractController {

    @Autowired
    private SessionService sessionService;

    @RequestMapping("/captcha")
    public void writeCaptcha(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final Captcha captcha = new Captcha.Builder(220, 50).addText().addNoise().build();
        CaptchaServletUtil.writeImage(response, captcha.getImage());
        getSessionService().setAttribute(Captcha.NAME, captcha);
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

}
