/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.interceptor;

import org.apache.commons.lang.StringUtils;

import com.namics.distrelec.b2b.core.model.cms2.components.DistrelecCategoryManagerCardComponentModel;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * {@code DistrelecCategoryManagerCardComponentValidateInterceptor}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.7
 */
public class DistrelecCategoryManagerCardComponentValidateInterceptor implements ValidateInterceptor {

    private static final int NAME_MAX_LENGTH = 25;
    private static final int JOB_MAX_LENGTH = 40;
    private static final int ORG_MAX_LENGTH = 25;
    private static final int QUOTE_MAX_LENGTH = 50;
    private static final int TIPP_MAX_LENGTH = 150;

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.interceptor.ValidateInterceptor#onValidate(java.lang.Object,
     * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
     */
    @Override
    public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException {
        if (!(model instanceof DistrelecCategoryManagerCardComponentModel)) {
            return;
        }

        final DistrelecCategoryManagerCardComponentModel component = (DistrelecCategoryManagerCardComponentModel) model;
        validate(component.getManagerName(), NAME_MAX_LENGTH, DistrelecCategoryManagerCardComponentModel.MANAGERNAME);
        validate(component.getJobTitle(), JOB_MAX_LENGTH, DistrelecCategoryManagerCardComponentModel.JOBTITLE);
        validate(component.getOrganisation(), ORG_MAX_LENGTH, DistrelecCategoryManagerCardComponentModel.ORGANISATION);
        validate(component.getQuote(), QUOTE_MAX_LENGTH, DistrelecCategoryManagerCardComponentModel.QUOTE);
        validate(component.getTipp(), TIPP_MAX_LENGTH, DistrelecCategoryManagerCardComponentModel.TIPP);
    }

    /**
     * Validate that the length of {@code input} does not exceed{@code maxLen}.
     * 
     * @param input
     *            the string to validate.
     * @param maxLen
     *            the max length.
     * @param varName
     *            the input name in the component.
     * @throws InterceptorException
     */
    private void validate(final String input, final int maxLen, final String varName) throws InterceptorException {
        if (StringUtils.length(input) > maxLen) {
            throw new InterceptorException(
                    "The " + varName + " should not be longer than " + maxLen + " characters. Current length is " + StringUtils.length(input));
        }
    }
}
