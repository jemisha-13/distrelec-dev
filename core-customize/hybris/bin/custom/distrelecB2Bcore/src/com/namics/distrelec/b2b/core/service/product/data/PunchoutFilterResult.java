/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product.data;

import org.apache.commons.lang.builder.ToStringBuilder;

import de.hybris.platform.core.model.product.ProductModel;

/**
 * Contains the result of a punchout filtering. Which product is punched out and what is the reason.
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 2.0
 * 
 */
public class PunchoutFilterResult {

    private ProductModel punchedOutProduct;
    private String punchedOutProductHierarchy;
    private PunchoutReason punchOutReason;
    private String punchedOutManufacturer;

    public ProductModel getPunchedOutProduct() {
        return punchedOutProduct;
    }

    public void setPunchedOutProduct(ProductModel punchedOutProduct) {
        this.punchedOutProduct = punchedOutProduct;
    }

    public String getPunchedOutProductHierarchy() {
        return punchedOutProductHierarchy;
    }

    public void setPunchedOutProductHierarchy(String punchedOutProductHierarchy) {
        this.punchedOutProductHierarchy = punchedOutProductHierarchy;
    }

    public PunchoutReason getPunchOutReason() {
        return punchOutReason;
    }

    public void setPunchOutReason(PunchoutReason punchOutReason) {
        this.punchOutReason = punchOutReason;
    }

    public String getPunchedOutManufacturer() {
        return punchedOutManufacturer;
    }

    public void setPunchedOutManufacturer(String punchedOutManufacturer) {
        this.punchedOutManufacturer = punchedOutManufacturer;
    }

    /**
     * Punchout Reason enumeration. The ordinal of the enum is also the priority if more than one punchout applies for the same product. The
     * highest prio has the enum with the lowest ordinal
     * 
     * @author daehusir, Distrelec
     * @since Distrelec 2.0
     * 
     */
    public enum PunchoutReason {
        CUSTOMER("CUSTOMER"), MANUFACTURER("MANUFACTURER"), COUNTRY("COUNTRY"), CUSTOMER_TYPE("CUSTOMER_TYPE");

        private final String value;

        PunchoutReason(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
