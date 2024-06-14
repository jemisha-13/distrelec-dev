/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters;

import com.distrelec.webservice.if18.v1.QuotationsResponse;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.facades.product.data.quote.QuotationRequestData;
import com.namics.distrelec.b2b.facades.product.data.quote.QuoteRequestItem;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * {@code QuotationRequestConverter}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.5
 */
public class QuotationRequestConverter extends AbstractPopulatingConverter<QuotationsResponse, QuotationRequestData> {

    private ProductService productService;
    private Converter<ProductModel, ProductData> productConverter;
    private DistrelecCodelistService distrelecCodelistService;

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.commerceservices.converter.impl.AbstractConverter#createTarget()
     */
    @Override
    protected QuotationRequestData createTarget() {
        return new QuotationRequestData();
    }

    @Override
    // TO-DO Need to fix all commented calls
    public void populate(final QuotationsResponse source, final QuotationRequestData target) {
        target.setQuotationId(source.getQuotationId());

        target.setPOnumber(source.getPOnumber());

        // Setting the quotation status
        target.setQuotationStatus(source.getQuotationStatus());
        target.setQuotationStatusText(getDistrelecCodelistService().getDistQuotationStatus(source.getQuotationStatus()).getName());

        if (source.getQuotationRequestDate() != null && !BigInteger.ZERO.equals(source.getQuotationRequestDate())) {
            target.setQuotationRequestDate(SoapConversionHelper.convertDate(source.getQuotationRequestDate()));
        }
        if (source.getQuotationExpiryDate() != null && !BigInteger.ZERO.equals(source.getQuotationExpiryDate())) {
            target.setQuotationExpiryDate(SoapConversionHelper.convertDate(source.getQuotationExpiryDate()));
        }
        target.setQuotationTotal(source.getTotal());
        target.setCustomerName(source.getCustomerName());
        // Set Currency
        target.setCurrencyCode(source.getCurrencyCode().toString());
        // Setting the product data
        addQuotationItem(source, target);
    }

    /**
     * Add a quotation item
     *
     * @param source
     * @param target
     */
    @Deprecated
    private void addQuotationItem(final QuotationsResponse source, final QuotationRequestData target) {
        if (target.getItems() == null) {
            target.setItems(new ArrayList<QuoteRequestItem>());
        }
        final QuoteRequestItem item = new QuoteRequestItem();

        item.setPrice(source.getTotal());

        if (source.getCurrencyCode() != null) {
            item.setCurrencyIso(source.getCurrencyCode().value());
        }

        target.getItems().add(item);
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    public Converter<ProductModel, ProductData> getProductConverter() {
        return productConverter;
    }

    public void setProductConverter(final Converter<ProductModel, ProductData> productConverter) {
        this.productConverter = productConverter;
    }

    public DistrelecCodelistService getDistrelecCodelistService() {
        return distrelecCodelistService;
    }

    public void setDistrelecCodelistService(final DistrelecCodelistService distrelecCodelistService) {
        this.distrelecCodelistService = distrelecCodelistService;
    }

}
