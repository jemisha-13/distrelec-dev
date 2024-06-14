/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.namics.distrelec.b2b.core.enums.GenericSanityCheckType;
import com.namics.distrelec.b2b.core.model.jobs.DistGenericSanityCheckCronJobModel;

import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

/**
 * Tests the {@link DistGenericSanityCheckCronJobValidateInterceptor} class.
 * 
 * @author pforster, Namics AG
 * @since Distrelec 3.0.0
 * 
 */
public class DistGenericSanityCheckCronJobValidateInterceptorTest {

    private static final double NEGAVITE_VALUE = -0.05;

    private static final int POSITIVE_VALUE_OVER_100 = 105;

    private static final int POSITIVE_VALUE_UNDER_100 = 95;

    private static final String EXAMPLE_BASE_QUERY = "select count(*) from {test}";

    @Mock
    private L10NService l10nService;

    private DistGenericSanityCheckCronJobValidateInterceptor interceptor = new DistGenericSanityCheckCronJobValidateInterceptor();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        interceptor.setL10nService(l10nService);
    }

    @Test
    public void testOnValidateValidAbsoluteType() {
        DistGenericSanityCheckCronJobModel model = getAbsoluteType();
        model.setThreshold(new Double(POSITIVE_VALUE_OVER_100));
        try {
            interceptor.onValidate(model, null);
        } catch (InterceptorException e) {
            Assert.fail("A threshold of " + POSITIVE_VALUE_OVER_100 + " is a valid state and shouldn't throw an exception.");
        }
    }

    @Test
    public void testOnValidateValidAbsoluteTypeZeroThreshold() {
        DistGenericSanityCheckCronJobModel model = getAbsoluteType();
        model.setThreshold(new Double(0));
        try {
            interceptor.onValidate(model, null);
        } catch (InterceptorException e) {
            Assert.fail("A threshold of 0 is a valid state and shouldn't throw an exception.");
        }
    }

    @Test(expected = InterceptorException.class)
    public void testOnValidateInvalidAbsoluteType() throws InterceptorException {
        DistGenericSanityCheckCronJobModel model = getAbsoluteType();
        model.setThreshold(new Double(NEGAVITE_VALUE));
        interceptor.onValidate(model, null);
    }

    @Test
    public void testOnValidateValidThresholdAndValidQueryPercentageType() {
        DistGenericSanityCheckCronJobModel model = getPercentageType();
        model.setThreshold(new Double(POSITIVE_VALUE_UNDER_100));
        model.setBaseQuery(EXAMPLE_BASE_QUERY);
        try {
            interceptor.onValidate(model, null);
        } catch (InterceptorException e) {
            Assert.fail("A threshold of " + POSITIVE_VALUE_UNDER_100 + " is a valid state and shouldn't throw an exception.");
        }
    }

    @Test(expected = InterceptorException.class)
    public void testOnValidateValidThresholdAndInvalidQueryPercentageType() throws InterceptorException {
        DistGenericSanityCheckCronJobModel model = getPercentageType();
        model.setThreshold(new Double(POSITIVE_VALUE_OVER_100));
        model.setBaseQuery(null);
        interceptor.onValidate(model, null);
    }

    @Test(expected = InterceptorException.class)
    public void testOnValidateInvalidThresholdAndValidQueryPercentageType() throws InterceptorException {
        DistGenericSanityCheckCronJobModel model = getPercentageType();
        model.setThreshold(new Double(NEGAVITE_VALUE));
        model.setBaseQuery(EXAMPLE_BASE_QUERY);
        interceptor.onValidate(model, null);
    }

    @Test(expected = InterceptorException.class)
    public void testOnValidateInvalidThresholdAndInvalidQueryPercentageType() throws InterceptorException {
        DistGenericSanityCheckCronJobModel model = getPercentageType();
        model.setThreshold(new Double(POSITIVE_VALUE_OVER_100));
        model.setBaseQuery(null);
        interceptor.onValidate(model, null);
    }

    private DistGenericSanityCheckCronJobModel getPercentageType() {
        DistGenericSanityCheckCronJobModel model = new DistGenericSanityCheckCronJobModel();
        model.setType(GenericSanityCheckType.PERCENTAGESANITYCHECK);
        return model;
    }

    private DistGenericSanityCheckCronJobModel getAbsoluteType() {
        DistGenericSanityCheckCronJobModel model = new DistGenericSanityCheckCronJobModel();
        model.setType(GenericSanityCheckType.ABSOLUTSANITYCHECK);
        return model;
    }
}
