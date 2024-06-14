/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.common;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.base.Optional;
import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;

import de.factfinder.webservice.ws71.FFsearch.ArrayOfCustomParameter;
import de.factfinder.webservice.ws71.FFsearch.ArrayOfString;
import de.factfinder.webservice.ws71.FFsearch.CustomParameter;
import de.factfinder.webservice.ws71.FFsearch.Params;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.store.BaseStoreModel;

@UnitTest
public class PriceFilterTranslatorTest {

    @Test
    public void testGetPriceFilterValueParamsNull() {
        // Init
        final Params params = null;

        // Action
        final String priceFilterValue = PriceFilterTranslator.getPriceFilterValue(params);

        // Evaluation
        Assert.assertEquals("Empty String expected", "", priceFilterValue);
    }

    @Test
    public void testGetPriceFilterValueDetailCustomParametersNull() {
        // Init
        final Params params = new Params();
        params.setDetailCustomParameters(null);

        // Action
        final String priceFilterValue = PriceFilterTranslator.getPriceFilterValue(params);

        // Evaluation
        Assert.assertEquals("Empty String expected", "", priceFilterValue);
    }

    @Test
    public void testGetPriceFilterValueCustomParameterEmpty() {
        // Init
        final Params params = new Params();
        final ArrayOfCustomParameter detailCustomParameters = new ArrayOfCustomParameter();
        params.setDetailCustomParameters(detailCustomParameters);

        // Action
        final String priceFilterValue = PriceFilterTranslator.getPriceFilterValue(params);

        // Evaluation
        Assert.assertEquals("Empty String expected", "", priceFilterValue);
    }

    @Test
    public void testGetPriceFilterValueCustomParameterFirstValuesNull() {
        // Init
        final Params params = new Params();
        final ArrayOfCustomParameter customParams = new ArrayOfCustomParameter();
        params.setDetailCustomParameters(customParams);
        final CustomParameter customParameter = new CustomParameter();
        customParams.getCustomParameter().add(customParameter);
        customParameter.setValues(null);

        // Action
        final String priceFilterValue = PriceFilterTranslator.getPriceFilterValue(params);

        // Evaluation
        Assert.assertEquals("Empty String expected", "", priceFilterValue);
    }

    @Test
    public void testGetPriceFilterValueCustomParameterFirstNameNotPriceList() {
        // Init
        final Params params = new Params();
        final ArrayOfCustomParameter customParams = new ArrayOfCustomParameter();
        params.setDetailCustomParameters(customParams);
        final CustomParameter customParameter = new CustomParameter();
        customParams.getCustomParameter().add(customParameter);
        customParameter.setValues(new ArrayOfString());
        customParameter.setName("NotPriceList");

        // Action
        final String priceFilterValue = PriceFilterTranslator.getPriceFilterValue(params);

        // Evaluation
        Assert.assertEquals("Empty String expected", "", priceFilterValue);
    }

    @Test
    public void testGetPriceFilterPositiv() {
        // Init
        final Params params = new Params();
        final ArrayOfCustomParameter customParams = new ArrayOfCustomParameter();
        params.setDetailCustomParameters(customParams);
        final CustomParameter customParameter = new CustomParameter();
        customParams.getCustomParameter().add(customParameter);
        final ArrayOfString values = new ArrayOfString();
        customParameter.setValues(values);
        values.getString().add("CHF;Gross");
        customParameter.setName(DistrelecfactfindersearchConstants.PRICE_LIST);

        // Action
        final String priceFilterValue = PriceFilterTranslator.getPriceFilterValue(params);

        // Evaluation
        Assert.assertEquals("Result not as expected", "CHF;Gross;Min", priceFilterValue);
    }

    @Test
    public void testBuildFilterValueNullParameters() {
        // Init
        final CurrencyModel currency = null;
        final BaseStoreModel baseStore = null;

        // Action
        final Optional<CustomParameter> optionalCustomParameter = PriceFilterTranslator.buildPriceFilterValue(currency, baseStore);

        // Evaluation
        Assert.assertTrue("No present custom parameter expected", !optionalCustomParameter.isPresent());
    }

    @Test
    public void testBuildFilterValueCurrencyNull() {
        // Init
        final CurrencyModel currency = null;
        final BaseStoreModel baseStore = new BaseStoreModel();

        // Action
        final Optional<CustomParameter> optionalCustomParameter = PriceFilterTranslator.buildPriceFilterValue(currency, baseStore);

        // Evaluation
        Assert.assertTrue("No present custom parameter expected", !optionalCustomParameter.isPresent());
    }

    @Test
    public void testBuildFilterValueBaseStoreNull() {
        // Init
        final CurrencyModel currency = new CurrencyModel();
        final BaseStoreModel baseStore = null;

        // Action
        final Optional<CustomParameter> optionalCustomParameter = PriceFilterTranslator.buildPriceFilterValue(currency, baseStore);

        // Evaluation
        Assert.assertTrue("No present custom parameter expected", !optionalCustomParameter.isPresent());
    }

    @Test
    public void testBuildFilterValueNet() {
        // Init
        final CurrencyModel currency = new CurrencyModel();
        currency.setIsocode("CHF");
        final BaseStoreModel baseStore = new BaseStoreModel();
        baseStore.setNet(true);

        // Action
        final Optional<CustomParameter> optionalCustomParameter = PriceFilterTranslator.buildPriceFilterValue(currency, baseStore);

        // Evaluation
        Assert.assertTrue("Valid custom parameter expected", optionalCustomParameter.isPresent());
        Assert.assertEquals("Number of Values not as expected", 1, optionalCustomParameter.get().getValues().getString().size());
        Assert.assertEquals("Value not as expected", "CHF;Net", optionalCustomParameter.get().getValues().getString().get(0));
    }

    @Test
    public void testBuildFilterValueNotNet() {
        // Init
        final CurrencyModel currency = new CurrencyModel();
        currency.setIsocode("CHF");
        final BaseStoreModel baseStore = new BaseStoreModel();
        baseStore.setNet(false);

        // Action
        final Optional<CustomParameter> optionalCustomParameter = PriceFilterTranslator.buildPriceFilterValue(currency, baseStore);

        // Evaluation
        Assert.assertTrue("Valid custom parameter expected", optionalCustomParameter.isPresent());
        Assert.assertEquals("Number of Values not as expected", 1, optionalCustomParameter.get().getValues().getString().size());
        Assert.assertEquals("Value not as expected", "CHF;Gross", optionalCustomParameter.get().getValues().getString().get(0));
    }

}
