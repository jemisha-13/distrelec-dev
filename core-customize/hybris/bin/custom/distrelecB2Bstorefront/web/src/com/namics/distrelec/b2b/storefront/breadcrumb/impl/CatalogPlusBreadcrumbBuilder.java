/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.breadcrumb.impl;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.site.BaseSiteService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * BreadcrumbBuilder implementation for the breadcrumb of catalog plus products.
 * 
 * @author pnueesch, Namics AG
 * @since Distrelec 1.1
 */
public class CatalogPlusBreadcrumbBuilder {

    private static final String LAST_LINK_CLASS = "active";
    private static final String BREADCRUMB_CATALOG_PLUS = "breadcrumb.catalogplus";

    private BaseSiteService baseSiteService;
    private ConfigurationService configurationService;
    private I18NService i18nService;
    private MessageSource messageSource;

    public List<Breadcrumb> getBreadcrumbs(final ProductModel productModel) {
        final List<Breadcrumb> breadcrumbs = new ArrayList<Breadcrumb>();
        if (productModel != null) {
            final String catalogPlusLabel = getMessageSource().getMessage(BREADCRUMB_CATALOG_PLUS, null, getI18nService().getCurrentLocale());
            final String catalogPlusLabelEN = getMessageSource().getMessage(BREADCRUMB_CATALOG_PLUS, null, Locale.ENGLISH);
            breadcrumbs.add(new Breadcrumb(getCatalogPlusLink(), catalogPlusLabel,catalogPlusLabelEN, null));
            breadcrumbs.add(new Breadcrumb(null, productModel.getName(), productModel.getName(Locale.ENGLISH), LAST_LINK_CLASS));
        }

        return breadcrumbs;
    }

    private String getCatalogPlusLink() {
        if (getBaseSiteService() != null && getBaseSiteService().getCurrentBaseSite() != null) {
            final String catalogPlusLinkKey = DistConstants.PropKey.Breadcrumb.CATALOG_PLUS_LINK + getBaseSiteService().getCurrentBaseSite().getUid();
            return getConfigurationService().getConfiguration().getString(catalogPlusLinkKey, null);
        }
        return null;
    }

    protected ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Required
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    protected BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    @Required
    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    protected I18NService getI18nService() {
        return i18nService;
    }

    @Required
    public void setI18nService(final I18NService i18nService) {
        this.i18nService = i18nService;
    }

    protected MessageSource getMessageSource() {
        return messageSource;
    }

    @Required
    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

}
