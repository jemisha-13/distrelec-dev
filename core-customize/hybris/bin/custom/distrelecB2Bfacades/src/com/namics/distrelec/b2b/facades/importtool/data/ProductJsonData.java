/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.importtool.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * {@code ImportJsonData}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.17
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({ "productCode", "quantity", "itemNumber", "reference" })
public class ProductJsonData {

    @JsonProperty("productCode")
    private String productCode;
    @JsonProperty("quantity")
    private long quantity;
    @JsonProperty("itemNumber")
    private String itemNumber;
    @JsonProperty("reference")
    private String reference;

    /**
     * Create a new instance of {@code ImportJsonData}
     */
    public ProductJsonData() {
        this(null, 0L);
    }

    /**
     * Create a new instance of {@code ImportJsonData}
     *
     * @param productCode
     * @param quantity
     */
    public ProductJsonData(final String productCode, final long quantity) {
        this.productCode = productCode;
        this.quantity = quantity;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(final String productCode) {
        this.productCode = productCode;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(final long quantity) {
        this.quantity = quantity;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(final String itemNumber) {
        this.itemNumber = itemNumber;
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (final Exception exp) {
            // NOP
        }
        return super.toString();
    }
}
