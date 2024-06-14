/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter.populator;

import static com.namics.distrelec.b2b.facades.product.converters.populator.ProductVolumePricesPopulator.calculateSaving;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.MIN;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.PRICE;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.STANDARD_PRICE;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.namics.distrelec.b2b.facades.product.DistPriceDataFactory;
import com.namics.distrelec.b2b.facades.product.converters.populator.ProductVolumePricesPopulator;
import com.namics.distrelec.b2b.facades.product.converters.populator.ProductVolumePricesPopulator.VolumePrices;
import com.namics.distrelec.b2b.facades.search.converter.SearchResultProductConverter;
import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

/**
 * Populator for {@link PriceData} on {@link ProductData}.
 *
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public class SearchResultPricePopulator extends AbstractSearchResultPopulator {

    private static final Logger LOG = LoggerFactory.getLogger(SearchResultProductConverter.class);

    public static final String DELIMITER = "|";

    @Autowired
    private DistPriceDataFactory priceDataFactory;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private BaseStoreService baseStoreService;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelService modelService;

    @Override
    public void populate(final SearchResultValueData source, final ProductData target) {
        final String priceValues = this.getValue(source, PRICE.getValue());
        final String standardPriceValues = this.getValue(source, STANDARD_PRICE.getValue());

        final Map<Long, Map<String, PriceData>> volumePrices = new TreeMap<>();
        convertPrices(priceValues, standardPriceValues, volumePrices);
        VolumePrices.setMaxQuantities(volumePrices);
        target.setVolumePricesMap(volumePrices);
        // set price property (used for webtrekk)
        final Set<Entry<Long, Map<String, PriceData>>> entrySet = volumePrices.entrySet();
        if (CollectionUtils.isNotEmpty(entrySet)) {
            final Map<String, PriceData> priceMap = entrySet.iterator().next().getValue();
            if (priceMap != null) {
                target.setPrice(priceMap.get(ProductVolumePricesPopulator.CUSTOM));
                calculateSaving(target, priceMap.get(ProductVolumePricesPopulator.LIST), priceMap.get(ProductVolumePricesPopulator.CUSTOM));
            }
        }
    }

    private void convertPrices(final String priceValues, String standardPriceValues, final Map<Long, Map<String, PriceData>> volumePrices) {
        if (StringUtils.isBlank(priceValues)) {
            return;
        }

        CurrencyModel currency = cmsSiteService.getCurrentSite().getDefaultCurrency();
        if (currency == null) {
            currency = baseStoreService.getCurrentBaseStore().getDefaultCurrency() != null ? baseStoreService.getCurrentBaseStore().getDefaultCurrency()
                                                                                           : commonI18NService.getCurrentCurrency();
        }
        final String netGross = getNetGross();

        for (final String volumePrice : StringUtils.split(priceValues, DELIMITER)) {
            final Optional<PriceData> priceItem = buildVolumePriceItem(volumePrice, currency, netGross);
            if (priceItem.isPresent()) {
                final Map<String, PriceData> priceMap = Maps.newHashMap();
                final PriceData price = priceItem.get();
                priceMap.put(ProductVolumePricesPopulator.CUSTOM, price);

                if (StringUtils.isNotBlank(standardPriceValues)) {
                    addListPrice(standardPriceValues, volumePrices, currency, netGross, priceMap, price);
                }

                if (!volumePrices.containsKey(price.getMinQuantity())) {
                    volumePrices.put(price.getMinQuantity(), priceMap);
                }
            }
        }
    }

    private void addListPrice(String standardPriceValues, Map<Long, Map<String, PriceData>> volumePrices, CurrencyModel currency, String netGross,
                              Map<String, PriceData> priceMap, PriceData price) {
        for (final String standardPrice : StringUtils.split(standardPriceValues, DELIMITER)) {
            final Optional<PriceData> standardPriceItem = builStandardPriceItem(standardPrice, currency, netGross);
            if (standardPriceItem.isPresent()) {
                priceMap.put(ProductVolumePricesPopulator.LIST, standardPriceItem.get());
            }
        }
    }

    private String getNetGross() {
        BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
        if (userService.getCurrentUser() instanceof EmployeeModel) {
            final BaseStoreModel baseEx = new BaseStoreModel();
            baseEx.setUid("distrelec_CH_b2c");
            baseStore = modelService.getByExample(baseEx);
        }
        if (baseStore.isNet()) {
            return DistrelecfactfindersearchConstants.NET;
        } else {
            return DistrelecfactfindersearchConstants.GROSS;
        }
    }

    /**
     * CHF;Net;1=46 or CHF;Net;Min=46
     */
    private Optional<PriceData> buildVolumePriceItem(final String volumePrice, final CurrencyModel currency, final String netGross) {
        final String[] volumePriceItems = StringUtils.split(volumePrice, ';');

        // CHF;Net;1=64, but don't parse duplicated CHF;Net;Min=46

        // Check with isValidPriceType is required because in FactFinder Campaign and Recommender WebServices all prices are delivered by
        // FactFinder

        if (volumePriceItems.length != 3 || StringUtils.contains(volumePrice, MIN) || !isValidPriceType(volumePriceItems, currency, netGross)) {
            return Optional.absent();
        }
        final String currencyIsoCode = volumePriceItems[0];
        final String[] scaledPriceValues = StringUtils.split(volumePriceItems[2], '=');
        // 100=94
        if (scaledPriceValues.length != 2) {
            return Optional.absent();
        }
        try {
            final Long scale = Long.valueOf(scaledPriceValues[0]);
            final Double value = Double.valueOf(scaledPriceValues[1]);
            return Optional.of(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(value.doubleValue()), currencyIsoCode, scale.longValue()));
        } catch (final NumberFormatException e) {
            LOG.error("Failed parsing pricescale/pricevalue values for string [{}]. ", scaledPriceValues, e);
        }
        return Optional.absent();
    }

    private Optional<PriceData> builStandardPriceItem(final String volumePrice, final CurrencyModel currency, final String netGross) {
        final String[] volumePriceItems = StringUtils.split(volumePrice, ';');

        if (volumePriceItems.length != 3 || !isValidPriceType(volumePriceItems, currency, netGross)) {
            return Optional.absent();
        }
        final String currencyIsoCode = volumePriceItems[0];
        final String[] scaledPriceValues = StringUtils.split(volumePriceItems[2], '=');
        // 100=94
        if (scaledPriceValues.length != 2) {
            return Optional.absent();
        }
        try {
            final Long scale = Long.valueOf(scaledPriceValues[0]);
            final Double value = Double.valueOf(scaledPriceValues[1]);
            return Optional.of(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(value.doubleValue()), currencyIsoCode, scale.longValue()));
        } catch (final NumberFormatException e) {
            LOG.error("Failed parsing pricescale/pricevalue values for string [{}]. ", scaledPriceValues, e);
        }
        return Optional.absent();
    }

    private boolean isValidPriceType(final String[] volumePriceItems, final CurrencyModel currency, final String netGross) {
        final boolean currencyValid = ArrayUtils.contains(volumePriceItems, currency.getIsocode());
        final boolean netGrossValid = ArrayUtils.contains(volumePriceItems, netGross);
        return currencyValid && netGrossValid;
    }
}
