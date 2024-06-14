/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto;

/**
 * This DTO represents an "Attribute" XML element.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class PimAttributeDto {

    private String code;
    private String name;
    private Boolean multiValued;
    private Boolean calculated;
    private Boolean embedded;
    private String unitCode;
    private String attributeTypeCode;

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Boolean getMultiValued() {
        return multiValued;
    }

    public void setMultiValued(final Boolean multiValued) {
        this.multiValued = multiValued;
    }

    public Boolean getCalculated() {
        return calculated;
    }

    public void setCalculated(final Boolean calculated) {
        this.calculated = calculated;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(final String unitCode) {
        this.unitCode = unitCode;
    }

    public String getAttributeTypeCode() {
        return attributeTypeCode;
    }

    public void setAttributeTypeCode(final String attributeTypeCode) {
        this.attributeTypeCode = attributeTypeCode;
    }

    public Boolean getEmbedded() {
        return embedded;
    }

    public void setEmbedded(final Boolean embedded) {
        this.embedded = embedded;
    }
}
