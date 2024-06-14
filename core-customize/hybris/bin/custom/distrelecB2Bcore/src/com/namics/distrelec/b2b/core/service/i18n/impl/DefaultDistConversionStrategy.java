/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.i18n.impl;

import java.math.BigDecimal;

import de.hybris.platform.servicelayer.i18n.impl.DefaultConversionStrategy;

/**
 * DefaultDistConversionStrategy extends DefaultConversionStrategy
 * 
 * @author daehusir, Namics AG
 * @since Distrelec 1.0
 * 
 */
public class DefaultDistConversionStrategy extends DefaultConversionStrategy {

    @Override
    public double round(final double value, final int digits) {
        return super.round(value, BigDecimal.valueOf(value).scale());
    }
}
