/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto;

import com.namics.distrelec.b2b.core.model.DistReplacementReasonModel;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;

/**
 * This DTO represents a hybris product reference.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class PimProductReferenceDto {

    private String sourceCode;

    private String targetPimId;

    private ProductReferenceTypeEnum productReferenceType;

    private DistReplacementReasonModel replacementReason;

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(final String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getTargetPimId() {
        return targetPimId;
    }

    public void setTargetPimId(final String targetPimId) {
        this.targetPimId = targetPimId;
    }

    public ProductReferenceTypeEnum getProductReferenceType() {
        return productReferenceType;
    }

    public void setProductReferenceType(final ProductReferenceTypeEnum productReferenceType) {
        this.productReferenceType = productReferenceType;
    }

    public DistReplacementReasonModel getReplacementReason() {
        return replacementReason;
    }

    public void setReplacementReason(final DistReplacementReasonModel replacementReason) {
        this.replacementReason = replacementReason;
    }
}
