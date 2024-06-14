/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.rma;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.rma.SapReturnRequestEntryModel;
import com.namics.distrelec.b2b.core.model.rma.SapReturnRequestModel;

import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.product.ProductService;

/**
 * {@code SapReturnRequestConverter}
 * <p>
 * Converter class for the {@code SapReturnRequestModel}
 * </p>
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class SapReturnRequestConverter extends AbstractPopulatingConverter<SapReturnRequestModel, ReturnRequestData> {

    @Autowired
    private ProductService productService;

    @Override
    protected ReturnRequestData createTarget() {
        return new ReturnRequestData();
    }

    @Override
    public void populate(final SapReturnRequestModel source, final ReturnRequestData target) {

        // Setting the return request data
        target.setRequestId(source.getCode());
        target.setRequestDate(source.getCreationtime());
        target.setEntries(createEntries(source.getSapReturnRequestEntries()));
        target.setOrderCode(source.getOrderNumber());
        target.setPurchaseDate(source.getPurchaseDate());
        // Set the customer data
        target.setCustomerId(source.getCustomerId());
        target.setEmail(source.getEmail());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setPhoneNumber(source.getPhoneNumber());
        target.setCompany(source.getCompany());
        target.setSalesOrgCode(source.getSalesOrgCode());
        super.populate(source, target);
    }

    /**
     * Convert the list of {@code SapReturnRequestEntryModel} to a list of {@code ReturnRequestEntryData}
     * 
     * @param sapReturnRequestEntries
     *            the list of {@code SapReturnRequestEntryModel} to be converted
     * @return a list of {@code ReturnRequestEntryData}
     */
    private List<ReturnRequestEntryData> createEntries(final Set<SapReturnRequestEntryModel> sapReturnRequestEntries) {
        final List<ReturnRequestEntryData> returnRequestEntryDatas = new ArrayList<ReturnRequestEntryData>();

        // Creating RMA request entries
        if (CollectionUtils.isNotEmpty(sapReturnRequestEntries)) {
            for (final SapReturnRequestEntryModel sapRmaEntry : sapReturnRequestEntries) {
                final ReturnRequestEntryData entryData = new ReturnRequestEntryData();

                try {
                    entryData.setProductName(getProductService().getProductForCode(sapRmaEntry.getArticleNumber()).getName());
                } catch (final Exception exp) {
                    //
                }

                entryData.setProductNumber(sapRmaEntry.getArticleNumber());
                entryData.setQuantity(Integer.parseInt(sapRmaEntry.getQuantity()));
                entryData.setNote(sapRmaEntry.getNote());
                entryData.setSerialNumbers(sapRmaEntry.getSerialNumbers());
                entryData.setPackaging(sapRmaEntry.getPacking());
                entryData.setReplacement(sapRmaEntry.getReplacement().booleanValue());
                // Create the reason
                entryData.setReason(sapRmaEntry.getReason().getCode());
                returnRequestEntryDatas.add(entryData);
            }
        }

        return returnRequestEntryDatas;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }
}
