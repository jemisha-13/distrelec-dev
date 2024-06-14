/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.process.email.context;

import java.util.Arrays;
import java.util.List;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.process.DistQuoteProductPriceProcessModel;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.ConfigurablePopulator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Velocity context for a quote product price email.
 */
public class DistQuoteProductPriceEmailContext extends CustomerEmailContext {

    private String company;

    private String firstName;

    private String lastName;

    private String customerEmail;

    private String phone;

    private Long quantity;

    private String comment;

    private ProductData product;

    private Converter<ProductModel, ProductData> b2bProductConverter;

    private ConfigurablePopulator<ProductModel, ProductData, ProductOption> productConfiguredPopulator;

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);
        if (businessProcessModel instanceof DistQuoteProductPriceProcessModel) {
            final DistQuoteProductPriceProcessModel distQuoteProductPriceProcess = (DistQuoteProductPriceProcessModel) businessProcessModel;
            if (distQuoteProductPriceProcess.getCustomer() instanceof B2BCustomerModel) {
                final B2BCustomerModel b2bCustomer = (B2BCustomerModel) getCustomer(distQuoteProductPriceProcess);
                put(FROM_EMAIL, getQuoteProductPriceFromEmail());
                put(FROM_DISPLAY_NAME, getQuoteProductPriceEmailDisplayName());
                put(EMAIL, getQuoteProductPriceEmail());
                put(DISPLAY_NAME, get(EMAIL));
                setCompany(distQuoteProductPriceProcess.getCompany());
                setFirstName(distQuoteProductPriceProcess.getFirstName());
                setLastName(distQuoteProductPriceProcess.getLastName());
                setCustomerEmail(distQuoteProductPriceProcess.getCustomerEmail());
                setPhone(distQuoteProductPriceProcess.getPhoneNumber());
                setQuantity(distQuoteProductPriceProcess.getQuantity());
                setComment(distQuoteProductPriceProcess.getComment());
                setQuantity(distQuoteProductPriceProcess.getQuantity());
                setComment(distQuoteProductPriceProcess.getComment());
                final List<ProductOption> productOptions = Arrays.asList(ProductOption.DIST_MANUFACTURER);
                final ProductData b2bProductData = getB2bProductConverter().convert(distQuoteProductPriceProcess.getProduct());
                getProductConfiguredPopulator().populate(distQuoteProductPriceProcess.getProduct(), b2bProductData, productOptions);
                setProduct(b2bProductData);
            }
        }
    }

    private String getQuoteProductPriceFromEmail() {
        return getConfigurationService().getConfiguration().getString(DistConstants.PropKey.Email.QUOTE_PRODUCT_PRICE_EMAIL_FROM_DEFAULT);
    }

    private String getQuoteProductPriceEmailDisplayName() {
        return getConfigurationService().getConfiguration().getString(DistConstants.PropKey.Email.QUOTE_PRODUCT_PRICE_EMAIL_DEFAULT_DISPLAYNAME);
    }

    private String getQuoteProductPriceEmail() {
        return getEmail(DistConstants.PropKey.Email.QUOTE_PRODUCT_PRICE_EMAIL_PREFIX, DistConstants.PropKey.Email.QUOTE_PRODUCT_PRICE_EMAIL_DEFAULT);
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(final Long quantity) {
        this.quantity = quantity;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public ProductData getProduct() {
        return product;
    }

    public void setProduct(final ProductData product) {
        this.product = product;
    }

    public Converter<ProductModel, ProductData> getB2bProductConverter() {
        return b2bProductConverter;
    }

    public void setB2bProductConverter(final Converter<ProductModel, ProductData> b2bProductConverter) {
        this.b2bProductConverter = b2bProductConverter;
    }

    public ConfigurablePopulator<ProductModel, ProductData, ProductOption> getProductConfiguredPopulator() {
        return productConfiguredPopulator;
    }

    public void setProductConfiguredPopulator(final ConfigurablePopulator<ProductModel, ProductData, ProductOption> productConfiguredPopulator) {
        this.productConfiguredPopulator = productConfiguredPopulator;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(final String company) {
        this.company = company;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(final String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

}
