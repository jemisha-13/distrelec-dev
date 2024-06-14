package com.namics.distrelec.b2b.core.inout.pim.servicelayer;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;

/**
 * Representation of image types.
 * 
 * @author ascherrer, Namics AG
 * @since 1.0
 */
public enum PimProductReferenceType {

	ACCESSORY(ProductReferenceTypeEnum.DIST_ACCESSORY, "Accessory"), // Avoid line break
    MANDATORY(ProductReferenceTypeEnum.MANDATORY, "DIS_Required_Accessory"),
    CROSS_SELLING(ProductReferenceTypeEnum.CROSSELLING, "DIS_Associated"),
    CONSISTENT_WITH(ProductReferenceTypeEnum.DIST_CONSISTENT_WITH, "ConsistenWith"), // Avoid line break
    SIMILAR(ProductReferenceTypeEnum.DIST_SIMILAR, "similar_products"),
	ALTERNATIVEUPGRADE(ProductReferenceTypeEnum.DIS_ALTERNATIVE_UPGRADE, "DIS_Alternative_Upgrade"),
	ALTERNATIVECALIBRATED(ProductReferenceTypeEnum.DIS_ALTERNATIVE_CALIBRATED, "DIS_Alternative_Calibrated"),
	ALTERNATIVEDE(ProductReferenceTypeEnum.DIS_ALTERNATIVE_DE, "DIS_Alternative_DE"),
	ALTERNATIVESIMILAR(ProductReferenceTypeEnum.DIS_ALTERNATIVE_SIMILAR, "DIS_Alternative_Similar"),
	ALTERNATIVEBETTERVALUE(ProductReferenceTypeEnum.DIS_ALTERNATIVE_BETTERVALUE, "DIS_Alternative_BetterValue");

    private ProductReferenceTypeEnum productReferenceType;
    private String stiboCode;

    PimProductReferenceType(final ProductReferenceTypeEnum productReferenceType, final String stiboCode) {
        this.productReferenceType = productReferenceType;
        this.stiboCode = stiboCode;
    }

    public static PimProductReferenceType getByStiboCode(final String stiboCode) {
        for (final PimProductReferenceType value : values()) {
            if (value.getStiboCode().equals(stiboCode)) {
                return value;
            }
        }
        return null;
    }

    // BEGIN GENERATED CODE

    public ProductReferenceTypeEnum getProductReferenceType() {
        return productReferenceType;
    }

    public String getStiboCode() {
        return stiboCode;
    }

    // END GENERATED CODE
}
