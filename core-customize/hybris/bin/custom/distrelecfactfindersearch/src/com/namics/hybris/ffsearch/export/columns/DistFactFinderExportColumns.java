/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.export.columns;

import static com.namics.hybris.ffsearch.util.DistFactFinderUtils.getCategoryCodeFFAttribute;

/**
 * FactFinder export columns with their names. Centralizes referencing the export coulmns.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public enum DistFactFinderExportColumns {

    // @formatter:off
    PRODUCT_NUMBER("ProductNumber"), // 1
    EAN("EAN"), // 2
    PRODUCT_NUMBER_ELFA("ProductNumberElfa"), // 3
    PRODUCT_NUMBER_MOVEX("ProductNumberMovex"), // 4
    PRODUCT_NUMBER_NAVISION("ProductNumberNavision"), // 5
    TITLE("Title"), // 6
    DESCRIPTION("Description"), // 7
    PRODUCT_URL("ProductURL"), // 8
    IMAGE_URL("ImageURL"), // 9
    ADDITIONAL_IMAGE_URLS("AdditionalImageURLs"), // 10
    MANUFACTURER("Manufacturer"), // 11
    MANUFACTURER_URL("ManufacturerUrl"), // 12
    MANUFACTURER_IMAGE_URL("ManufacturerImageUrl"), // 13
    PRICE("Price"), // 14
    WEB_USE("WebUse"), // 15
    CATEGORY1("Category1"), // 16
    CATEGORY2("Category2"), // 17
    CATEGORY3("Category3"), // 18
    CATEGORY4("Category4"), // 19
    CATEGORY5("Category5"), // 20
    CATEGORYPATH("CategoryPath"), // 23
    CATEGORY_EXTENSIONS("CategoryExtensions"), // 24
    TYPENAME("TypeName"), // 25
    TYPE_NAME_NORMALIZED("TypeNameNormalized"), // 26
    INSTOCK("InStock"), // 27
    ITEMS_MIN("ItemsMin"), // 28
    ITEMS_STEP("ItemsStep"), // 29
    SALESUNIT("SalesUnit"), // 30
    BESTSELLER("Bestseller"), // 31
    PROMOTIONLABELS("PromotionLabels"), // 32
    REPLACEMENT("Replacement"), // 33
    BUYABLE("Buyable"), // 34
    TITLE_SHORT("TitleShort"), // 36
    SYNONYM_FULL_MATCH("SynonymFullMatch"), // 37
    PRODUCT_LINE_NAME("ProductLineName"), // 39
    ACCESSORY("Accessory"), // 41
    PRODUCT_FAMILY_NAME("ProductFamilyName"), // 43
    SINGLE_MIN_PRICE("singleMinPrice"), // 44
    PRODUCT_FAMILY_CODE("productFamilyCode"), // 45
    PRODUCT_HIERARCHY("ProductHierarchy"), // 46
    TOTALINSTOCK("totalInStock"), // 47
    AVAILABLEINPICKUP("availableInPickup"), // 48
    PICKUPSTOCK("pickupStock"), // 49
    CATEGORY1_CODE(getCategoryCodeFFAttribute(1)), // 50
    CATEGORY2_CODE(getCategoryCodeFFAttribute(2)), // 51
    CATEGORY3_CODE(getCategoryCodeFFAttribute(3)), // 52
    CATEGORY4_CODE(getCategoryCodeFFAttribute(4)), // 53
    CATEGORY5_CODE(getCategoryCodeFFAttribute(5)), // 54
    CATEGORY_CODE_PATH("categoryCodePath"), // 57
    SINGLE_UNIT_PRICE("singleUnitPrice"), // 58
    ENERGY_EFFICIENCY("energyEfficiency"), // 59
    PIM_WEB_USE_D("pimWebUseD"), //61
    ALTERNATIVES_MPN("alternativesMPN"), //62
    NORMALIZED_ALTERNATIVES_MPN("normalizedAlternativesMPN"), // 63
    CURATED_PRODUCT_SELECTION("curatedProductSelection"), // 64
    SAP_PLANT_PROFILES("sapPlantProfiles"), // 65
    SAP_MPN("sapMPN"), // 66
    ALTERNATE_ALIAS_MPN("alternateAliasMPN"), //67
    NORMALIZED_ALTERNATE_ALIAS_MPN("normalizedAlternateAliasMPN"), //68
    ITEM_CATEGORY_GROUP("itemCategoryGroup"),
    NET_MARGIN_RANK("netMarginRank"),
    STANDARD_PRICE("StandardPrice"),
    PRODUCT_STATUS("productStatus"),
    SALES_STATUS("salesStatus"),
    IMAGE_WEBP_URL("ImageWebpUrl"),

    PRODUCT_NUMBER_SAP("Id", false),
    TECHNICAL_ATTRIBUTES("TechnicalAttributes", false), // This is just a virtual field and is not exported by the hybris export. It is just configured on FF side and contains the same content as the webUse field
    ORIG_POSITION("__ORIG_POSITION__"),
    CAMPAIGN("__FFCampaign__"),

    ENERGY_EFFICIENCY_LABEL_IMAGE_URL("energyEfficiencyLabelImageUrl");


    // @formatter:on
    
    private String value;
    private boolean exported;

    private DistFactFinderExportColumns(final String value, final boolean exported) {
        this.value = value;
        this.exported = exported;
    }

    private DistFactFinderExportColumns(final String value) {
        this(value, true);
    }


    public String getValue() {
        return value;
    }

    public boolean isExported() {
        return exported;
    }

}
