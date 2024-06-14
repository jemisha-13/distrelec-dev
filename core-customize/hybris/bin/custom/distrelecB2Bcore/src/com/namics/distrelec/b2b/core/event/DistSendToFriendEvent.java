/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import java.util.List;

import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * Event object for send to friend functionality.
 */
public class DistSendToFriendEvent extends AbstractCommerceUserEvent<BaseSiteModel> {

    /**
     * Send to friend event types
     */

    private String yourName;
    private String yourEmail;
    private String receiverName;
    private String receiverEmail;
    private String message;

    // Additional attributes for different send to friend events
    private String url;
    private ProductModel product;
    private EmailAttachmentModel attachment;
    private List<ProductModel> comparisonList;
    private CartModel cart;
    private LanguageModel language;

    /**
     * Default constructor
     */
    public DistSendToFriendEvent() {
        super();
    }

    /**
     * Parameterized Constructor
     */
    public DistSendToFriendEvent(final String yourName, final String yourEmail, final String receiverName, final String receiverEmail, final String message) {
        super();
        this.yourName = yourName;
        this.yourEmail = yourEmail;
        this.receiverName = receiverName;
        this.receiverEmail = receiverEmail;
        this.message = message;
    }

    public String getYourName() {
        return yourName;
    }

    public void setYourName(final String yourName) {
        this.yourName = yourName;
    }

    public String getYourEmail() {
        return yourEmail;
    }

    public void setYourEmail(final String yourEmail) {
        this.yourEmail = yourEmail;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(final String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(final String receiverEmail) {
        this.receiverEmail = receiverEmail;
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

    public ProductModel getProduct() {
        return product;
    }

    public void setProduct(final ProductModel product) {
        this.product = product;
    }

    public List<ProductModel> getComparisonList() {
        return comparisonList;
    }

    public void setComparisonList(final List<ProductModel> comparisonList) {
        this.comparisonList = comparisonList;
    }

    public CartModel getCart() {
        return cart;
    }

    public void setCart(final CartModel cart) {
        this.cart = cart;
    }

    public EmailAttachmentModel getAttachment() {
        return attachment;
    }

    public void setAttachment(final EmailAttachmentModel attachment) {
        this.attachment = attachment;
    }

    public LanguageModel getLanguage() {
        return language;
    }

    public void setLanguage(LanguageModel language) {
        this.language = language;
    }

}
