/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.export.query;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.namics.distrelec.b2b.core.model.DistPromotionLabelModel;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchParameterProvider;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.hybris.ffsearch.model.DistFactFinderExportChannelModel;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Builder for flexible search query params, used in {@link DistFactFinderProductExportQueryCreator3}.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public class DistFactFinderProductExportParameterProvider implements DistFlexibleSearchParameterProvider<DistFactFinderExportChannelModel> {

    private static final Logger LOG = LoggerFactory.getLogger(DistFactFinderProductExportParameterProvider.class);

    public static final String DATE = "Date";
    public static final String LANGUAGE_ISOCODE = "LanguageIsocode";
    public static final String LANGUAGE_ISOCODE_NO_QUOTE = "CodeLang";
    public static final String EXCLUDED_PRODUCTS = "ExcludedProducts";
    public static final String NUMBER_OF_DELIVERY_COUNTRIES = "NumberOfDeliveryCountries";
    public static final String DELIVERY_COUNTRIES = "DeliveryCountries";
    public static final String ERP_SYSTEM = "ErpSystem";
    public static final String PROMOLABEL_ATTRNAME = "dplParam_";

    // Max. length of VARCHAR2 is 4000 (limit set to 3800 to have some space left for encoding of special characters).
    public static final int MAX_VARCHAR2_LENGTH = 3800;

    private FlexibleSearchService flexibleSearchService;

    @Override
    public Map<String, Object> getPriceExportParameters(final CMSSiteModel cmsSite) {
        Assert.notNull(cmsSite.getSalesOrg(), "SalesOrg must not be null");

        final Map<String, Object> parameters = new HashMap<>();
        // SalesOrg as PK
        parameters.put(DistSalesOrgModel._TYPECODE, Long.valueOf(cmsSite.getSalesOrg().getPk().getLongValue()));
        // CMSSite as PK
        parameters.put(CMSSiteModel._TYPECODE, Long.valueOf(cmsSite.getPk().getLongValue()));
        // Date for pricerows
        parameters.put(DATE, new Date());

        return parameters;
    }

    @Override
    public Map<String, Object> getParameters(final DistFactFinderExportChannelModel channel) {
        Assert.notNull(channel.getCmsSite().getSalesOrg(), "SalesOrg must not be null");
        Assert.notNull(channel.getCmsSite().getCountry(), "Country must not be null");

        final Map<String, Object> parameters = new HashMap<>();

        // SalesOrg as PK
        parameters.put(DistSalesOrgModel._TYPECODE, Long.valueOf(channel.getCmsSite().getSalesOrg().getPk().getLongValue()));
        // CMSSite as PK
        parameters.put(CMSSiteModel._TYPECODE, Long.valueOf(channel.getCmsSite().getPk().getLongValue()));
        // Country as PK
        parameters.put(CountryModel._TYPECODE, Long.valueOf(channel.getCmsSite().getCountry().getPk().getLongValue()));
        // Date for pricerows
        parameters.put(DATE, new Date());
        // Language as PK
        parameters.put(LanguageModel._TYPECODE, Long.valueOf(channel.getLanguage().getPk().getLongValue()));
        // Language ISO code
        parameters.put(LANGUAGE_ISOCODE, channel.getLanguage().getIsocode());
        // Language ISO code no quotes
        parameters.put(LANGUAGE_ISOCODE_NO_QUOTE, channel.getLanguage().getIsocode());
        // ERP System
        parameters.put(ERP_SYSTEM, channel.getCmsSite().getSalesOrg().getErpSystem().getCode());

        putDeliveryCountryParams(parameters, channel);

        parameters.put(EXCLUDED_PRODUCTS, getExcludedProducts(channel.getLanguage()));

        parameters.putAll(getPromotionalParameters(PROMOLABEL_ATTRNAME));

        return parameters;
    }

    private Set<Long> getExcludedProducts(final LanguageModel language) {
        final Set<Long> productPks = new HashSet<>();

        // Check for products that should be excluded
        final StringBuilder query2 = new StringBuilder();
        query2.append("SELECT DISTINCT {p.pk} from {Product as p} WHERE {p.exclude} = 1");
        final FlexibleSearchQuery flexibleSearchQuery2 = new FlexibleSearchQuery(query2.toString());
        final SearchResult<ProductModel> searchResult2 = flexibleSearchService.<ProductModel> search(flexibleSearchQuery2);

        if (searchResult2.getResult().isEmpty()) {
            LOG.debug("No products to exclude");
        } else {
            for (final ProductModel product : searchResult2.getResult()) {
                productPks.add(Long.valueOf(product.getPk().getLongValue()));
            }
        }

        if (productPks.isEmpty()) {
            // Add dummy PK (ensure at least one parameter in 'NOT IN' clause)
            productPks.add(Long.valueOf(PK.NULL_PK.getLongValue()));
        }

        return productPks;
    }

    private void putDeliveryCountryParams(final Map<String, Object> parameters, final DistFactFinderExportChannelModel channel) {
        final Set<Long> deliveryCountries = new HashSet<>();

        for (final BaseStoreModel baseStore : channel.getCmsSite().getStores()) {
            if (CollectionUtils.isNotEmpty(baseStore.getDeliveryCountries())) {
                for (final CountryModel c : baseStore.getDeliveryCountries()) {
                    deliveryCountries.add(Long.valueOf(c.getPk().getLongValue()));
                }
            }
        }

        if (deliveryCountries.isEmpty()) {
            parameters.put(DELIVERY_COUNTRIES, Collections.singletonList(Long.valueOf(0)));
            LOG.warn("No delivery countries for channel with code [{}] found", channel.getCode());
        } else {
            parameters.put(DELIVERY_COUNTRIES, deliveryCountries);
        }

        parameters.put(NUMBER_OF_DELIVERY_COUNTRIES, Integer.valueOf(deliveryCountries.size()));
    }

    protected Map<String, Object> getPromotionalParameters(String paramPrefix) {
        Map<String, Object> parameters = new HashMap<String, Object>(10);
        final StringBuilder query = new StringBuilder();
        query.append("SELECT {dpl.").append(DistPromotionLabelModel.CODE).append("}, ");
        query.append(" 'code:' +  CAST({dpl.").append(DistPromotionLabelModel.CODE).append("} AS varchar(10))");
        query.append("  + ',label:\"' + COALESCE({dpl.").append(DistPromotionLabelModel.NAME).append(":o}, '') ");
        query.append("  + '\",priority:'  +  CAST({dpl.").append(DistPromotionLabelModel.PRIORITY).append("} AS varchar(10))");
        query.append("  + ',rank:'  + CAST({dpl.").append(DistPromotionLabelModel.RANK).append("} AS varchar(10)) AS VALUE ");
        query.append("FROM {").append(DistPromotionLabelModel._TYPECODE).append(" AS dpl} ");

        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query.toString());
        flexibleSearchQuery.setResultClassList(Arrays.asList(String.class, String.class));
        final List<List<String>> searchResultRows = getFlexibleSearchService().<List<String>> search(flexibleSearchQuery).getResult();

        if (searchResultRows != null) {
            for (final List<String> resultRow : searchResultRows) {
                parameters.put(paramPrefix + resultRow.get(0), resultRow.get(1));
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getPromotionalParameters: " + paramPrefix + resultRow.get(0) + " = " + resultRow.get(1));
                }
            }
        }
        return parameters;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    @Required
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

}
