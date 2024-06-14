/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

/**
 * Enum with all PIM import element handlers. Used by the element handler factory to create an instance of the prototype bean reference by
 * the enum's id.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public enum PimImportElementHandlerTypeEnum {

    ROOT_ELEMENT_HANDLER("rootElementHandler"), // Avoid line break
    UNIT_ELEMENT_HANDLER("unitElementHandler"), // Avoid line break
    ATTRIBUTE_ELEMENT_HANDLER("attributeElementHandler"), // Avoid line break
    DEFAULT_CATEGORY_ELEMENT_HANDLER("defaultCategoryElementHandler"), // Avoid line break
    ATTRIBUTE_LINK_ELEMENT_HANDLER("attributeLinkElementHandler"), // Avoid line break
    PRODUCT_MANUFACTURER_ELEMENT_HANDLER("productManufacturerElementHandler"), // Avoid line break
    PRODUCT_LINE_ELEMENT_HANDLER("productLineElementHandler"), // Avoid line break
    PRODUCT_FAMILY_ELEMENT_HANDLER("productFamilyElementHandler"), // Avoid line break
    PRODUCT_IMAGE_360_ELEMENT_HANDLER("productImage360Handler"), // Avoid line break
	PRODUCT_ELEMENT_HANDLER("productElementHandler"), // Avoid line break
    CLASSIFICATION_REFERENCE_ELEMENT_HANDLER("classificationReferenceElementHandler"), // Avoid line break
    AUDIO_ASSET_ELEMENT_HANDLER("audioAssetElementHandler"),
    IMAGE_ASSET_ELEMENT_HANDLER("imageAssetElementHandler"), // Avoid line break
    DATA_SHEET_ASSET_ELEMENT_HANDLER("dataSheetAssetElementHandler"), // Avoid line break
    VIDEO_ASSET_ELEMENT_HANDLER("videoAssetElementHandler"),
    PRODUCT_FAMILY_IMAGE_ASSET_HANDLER("productFamilyImageAssetHandler"),
    PRODUCT_FAMILY_VIDEO_ASSET_HANDLER("productFamilyVideoAssetHandler");

    private String id;

    private PimImportElementHandlerTypeEnum(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
