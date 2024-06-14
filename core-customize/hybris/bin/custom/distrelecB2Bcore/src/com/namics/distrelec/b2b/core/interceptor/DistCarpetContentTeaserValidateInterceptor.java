/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.cms2.items.DistCarpetContentTeaserModel;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * Check if manual or search products are defined, not manual and search products are defined and at least 5 products are manual added.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class DistCarpetContentTeaserValidateInterceptor implements ValidateInterceptor {

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    @Autowired
    @Qualifier("cmsSiteService")
    private CMSSiteService cmsSiteService;

    @Override
    public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException {
        checkPicture((DistCarpetContentTeaserModel) model);
    }

    protected void checkPicture(final DistCarpetContentTeaserModel component) throws InterceptorException {
        CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();
        if (currentSite == null) {
            for (CMSSiteModel cmsSite : getCmsSiteService().getSites()) {
                if (cmsSite.getContentCatalogs().contains(component.getCatalogVersion().getCatalog())) {
                    currentSite = cmsSite;
                    break;
                }
            }
        }

        if (currentSite == null) {
            // If the site is still null, we can not do any useful checks, so we skip it
            return;
        }

        if (component.getImageSmall(new Locale(currentSite.getDefaultLanguage().getIsocode())) == null) {
            throw new InterceptorException(getL10nService().getLocalizedString("validations.distcarpet.teaser.nosmallimage"));
        }
    }

    public L10NService getL10nService() {
        return l10nService;
    }

    public void setL10nService(L10NService l10nService) {
        this.l10nService = l10nService;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }
}
