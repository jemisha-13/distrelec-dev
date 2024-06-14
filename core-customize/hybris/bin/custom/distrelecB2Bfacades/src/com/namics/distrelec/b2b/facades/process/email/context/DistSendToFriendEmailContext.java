/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.process.email.context;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import de.hybris.platform.commercefacades.product.data.ProductData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.config.DistrelecSiteConfigService;
import com.namics.distrelec.b2b.core.model.process.DistSendToFriendProcessModel;
import com.namics.distrelec.b2b.core.service.process.email.context.AbstractDistEmailContext;
import com.namics.distrelec.b2b.facades.product.DistPriceDataFactory;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.product.data.ProductAvailabilityData;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Velocity context for a send to friend email.
 */
public class DistSendToFriendEmailContext extends AbstractDistEmailContext<DistSendToFriendProcessModel> {

    private static final String SENDER_MAIL = "senderMail";

    @Autowired
    @Qualifier("productModelUrlResolver")
    private UrlResolver<ProductModel> productModelUrlResolver;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DistrelecProductFacade productFacade;

    @Autowired
    private DistPriceDataFactory priceDataFactory;

    @Autowired
    private DistrelecSiteConfigService distrelecSiteConfigService;

    private String emailSubject;

    private String message;

    private String url;

    private ProductData product;

    private Converter<ProductModel, ProductData> productConverter;

    private Converter<CartModel, CartData> cartConverter;

    private List<ProductData> comparisonList;

    private CartData cart;

    private String host;

    private String importToolUrl;

    @Override
    public void init(final DistSendToFriendProcessModel distSendToFriendProcessModel, final EmailPageModel emailPageModel) {
        super.init(distSendToFriendProcessModel, emailPageModel);
        put(FROM_EMAIL, resolveFromEmail(distSendToFriendProcessModel));
        put(SENDER_MAIL, distSendToFriendProcessModel.getYourEmail());
        put(FROM_DISPLAY_NAME, distSendToFriendProcessModel.getYourName());
        put(DISPLAY_NAME, distSendToFriendProcessModel.getReceiverName());
        put(EMAIL, distSendToFriendProcessModel.getReceiverEmail());
        setMessage(distSendToFriendProcessModel.getMessage());
        setUrl(distSendToFriendProcessModel.getUrl());
        setHost(configurationService.getConfiguration().getString("website." + getBaseSite().getUid() + ".http"));
        setImportToolUrl(configurationService.getConfiguration().getString("website." + getBaseSite().getUid() + ".https") + "/import-tool");
        if (distSendToFriendProcessModel.getProduct() != null) {
            setProduct(productConverter.convert(distSendToFriendProcessModel.getProduct()));
        }
        final List<ProductModel> comparisonList = distSendToFriendProcessModel.getComparisonList();
        if (isNotEmpty(comparisonList)) {
            setComparisonList(Converters.convertAll(comparisonList, productConverter));
        }
        if (distSendToFriendProcessModel.getCart() != null) {
            setCart(cartConverter.convert(distSendToFriendProcessModel.getCart()));
        }
    }

    private String resolveFromEmail(DistSendToFriendProcessModel distSendToFriendProcessModel) {
        return distrelecSiteConfigService.getStringForBaseSite("distrelec.share.with.friend.email", "noreply@distrelec.com",
                                                               distSendToFriendProcessModel.getSite());
    }

    @Override
    protected BaseSiteModel getSite(final DistSendToFriendProcessModel distSendToFriendProcessModel) {
        return distSendToFriendProcessModel.getSite();
    }

    @Override
    protected CustomerModel getCustomer(final DistSendToFriendProcessModel businessProcessModel) {
        return null;
    }

    @Override
    protected LanguageModel getEmailLanguage(final DistSendToFriendProcessModel distSendToFriendProcessModel) {
        return distSendToFriendProcessModel.getLanguage() != null ? distSendToFriendProcessModel.getLanguage()
                                                                  : distSendToFriendProcessModel.getCustomer().getSessionLanguage();
    }

    public ProductAvailabilityData getAvailabilityDataForProduct(final String productCode) {
        final List<ProductAvailabilityData> productAvailabilityDataList = productFacade.getAvailability(Collections.singletonList(productCode));
        if (productAvailabilityDataList != null) {
            return productAvailabilityDataList.get(0);
        }
        return null;
    }

    public String getSenderMail() {
        return (String) get(SENDER_MAIL);
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(final String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public ProductData getProduct() {
        return product;
    }

    public void setProduct(final ProductData product) {
        this.product = product;
    }

    public Converter<ProductModel, ProductData> getProductConverter() {
        return productConverter;
    }

    public void setProductConverter(final Converter<ProductModel, ProductData> productConverter) {
        this.productConverter = productConverter;
    }

    public List<ProductData> getComparisonList() {
        return comparisonList;
    }

    public void setComparisonList(final List<ProductData> comparisonList) {
        this.comparisonList = comparisonList;
    }

    public CartData getCart() {
        return cart;
    }

    public void setCart(final CartData cart) {
        this.cart = cart;
    }

    public UrlResolver<ProductModel> getProductModelUrlResolver() {
        return productModelUrlResolver;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getImportToolUrl() {
        return importToolUrl;
    }

    public void setImportToolUrl(final String importToolUrl) {
        this.importToolUrl = importToolUrl;
    }

    public String getFormattedPrice(final BigDecimal value) {
        return priceDataFactory.create(value).getFormattedValue();
    }

    public Converter<CartModel, CartData> getCartConverter() {
        return cartConverter;
    }

    public void setCartConverter(final Converter<CartModel, CartData> cartConverter) {
        this.cartConverter = cartConverter;
    }

}
