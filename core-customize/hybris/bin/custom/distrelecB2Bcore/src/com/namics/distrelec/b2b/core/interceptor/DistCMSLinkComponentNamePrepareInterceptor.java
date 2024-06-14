/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

/**
 * Set name for cms link component.
 * 
 * @author wspalinger, Namics AG
 * 
 */
public class DistCMSLinkComponentNamePrepareInterceptor implements PrepareInterceptor {

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    @Override
    public void onPrepare(final Object model, final InterceptorContext ctx) throws InterceptorException {
        if (model instanceof CMSLinkComponentModel) {
            final CMSLinkComponentModel link = (CMSLinkComponentModel) model;
            Map<String, Set<Locale>> dirtyAttrs = ctx.getDirtyAttributes(link);
            if (dirtyAttrs.containsKey(CMSLinkComponentModel.NAME)) {
                try {
                    if (StringUtils.isNotBlank(link.getName())) {
                        link.setName(URLEncoder.encode(link.getName(), "UTF-8"));
                    } else {
                        throw new InterceptorException(getL10nService().getLocalizedString("validations.distcmslink.emptyname"));
                    }
                } catch (final UnsupportedEncodingException e) {
                    throw new InterceptorException(getL10nService().getLocalizedString("validations.distcmslink.encodingproblem"), e);
                }
            }
        }
    }

    public L10NService getL10nService() {
        return l10nService;
    }

    public void setL10nService(final L10NService l10nService) {
        this.l10nService = l10nService;
    }
}
