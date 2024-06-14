/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.cms2.items.DistCarpetContentTeaserWithTextModel;

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
public class DistCarpetContentTeaserWithTextValidateInterceptor implements ValidateInterceptor {

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    private static final int MAX_LENGTH_TITLE = 21;
    private static final int MAX_LENGTH_TEXT = 21;
    private static final int MAX_LENGTH_SUBTEXT = 21;

    @Override
    public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException {
        final DistCarpetContentTeaserWithTextModel component = (DistCarpetContentTeaserWithTextModel) model;
        checkContent(component);
    }

    private void checkContent(final DistCarpetContentTeaserWithTextModel component) throws InterceptorException {
        if (component.getImageSmall() == null) {
            throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.teaser.nosmallimage"));
        }
        if (component.getTitle() != null && component.getTitle().length() > MAX_LENGTH_TITLE) {
            throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.teaser.maxlengthtitle"));
        }
        if (component.getText() != null && component.getText().length() > MAX_LENGTH_TEXT) {
            throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.teaser.maxlengthtext"));
        }
        if (component.getSubText() != null && component.getSubText().length() > MAX_LENGTH_SUBTEXT) {
            throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.teaser.maxlengthsubtext"));
        }
    }
}
