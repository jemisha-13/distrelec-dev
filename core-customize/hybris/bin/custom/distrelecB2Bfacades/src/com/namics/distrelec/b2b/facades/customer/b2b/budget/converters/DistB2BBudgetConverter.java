/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.customer.b2b.budget.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.model.DistB2BBudgetModel;

import de.hybris.platform.b2bcommercefacades.company.data.B2BBudgetData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Converts a {@link DistB2BBudgetModel} to {@link B2BBudgetData}.
 *
 * @author dsivakumaran, Namics AG
 * @since Distrelec 1.0
 *
 */
public class DistB2BBudgetConverter extends AbstractPopulatingConverter<DistB2BBudgetModel, B2BBudgetData> {

    @Autowired
    @Qualifier("currencyConverter")
    private Converter<CurrencyModel, CurrencyData> currencyConverter;

    @Override
    public void populate(final DistB2BBudgetModel source, final B2BBudgetData target) {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        target.setCode(source.getCode());
        target.setName(source.getName());
        target.setActive(source.getActive().booleanValue());
        target.setStartDate(source.getDateRange().getStart());
        target.setEndDate(source.getDateRange().getEnd());
        target.setOrderBudget(source.getOrderBudget());
        target.setOriginalYearlyBudget(source.getOriginalYearlyBudget());
        target.setYearlyBudget(source.getYearlyBudget());
        target.setCurrency(currencyConverter.convert(source.getCurrency()));
        super.populate(source, target);
    }
}
