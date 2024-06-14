/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.cms2.components.DistProductFFCampaignCarouselComponentModel;

import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * {@code DistProductFFCampaignCarouselComponentValidateInterceptor}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class DistProductFFCampaignCarouselComponentValidateInterceptor implements ValidateInterceptor {

    private static final Integer MIN_PRODUCTS = Integer.valueOf(1);

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    /**
     * Create a new instance of {@code DistProductFFCampaignCarouselComponentValidateInterceptor}
     */
    public DistProductFFCampaignCarouselComponentValidateInterceptor() {
    }

    /*
     * (non-Javadoc)
     * @see de.hybris.platform.servicelayer.interceptor.ValidateInterceptor#onValidate(java.lang.Object,
     * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
     */
    @Override
    public void onValidate(Object model, InterceptorContext ctx) throws InterceptorException {
        if (!(model instanceof DistProductFFCampaignCarouselComponentModel)) {
            return;
        }
        final DistProductFFCampaignCarouselComponentModel component = (DistProductFFCampaignCarouselComponentModel) model;
        if (StringUtils.isBlank(component.getSearchQuery())) {
            throw new InterceptorException(l10nService.getLocalizedString("validations.distproductffcompaigncarousel.emptysearchquery"));
        }

        // Check the minimum values
        if (component.getMaxSearchResults() == null) {
            component.setMaxSearchResults(MIN_PRODUCTS);
        }

        if (component.getMaxSearchResults().compareTo(MIN_PRODUCTS) < 0) {
            throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.lessminsearchresults", new Object[] { MIN_PRODUCTS }));
        }
    }
}
