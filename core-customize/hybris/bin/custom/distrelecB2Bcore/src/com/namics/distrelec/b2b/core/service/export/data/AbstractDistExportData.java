/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.export.data;

import java.util.Map;

import de.hybris.platform.acceleratorservices.dataexport.generic.output.csv.DelimitedFileMethod;

/**
 * DistProductExport Data Object
 * 
 */
public abstract class AbstractDistExportData {

    private String code;
    private String manufacturerName;
    private String typeName;
    private Long quantity;
    private String name;
    private String availability;
    private Integer stockLevel;
    private Map<Long, String> volumePrices;
    private String expiredOn;
    private String replacementReason;
    private String reference;

    public String getCode() {
        return code;
    }

    // @DelimitedFileMethod(position = 1, name = "Art-Nr", nullValue = "")
    @DelimitedFileMethod(position = 2, name = "${distrelec.brand} Article Number", nullValue = "")
    public void setCode(final String code) {
        this.code = code;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public String getReference() {
        return reference;
    }

    @DelimitedFileMethod(position = 3, name = "Reference", nullValue = "\u0000")
    public void setReference(final String reference) {
        this.reference = reference;
    }

    @DelimitedFileMethod(position = 4, name = "Manufacturer", nullValue = "")
    public void setManufacturerName(final String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getTypeName() {
        return typeName;
    }

    // @DelimitedFileMethod(position = 3, name = "Type", nullValue = "")
    @DelimitedFileMethod(position = 5, name = "Manufacturer Article Number", nullValue = "")
    public void setTypeName(final String typeName) {
        this.typeName = typeName;
    }

    public Long getQuantity() {
        return quantity;
    }

    @DelimitedFileMethod(position = 1, name = "Quantity", nullValue = "")
    public void setQuantity(final Long quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    @DelimitedFileMethod(position = 6, name = "Name", nullValue = "")
    public void setName(final String name) {
        this.name = name;
    }

    public String getAvailability() {
        return availability;
    }

    @DelimitedFileMethod(position = 7, name = "Availability", nullValue = "")
    public void setAvailability(final String availability) {
        this.availability = availability;
    }

    public Integer getStockLevel() {
        return stockLevel;
    }

    @DelimitedFileMethod(position = 8, name = "Stock", nullValue = "")
    public void setStockLevel(final Integer stockLevel) {
        this.stockLevel = stockLevel;
    }

    public Map<Long, String> getVolumePrices() {
        return volumePrices;
    }

    @DelimitedFileMethod(position = 10, name = "VolumePrices", nullValue = "")
    public void setVolumePrices(final Map<Long, String> volumePrices) {
        this.volumePrices = volumePrices;
    }

    public String getExpiredOn() {
        return expiredOn;
    }

    @DelimitedFileMethod(position = 9, name = "Expired On", nullValue = "")
    public void setExpiredOn(final String expiredOn) {
        this.expiredOn = expiredOn;
    }

    public String getReplacementReason() {
        return replacementReason;
    }

    // @DelimitedFileMethod(position = 11, name = "Replacement Reason", nullValue = "")
    public void setReplacementReason(final String replacementReason) {
        this.replacementReason = replacementReason;
    }

}
