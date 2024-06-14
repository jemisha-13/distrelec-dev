/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.v2.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.site.DistrelecBaseStoreService;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;

public class BaseCommerceController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(BaseCommerceController.class);

    @Autowired
    private DistrelecBaseStoreService baseStoreService;

    @Autowired
    private DistCheckoutFacade distCheckoutFacade;

    @Autowired
    private DistrelecCMSSiteService cmsSiteService;

    protected String getErrorMessage(String messageKey) {
        return getMessageSource().getMessage(messageKey, null, getI18nService().getCurrentLocale());
    }

    protected String getDataFormatForCurrentCmsSite() {
        final Locale locale = new Locale(getI18nService().getCurrentLocale().getLanguage(), cmsSiteService.getCurrentSite().getCountry().getIsocode());
        return getMessageSource().getMessage("text.store.dateformat.datepicker.selection", null, "MM/dd/yyyy", locale);
    }

    protected boolean isEShopGroup() {
        return getUserService().isMemberOfGroup(getUserService().getCurrentUser(), getUserService().getUserGroupForUID(DistConstants.User.B2BEESHOPGROUP_UID));
    }

    protected DistrelecBaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    protected DistCheckoutFacade getDistCheckoutFacade() {
        return distCheckoutFacade;
    }

    protected DistrelecCMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }
}
