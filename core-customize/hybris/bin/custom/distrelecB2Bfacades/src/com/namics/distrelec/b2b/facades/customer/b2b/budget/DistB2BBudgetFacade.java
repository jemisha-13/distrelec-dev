/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.customer.b2b.budget;

import java.math.BigDecimal;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BBudgetData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException;

public interface DistB2BBudgetFacade {

    B2BBudgetData getBudgetForCustomer(B2BCustomerModel customer);

    void calculateExceededBudget(final B2BBudgetData budget, final PriceData orderTotal);

    void calculateExceededBudget(final B2BBudgetData budget, final BigDecimal orderTotal);

    B2BBudgetData getExceededBudgetForCart() throws CartException;

    boolean doesOrderRequireApproval(B2BBudgetData budget);

    BigDecimal getExceededBudgetValue(B2BBudgetData budget);

}
