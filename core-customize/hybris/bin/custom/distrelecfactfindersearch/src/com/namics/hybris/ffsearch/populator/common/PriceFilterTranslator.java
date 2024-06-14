/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.common;

import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.GROSS;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.MIN;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.NET;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.PRICE_LIST;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.PRICE;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.namics.hybris.ffsearch.data.search.SearchRequest;

import de.factfinder.webservice.ws71.FFsearch.ArrayOfCustomParameter;
import de.factfinder.webservice.ws71.FFsearch.ArrayOfString;
import de.factfinder.webservice.ws71.FFsearch.CustomParameter;
import de.factfinder.webservice.ws71.FFsearch.Params;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Translator {@link CurrencyModel} ] {@link BaseStoreModel} to their {@link CustomParameter} on a {@link SearchRequest} and back.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class PriceFilterTranslator {

    private static final Logger LOG = LoggerFactory.getLogger(PriceFilterTranslator.class);

    /**
     * Translates a facet code like e.g. "EUR;Gross;Min" back to its name "Price".
     * 
     * For all other facets (e.g. Title or ProductNumber), the name is returned;
     * 
     * @param facetName
     *            facetName to check for being a "Price".
     * @return the name facet.
     */
    public static String getPriceSensitiveFacetName(final String facetName) {
        return SortCodeTranslator.getSortName(facetName);
    }

    /**
     * Prices needs to be referred to using the special code of the priceFilter attribute used when making the search request e.g.
     * "EUR;Gross;Min". </br>
     * 
     * For all other facets (e.g. Title or ProductNumber), the sort code is identical to its name.
     * 
     * @param facetName
     *            name of the facet
     * @param request
     *            the search request made to obtain the facetName
     * @return a valid facet name which the FactFinder instance understands.
     */
    public static String getPriceSensitiveFacetCode(final String facetName, final SearchRequest request) {
        if (!StringUtils.equals(PRICE.getValue(), facetName)) {
            return facetName;
        }
        // Prices have to be referred to according to the used priceList custom filter in the {@link RequestQueryPopulator}
        // e.g. "EUR;Gross;Min"
        final String priceFilter = getPriceFilterValue(request);
        return StringUtils.isNotBlank(priceFilter) ? priceFilter : facetName;
    }

    /**
     * Delivers the the value of any current {@link CustomParameter} price min filter, e.g. "CHF;Gross;Min" set on search request
     * parameters.
     * 
     * @param request
     *            search request to parse the custom price filter value from.
     * @return the value of the CustomParameter e.g. "CHF;Gross;Min". The empty string "" if none could be found.
     */
    public static String getPriceFilterValue(final SearchRequest request) {
        if (request == null || request.getSearchParams() == null) {
            return StringUtils.EMPTY;
        }
        return getPriceFilterValue(request.getSearchParams());
    }

    /**
     * Delivers the the value e.g. "CHF;Gross;Min" of any current {@link CustomParameter} price min filter set on search request parameters.
     * 
     * @param params
     *            parameters of a search request to parse the custom price filter value from.
     * @return the value of the CustomParameter e.g. "CHF;Gross;Min". The empty string "" if none could be found.
     */
    public static String getPriceFilterValue(final Params params) {
        if (params == null || params.getDetailCustomParameters() == null) {
            return StringUtils.EMPTY;
        }
        final ArrayOfCustomParameter customParams = params.getDetailCustomParameters();
        if (customParams == null || customParams.getCustomParameter().isEmpty() || customParams.getCustomParameter().get(0).getValues() == null) {
            return StringUtils.EMPTY;
        }
        final CustomParameter priceParam = customParams.getCustomParameter().get(0);
        if (!StringUtils.equals(PRICE_LIST, priceParam.getName())) {
            return StringUtils.EMPTY;
        }
        final String currencyNet = customParams.getCustomParameter().get(0).getValues().getString().get(0); // e.g. EUR;Gross
        return getPriceFilterValue(currencyNet);
    }

    private static String getPriceFilterValue(final String currencyNet) {
        return new StringBuilder(currencyNet).append(";").append(MIN).toString();
    }

    public static Optional<CustomParameter> buildPriceFilterValue(final CurrencyModel currency, final BaseStoreModel baseStore) {
        if (currency == null || baseStore == null) {
            LOG.error("Unable to build the priceList filter for a search request. Currency/BaseStore are not available.");
            return Optional.absent();
        }
        final CustomParameter priceFilter = new CustomParameter();
        priceFilter.setValues(new ArrayOfString());
        priceFilter.setName(PRICE_LIST);
        priceFilter.getValues().getString().add(buildCurrencyNetString(currency, baseStore));
        return Optional.fromNullable(priceFilter);
    }

    private static String buildCurrencyNetString(final CurrencyModel currency, final BaseStoreModel baseStore) {
        final StringBuilder currencyNet = new StringBuilder(StringUtils.upperCase(currency.getIsocode()));
        if (baseStore.isNet()) {
            currencyNet.append(";").append(NET);
        } else {
            currencyNet.append(";").append(GROSS);
        }
        return currencyNet.toString();
    }

    public static String buildPricePattern(final CurrencyModel currency, final BaseStoreModel baseStore) {
        final String currencyNet = buildCurrencyNetString(currency, baseStore);
        return getPriceFilterValue(currencyNet);
    }

}
