/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.order.strategies.impl.dibs;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;

public class CreditCardPaymentParamsStrategyTest extends DibsPaymentParamStrategy {

    private CreditCardPaymentParamsStrategy strategy;
    private CartModel cartModel;
    private DecimalFormat format;
    private CurrencyModel currencyModel;

    @Before
    public void setup() {
        strategy = new CreditCardPaymentParamsStrategy();
        cartModel = Mockito.mock(CartModel.class);
        currencyModel = Mockito.mock(CurrencyModel.class);
        format = (DecimalFormat) DecimalFormat.getInstance();
        format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.GERMANY));
        format.setGroupingUsed(false);
    }

    /**
     * test if it fails on slightly different amounts
     */
    @Test
    public void validPaymentDataTestFail() {
        BDDMockito.given(cartModel.getTotalPrice()).willReturn(10.01d);
        BDDMockito.given(currencyModel.getDigits()).willReturn(2);
        BDDMockito.given(cartModel.getCurrency()).willReturn(currencyModel);
        final String totalsum = "10.00";

        Assert.assertFalse("", strategy.validPaymentData(cartModel, totalsum));
    }

    /**
     * test with a lot of random numbers
     */
    @Test
    public void validPaymentDataTestOK() {
        final Random random = new Random();

        for (int i = 0; i < 1000; i++) {
            int nextInt = random.nextInt();

            if (nextInt < 0) {
                nextInt = nextInt * -1;
            }

            validPaymentDataSubTest(nextInt);
        }
    }

    private void validPaymentDataSubTest(final int random) {
        final double randomDouble = new Integer(random).doubleValue() / 100;

        BDDMockito.given(cartModel.getTotalPrice()).willReturn(randomDouble);

        final String totalsum = format.format(randomDouble);

        Assert.assertTrue("validate failes for input [" + random + "], cart.getTotalPrice [" + cartModel.getTotalPrice() + "], totalsum [" + totalsum + "] ",
                strategy.validPaymentData(cartModel, totalsum));
    }

}
