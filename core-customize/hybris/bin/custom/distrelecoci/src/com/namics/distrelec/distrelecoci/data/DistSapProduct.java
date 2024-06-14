/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.distrelecoci.data;

/**
 * DistSapProduct
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 3.0
 * 
 */
public abstract interface DistSapProduct {
    String getItemDescription();

    String getItemMatNr();

    double getItemQuantity();

    String getItemUnit();

    double getItemPrice();

    String getItemCurrency();

    int getItemPriceUnit();

    int getItemLeadTime();

    String getItemLongtext();

    String getItemVendor();

    String getItemManufactCode();

    String getItemVendorMat();

    String getItemManufactMat();

    String getItemMatGroup();

    boolean isItemService();

    String getItemContract();

    String getItemContractItem();

    String getItemExtQuoteId();

    String getItemExtQuoteItem();

    String getItemExtProductId();

    String getItemAttachment();

    String getItemAttachmentTitle();

    char getItemAttachmentPurpose();

    String getItemExtCategoryId();

    String getItemExtCategory();

    String getItemSLDSysName();

    String[] getCustomParameterNames();

    String getCustomParameterValue(String paramString);
}
