/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.process.email.context;

import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;

/**
 * Extended email velocity context.
 * 
 * @author lstuker, Namics AG
 * @since Distrelec 1.0
 * 
 */
public abstract class AbstractDistEmailContext<T extends BusinessProcessModel> extends AbstractEmailContext<T> {

    public static final String EMAIL_LANGUAGE = "email_language";

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private BaseSiteService baseSiteService;

    private String header;
    private String footer;

    public String getHeader() {
        return header;
    }

    public void setHeader(final String header) {
        this.header = header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(final String header) {
        this.footer = header;
    }

    public String getContactEmail() {
        String email = configurationService.getConfiguration().getString(DistConstants.PropKey.Email.INFO_EMAIL_PREFIX + baseSiteService.getCurrentBaseSite().getUid());
        if (StringUtils.isEmpty(email)) {
            return configurationService.getConfiguration().getString(DistConstants.PropKey.Email.INFO_EMAIL_DEFAULT);
        }
        return email;
    }

    @Override
    public void init(final T businessProcessModel, final EmailPageModel emailPageModel) {
        final BaseSiteModel baseSite = getSite(businessProcessModel);
        if (baseSite != null) {
            put(BASE_SITE, baseSite);
        }
        final SiteBaseUrlResolutionService siteBaseUrlResolutionService = getSiteBaseUrlResolutionService();

        put(BASE_URL, siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSite, false, StringUtils.EMPTY));
        put(SECURE_BASE_URL, siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSite, true, StringUtils.EMPTY));
        put(MEDIA_BASE_URL, siteBaseUrlResolutionService.getMediaUrlForSite(baseSite, false));
        put(MEDIA_SECURE_BASE_URL, siteBaseUrlResolutionService.getMediaUrlForSite(baseSite, true));
        put(THEME, baseSite != null && baseSite.getTheme() != null ? baseSite.getTheme().getCode() : null);
        put(FROM_EMAIL, emailPageModel.getFromEmail());

        final LanguageModel language = getEmailLanguage(businessProcessModel);
        if (language != null) {
            put(EMAIL_LANGUAGE, language);
            String fromName = emailPageModel.getFromName(new Locale(language.getIsocode()));
            if (StringUtils.isBlank(fromName)) {
                fromName = emailPageModel.getFromName();
            }
            put(FROM_DISPLAY_NAME, fromName);
        } else {
            put(FROM_DISPLAY_NAME, emailPageModel.getFromName());
        }

        final CustomerModel customerModel = getCustomer(businessProcessModel);
        if (customerModel != null) {
            final TitleModel title = customerModel.getTitle();
            put(TITLE, (title != null && title.getMailSalutationTemplate() != null) ? title.getMailSalutationTemplate() : StringUtils.EMPTY);
            put(DISPLAY_NAME, customerModel.getDisplayName());
            put(EMAIL, getCustomerEmailResolutionService().getEmailForCustomer(customerModel));
        }
        put("StringEscapeUtils", StringEscapeUtils.class);
    }

    protected String getEmail(final String prefix, final String defaultEmailId) {
        String email = getConfigurationService().getConfiguration().getString(prefix + getBaseSite().getUid());
        if (StringUtils.isEmpty(email)) {
            return getConfigurationService().getConfiguration().getString(defaultEmailId);
        }
        return email;
    }

    @Override
    protected LanguageModel getEmailLanguage(final T businessProcessModel) {
        return getCommonI18NService().getLanguage("en");
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }
}
