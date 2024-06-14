package com.namics.distrelec.b2b.facades.process.email.context;

import static com.namics.distrelec.b2b.core.constants.DistConstants.UTMParams.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.process.DistNewCustomerActivationProcessModel;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;

public class DistNewCustomerActivationEmailContext extends CustomerEmailContext {
    private static final String TOKEN_PARAM = "token=";

    private static final String LANGUAGE_PARAM = "&lang=";

    private static final String EMAIL_PARAM = "&email=";

    private static final String INITIAL_PASSWORD_URL = "/account/password/setinitialpw";

    @Autowired
    private DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;

    private String token;

    @Override
    public void init(final BusinessProcessModel businessProcess, final EmailPageModel emailPageModel) {
        super.init(businessProcess, emailPageModel);
        if (businessProcess instanceof DistNewCustomerActivationProcessModel) {
            setToken(((DistNewCustomerActivationProcessModel) businessProcess).getToken());
        }
    }

    @Override
    public String getURLEncodedToken() {
        return URLEncoder.encode(token, StandardCharsets.UTF_8);
    }

    public String getSecureResetPasswordUrl() {
        return distSiteBaseUrlResolutionService.getHeadlessWebsiteUrlForSite(getBaseSite(), Boolean.TRUE, INITIAL_PASSWORD_URL, getQueryParams());
    }

    private String getQueryParams() {
        return TOKEN_PARAM + getURLEncodedToken() +
               EMAIL_PARAM + URLEncoder.encode(getCustomer().getUid(), StandardCharsets.UTF_8) +
               LANGUAGE_PARAM + getCustomer().getLanguage().getIsocode() +
               UTM_SOURCE + "br_srv" +
               UTM_MEDIUM + "email" +
               UTM_CAMPAIGN + "rs_customer_activation" +
               UTM_TERM + "rs_customer_activation";
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }
}
