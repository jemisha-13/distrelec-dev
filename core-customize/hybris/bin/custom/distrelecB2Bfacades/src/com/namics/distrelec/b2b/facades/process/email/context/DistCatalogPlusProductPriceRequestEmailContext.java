/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.process.email.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.process.DistCatalogPlusPriceRequestProcessModel;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.ConfigurablePopulator;
import de.hybris.platform.converters.ModifiableConfigurablePopulator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;

/**
 * {@code DistCatalogPlusProductPriceRequestEmailContext}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class DistCatalogPlusProductPriceRequestEmailContext extends CustomerEmailContext {

    private Long quantity;

    private String comment;

    private String emailSubject;

    private List<ProductData> products;

    private Converter<ProductModel, ProductData> b2bProductConverter;

    private ConfigurablePopulator<ProductModel, ProductData, ProductOption> productConfiguredPopulator;

    @Autowired
    private I18NService i18NService;

    @Autowired
    private MessageSource messageSource;

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);
        if (businessProcessModel instanceof DistCatalogPlusPriceRequestProcessModel) {
            final DistCatalogPlusPriceRequestProcessModel distCatalogPlusPriceRequestProcess = (DistCatalogPlusPriceRequestProcessModel) businessProcessModel;
            if (distCatalogPlusPriceRequestProcess.getCustomer() instanceof B2BCustomerModel) {
                final B2BCustomerModel b2bCustomer = (B2BCustomerModel) getCustomer(distCatalogPlusPriceRequestProcess);
                put(FROM_EMAIL, b2bCustomer.getEmail());
                put(FROM_DISPLAY_NAME, b2bCustomer.getName());
                put(EMAIL, getCatalogPlusEmail());
                put(DISPLAY_NAME, get(EMAIL));

                setQuantity(distCatalogPlusPriceRequestProcess.getQuantity());
                setComment(distCatalogPlusPriceRequestProcess.getComment());
                final List<ProductOption> productOptions = Arrays.asList(ProductOption.DIST_MANUFACTURER);

                final StringBuilder builder = new StringBuilder();
                boolean first = true;

                if (distCatalogPlusPriceRequestProcess.getProducts() != null && !distCatalogPlusPriceRequestProcess.getProducts().isEmpty()) {
                    for (final ProductModel product : distCatalogPlusPriceRequestProcess.getProducts()) {
                        final ProductData b2bProductData = getB2bProductConverter().convert(product);
                        getProductConfiguredPopulator().populate(product, b2bProductData, productOptions);
                        getProducts().add(b2bProductData);
                        if (!first) {
                            builder.append(", ");
                        } else {
                            first = false;
                        }

                        builder.append(product.getCatPlusSupplierAID());
                    }
                }

                setEmailSubject(getMessageSource().getMessage("requestCatalogPlusProductPrice.emailSubject", new Object[] { builder.toString() },
                                                              "Distrelec Catalog Plus Price Request", getI18NService().getCurrentLocale()));
            }
        }
    }

    private String getCatalogPlusEmail() {
        return getEmail(DistConstants.PropKey.Email.REQUEST_CATALOG_PLUS_PRODUCT_PRICE_EMAIL_PREFIX,
                        DistConstants.PropKey.Email.REQUEST_CATALOG_PLUS_PRODUCT_PRICE_EMAIL_DEFAULT);
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

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(final String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public List<ProductData> getProducts() {
        if (this.products == null) {
            this.products = new ArrayList<>();
        }
        return products;
    }

    public void setProducts(final List<ProductData> products) {
        this.products = products;
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

    public void setProductConfiguredPopulator(final ModifiableConfigurablePopulator<ProductModel, ProductData, ProductOption> productConfiguredPopulator) {
        this.productConfiguredPopulator = productConfiguredPopulator;
    }

    public I18NService getI18NService() {
        return i18NService;
    }

    public void setI18NService(final I18NService i18nService) {
        i18NService = i18nService;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
