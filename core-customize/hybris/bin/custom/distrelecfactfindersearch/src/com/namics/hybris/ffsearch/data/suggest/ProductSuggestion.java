/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.suggest;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.namics.distrelec.b2b.core.suggestion.SuggestionEnergyEfficencyData;

/**
 * POJO for a product suggestion.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
@SuppressWarnings("PMD")
public class ProductSuggestion extends AbstractSuggestion {

    private String typeName;
    private String items_min;
    private String items_step;
    private BigDecimal price;
    private String formattedPriceValue;
    private String currencyIso;
    private String salesUnit;
    private String manufacturer;
    private SuggestionEnergyEfficencyData energyEfficiencyData;

    // BEGIN GENERATED CODE

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    public String getFormattedPriceValue() {
        return formattedPriceValue;
    }

    public void setFormattedPriceValue(final String formattedPriceValue) {
        this.formattedPriceValue = formattedPriceValue;
    }

    public String getCurrencyIso() {
        return currencyIso;
    }

    public void setCurrencyIso(final String currencyIso) {
        this.currencyIso = currencyIso;
    }

    public String getItems_min() {
        return items_min;
    }

    public void setItems_min(final String items_min) {
        this.items_min = items_min;
    }

    public String getItems_step() {
        return items_step;
    }

    public void setItems_step(final String items_step) {
        this.items_step = items_step;
    }

    public String getSalesUnit() {
        return salesUnit;
    }

    public void setSalesUnit(final String salesUnit) {
        this.salesUnit = salesUnit;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(final String typeName) {
        this.typeName = typeName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(final String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public SuggestionEnergyEfficencyData getEnergyEfficiencyData() {
        return energyEfficiencyData;
    }

    public void setEnergyEfficiencyData(SuggestionEnergyEfficencyData energyEfficiencyData) {
        this.energyEfficiencyData = energyEfficiencyData;
    }

}
