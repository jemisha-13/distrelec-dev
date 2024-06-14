/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.converter.suggest;

import java.math.BigDecimal;
import java.util.Map;

import com.namics.distrelec.b2b.core.suggestion.SuggestionEnergyEfficencyData;
import com.namics.hybris.ffsearch.populator.suggestion.DistFactFinderEnergyEfficiencyPopulator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.hybris.ffsearch.common.PriceValueParser;
import com.namics.hybris.ffsearch.data.suggest.ProductSuggestion;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;
import com.namics.hybris.ffsearch.populator.common.PriceFilterTranslator;

import de.factfinder.webservice.ws71.FFsearch.ResultSuggestion;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

/**
 * Converter for a {@link ResultSuggestion} to a {@link ProductSuggestion}.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class ProductSuggestionConverter extends AbstractSuggestionConverter<ResultSuggestion, ProductSuggestion> {

    private static final Logger LOG = LoggerFactory.getLogger(ProductSuggestionConverter.class);

    private final Gson GSON = new Gson();

    private BaseStoreService baseStoreService;
    private CommonI18NService commonI18NService;
    private DistSalesOrgService distSalesOrgService;
    private DistFactFinderEnergyEfficiencyPopulator distFactFinderEnergyEfficiencyPopulator;

    @Override
    public ProductSuggestion convert(final ResultSuggestion source, final ProductSuggestion target) {
        target.setId(source.getName());
        target.setCodeErpRelevant(getCodeSap(source));
        target.setName(getTitle(source));
        target.setTypeName(getTypeName(source));
        target.setPrice(getPrice(source));
        target.setCurrencyIso(getCurrencyIso());
        target.setItems_min(getItemsMin(source));
        target.setItems_step(getItemsStep(source));
        target.setUrl(getUrl(source));
        target.setImg_url(getImageUrl(source));
        target.setSalesUnit(getSalesUnit(source));
        target.setManufacturer(getManufacturer(source));
        target.setEnergyEfficiencyData(getEnergyEfficiency(source));
        return target;
    }

    private BigDecimal getPrice(final ResultSuggestion source) {
        final CurrencyModel currency = getCommonI18NService().getCurrentCurrency();
        final BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
        final String pricePattern = PriceFilterTranslator.buildPricePattern(currency, baseStore);

        final String priceRow = getValue(DistFactFinderExportColumns.PRICE.getValue(), source);
        final String priceValue = PriceValueParser.getPrice(pricePattern, priceRow);

        try {
            return new BigDecimal(priceValue);
        } catch (final NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private String getCurrencyIso() {
        return getCommonI18NService().getCurrentCurrency().getIsocode();
    }

    private String getTypeName(final ResultSuggestion source) {
        return getValue(DistFactFinderExportColumns.TYPENAME.getValue(), source);
    }

    private String getItemsMin(final ResultSuggestion source) {
        return getValue(DistFactFinderExportColumns.ITEMS_MIN.getValue(), source);
    }

    private String getItemsStep(final ResultSuggestion source) {
        return getValue(DistFactFinderExportColumns.ITEMS_STEP.getValue(), source);
    }

    private String getSalesUnit(final ResultSuggestion source) {
        return getValue(DistFactFinderExportColumns.SALESUNIT.getValue(), source);
    }

    protected String getImageUrl(final ResultSuggestion source) {
        final String json = getValue(DistFactFinderExportColumns.ADDITIONAL_IMAGE_URLS.getValue(), source);
        if (StringUtils.isNotBlank(json)) {
            try {
                final Map<String, String> additionalImageUrls = GSON.fromJson(json, Map.class);
                final String imageUrl = additionalImageUrls.get(DistConstants.MediaFormat.LANDSCAPE_SMALL);
                if (StringUtils.isNotBlank(imageUrl)) {
                    return imageUrl;
                }
            } catch (final JsonSyntaxException e) {
                LOG.error("Could not parse JSON of AdditionalImageURLs", e);
            }
        }

        return null;
    }

    protected String getCodeElfa(final ResultSuggestion source) {
        return getValue(DistFactFinderExportColumns.PRODUCT_NUMBER_ELFA.getValue(), source);
    }

    protected String getCodeMovex(final ResultSuggestion source) {
        return getValue(DistFactFinderExportColumns.PRODUCT_NUMBER_MOVEX.getValue(), source);
    }

    protected String getCodeSap(final ResultSuggestion source) {
        return getValue(DistFactFinderExportColumns.PRODUCT_NUMBER_SAP.getValue(), source);
    }

    private String getTitle(final ResultSuggestion source) {
        return getValue(DistFactFinderExportColumns.TITLE.getValue(), source);
    }

    private String getManufacturer(final ResultSuggestion source) {
        return getValue(DistFactFinderExportColumns.MANUFACTURER.getValue(), source);
    }

    private SuggestionEnergyEfficencyData getEnergyEfficiency(final ResultSuggestion source) {
        String energyEfficiencyJson = getValue(DistFactFinderExportColumns.ENERGY_EFFICIENCY.getValue(), source);
        SuggestionEnergyEfficencyData energyData = new SuggestionEnergyEfficencyData();
        getDistFactFinderEnergyEfficiencyPopulator().populate(energyEfficiencyJson, energyData);
        if (StringUtils.isNotBlank(energyData.getEfficency()) || StringUtils.isNotBlank(energyData.getPower())) {
            energyData.setManufacturer(getManufacturer(source));
            energyData.setType(getTypeName(source));
            return energyData;
        }
        return null;
    }

    @Override
    protected ProductSuggestion createTarget() {
        return new ProductSuggestion();
    }

    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

    public DistFactFinderEnergyEfficiencyPopulator getDistFactFinderEnergyEfficiencyPopulator() {
        return distFactFinderEnergyEfficiencyPopulator;
    }

    public void setDistFactFinderEnergyEfficiencyPopulator(final DistFactFinderEnergyEfficiencyPopulator distFactFinderEnergyEfficiencyPopulator) {
        this.distFactFinderEnergyEfficiencyPopulator = distFactFinderEnergyEfficiencyPopulator;
    }
}
