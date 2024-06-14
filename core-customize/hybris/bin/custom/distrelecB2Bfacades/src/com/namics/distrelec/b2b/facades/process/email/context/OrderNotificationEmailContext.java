/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.namics.distrelec.b2b.facades.process.email.context;

import java.math.BigDecimal;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.facades.product.DistPriceDataFactory;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Context (velocity) for email order notification.
 */
public class OrderNotificationEmailContext extends CustomerEmailContext {

    private static final String ORDER_URL = "/my-account/order-history/order-details/";

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DistPriceDataFactory priceDataFactory;

    @Autowired
    @Qualifier("b2bOrderConverter")
    private Converter<OrderModel, OrderData> orderConverter;

    private OrderModel orderModel;

    private OrderData orderData;

    private String host;

    private String deliveryMode;

    private String paymentMode;

    private StringBuilder orderUrl;

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);
        if (businessProcessModel instanceof OrderProcessModel) {
            orderModel = ((OrderProcessModel) businessProcessModel).getOrder();
            orderData = getOrderConverter().convert(orderModel);
        }
        setHost(configurationService.getConfiguration().getString("website." + getBaseSite().getUid() + ".http"));

        final LanguageModel emailLanguage = getEmailLanguage(businessProcessModel);
        if (emailLanguage != null) {
            final Locale locale = new Locale(emailLanguage.getIsocode());
            deliveryMode = orderModel.getDeliveryMode() != null ? orderModel.getDeliveryMode().getName(locale) : null;
            paymentMode = orderModel.getPaymentMode() != null ? orderModel.getPaymentMode().getName(locale) : null;
        } else {
            deliveryMode = orderModel.getDeliveryMode() != null ? orderModel.getDeliveryMode().getName() : null;
            paymentMode = orderModel.getPaymentMode() != null ? orderModel.getPaymentMode().getName() : null;
        }
        orderUrl = new StringBuilder(getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(), false, ORDER_URL)).append(orderModel.getCode());

        final UserModel user = orderModel.getUser();
        if (getUserService().isMemberOfGroup(user, getUserService().getUserGroupForUID(DistConstants.User.B2BEESHOPGROUP_UID))) {
            final AddressModel billingAddress = orderModel.getPaymentAddress();
            if (billingAddress != null && StringUtils.isNotBlank(billingAddress.getEmail())) {
                put(EMAIL, billingAddress.getEmail());
            }
        }

    }

    public OrderData getOrder() {
        return orderData;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getFormattedPrice(final BigDecimal value) {
        if (null == value) {
            return "0.00";
        }
        return priceDataFactory.create(value).getFormattedValue();
    }

    public String getFormattedPrice(final BigDecimal value, final int maxFractionDigits) {
        if (null == value) {
            return "0.00";
        }
        return priceDataFactory.create(value, maxFractionDigits).getFormattedValue();
    }

    public String getPaymentAddress() {
        final StringBuffer buffer = new StringBuffer(200);
        final UserModel user = orderModel.getUser();
        if (user instanceof B2BCustomerModel) {
            final B2BCustomerModel customer = (B2BCustomerModel) user;
            final AddressData address = getOrder().getBillingAddress();
            if (customer.getCustomerType().equals(CustomerType.B2C) || customer.getCustomerType().equals(CustomerType.GUEST)
                    || customer.getCustomerType().equals(CustomerType.B2E)) {
                buffer.append(StringEscapeUtils.escapeHtml(address.getFirstName()));
                buffer.append(' ');
                buffer.append(StringEscapeUtils.escapeHtml(address.getLastName()));
                buffer.append("<br/>");

                if (StringUtils.isNotBlank(address.getAdditionalAddress())) {
                    buffer.append(StringEscapeUtils.escapeHtml(address.getAdditionalAddress()));
                    buffer.append("<br/>");
                }

                buffer.append(StringEscapeUtils.escapeHtml(address.getLine1()));
                buffer.append(' ');
                buffer.append(StringEscapeUtils.escapeHtml(address.getLine2()));
                buffer.append("<br/>");

                if (StringUtils.isNotBlank(address.getPobox())) {
                    buffer.append(StringEscapeUtils.escapeHtml(address.getPobox()));
                    buffer.append("<br/>");
                }

                buffer.append(address.getPostalCode());
                buffer.append(' ');
                buffer.append(StringEscapeUtils.escapeHtml(address.getTown()));
                buffer.append("<br/>");

                buffer.append(StringEscapeUtils.escapeHtml(address.getCountry().getName()));
                buffer.append("<br/>");
            } else {
                buffer.append(StringEscapeUtils.escapeHtml(address.getCompanyName()));
                buffer.append("<br/>");

                if (StringUtils.isNotBlank(address.getCompanyName2())) {
                    buffer.append(StringEscapeUtils.escapeHtml(address.getCompanyName2()));
                    buffer.append("<br/>");
                }

                if (StringUtils.isNotBlank(address.getCompanyName3())) {
                    buffer.append(StringEscapeUtils.escapeHtml(address.getCompanyName3()));
                    buffer.append("<br/>");
                }

                buffer.append(StringEscapeUtils.escapeHtml(address.getLine1()));
                buffer.append(' ');
                buffer.append(StringEscapeUtils.escapeHtml(address.getLine2()));
                buffer.append("<br/>");

                if (StringUtils.isNotBlank(address.getPobox())) {
                    buffer.append(StringEscapeUtils.escapeHtml(address.getPobox()));
                    buffer.append("<br/>");
                }

                buffer.append(address.getPostalCode());
                buffer.append(' ');
                buffer.append(StringEscapeUtils.escapeHtml(address.getTown()));
                buffer.append("<br/>");

                buffer.append(StringEscapeUtils.escapeHtml(address.getCountry().getName()));
                buffer.append("<br/>");
            }
        } else {
            final AddressData address = getOrder().getBillingAddress();
            buffer.append(StringEscapeUtils.escapeHtml(address.getFirstName()));
            buffer.append(' ');
            buffer.append(StringEscapeUtils.escapeHtml(address.getLastName()));
            buffer.append("<br/>");

            buffer.append(StringEscapeUtils.escapeHtml(address.getLine1()));
            buffer.append(' ');
            buffer.append(StringEscapeUtils.escapeHtml(address.getLine2()));
            buffer.append("<br/>");

            buffer.append(address.getPostalCode());
            buffer.append(' ');
            buffer.append(StringEscapeUtils.escapeHtml(address.getTown()));
            buffer.append("<br/>");

            buffer.append(StringEscapeUtils.escapeHtml(address.getCountry().getName()));
            buffer.append("<br/>");
        }

        return buffer.toString();
    }

    public String getDeliveryAddress() {
        final StringBuffer buffer = new StringBuffer(200);
        final UserModel user = orderModel.getUser();
        if (orderModel.getDeliveryAddress() != null) {
            if (user instanceof B2BCustomerModel) {
                final B2BCustomerModel customer = (B2BCustomerModel) user;
                final AddressData address = getOrder().getDeliveryAddress();
                if (customer.getCustomerType().equals(CustomerType.B2C) || customer.getCustomerType().equals(CustomerType.GUEST)
                        || customer.getCustomerType().equals(CustomerType.B2E)) {
                    buffer.append(StringEscapeUtils.escapeHtml(address.getFirstName()));
                    buffer.append(' ');
                    buffer.append(StringEscapeUtils.escapeHtml(address.getLastName()));
                    buffer.append("<br/>");

                    if (StringUtils.isNotBlank(address.getAdditionalAddress())) {
                        buffer.append(StringEscapeUtils.escapeHtml(address.getAdditionalAddress()));
                        buffer.append("<br/>");
                    }

                    buffer.append(StringEscapeUtils.escapeHtml(address.getLine1()));
                    buffer.append(' ');
                    buffer.append(StringEscapeUtils.escapeHtml(address.getLine2()));
                    buffer.append("<br/>");

                    if (StringUtils.isNotBlank(address.getPobox())) {
                        buffer.append(StringEscapeUtils.escapeHtml(address.getPobox()));
                        buffer.append("<br/>");
                    }

                    buffer.append(address.getPostalCode());
                    buffer.append(' ');
                    buffer.append(StringEscapeUtils.escapeHtml(address.getTown()));
                    buffer.append("<br/>");

                    buffer.append(StringEscapeUtils.escapeHtml(address.getCountry().getName()));
                    buffer.append("<br/>");
                } else {
                    buffer.append(StringEscapeUtils.escapeHtml(address.getCompanyName()));
                    buffer.append("<br/>");

                    if (StringUtils.isNotBlank(address.getCompanyName2())) {
                        buffer.append(StringEscapeUtils.escapeHtml(address.getCompanyName2()));
                        buffer.append("<br/>");
                    }

                    if (StringUtils.isNotBlank(address.getCompanyName3())) {
                        buffer.append(StringEscapeUtils.escapeHtml(address.getCompanyName3()));
                        buffer.append("<br/>");
                    }

                    buffer.append(StringEscapeUtils.escapeHtml(address.getFirstName()));
                    buffer.append(' ');
                    buffer.append(StringEscapeUtils.escapeHtml(address.getLastName()));
                    buffer.append("<br/>");

                    buffer.append(StringEscapeUtils.escapeHtml(address.getLine1()));
                    buffer.append(' ');
                    buffer.append(StringEscapeUtils.escapeHtml(address.getLine2()));
                    buffer.append("<br/>");

                    if (StringUtils.isNotBlank(address.getPobox())) {
                        buffer.append(StringEscapeUtils.escapeHtml(address.getPobox()));
                        buffer.append("<br/>");
                    }

                    buffer.append(address.getPostalCode());
                    buffer.append(' ');
                    buffer.append(StringEscapeUtils.escapeHtml(address.getTown()));
                    buffer.append("<br/>");

                    buffer.append(StringEscapeUtils.escapeHtml(address.getCountry().getName()));
                    buffer.append("<br/>");
                }
            } else {
                final AddressData address = getOrder().getDeliveryAddress();
                buffer.append(StringEscapeUtils.escapeHtml(address.getFirstName()));
                buffer.append(' ');
                buffer.append(StringEscapeUtils.escapeHtml(address.getLastName()));
                buffer.append("<br/>");

                buffer.append(StringEscapeUtils.escapeHtml(address.getLine1()));
                buffer.append(' ');
                buffer.append(StringEscapeUtils.escapeHtml(address.getLine2()));
                buffer.append("<br/>");

                buffer.append(address.getPostalCode());
                buffer.append(' ');
                buffer.append(StringEscapeUtils.escapeHtml(address.getTown()));
                buffer.append("<br/>");

                buffer.append(StringEscapeUtils.escapeHtml(address.getCountry().getName()));
                buffer.append("<br/>");
            }
        }

        return buffer.toString();
    }

    public String getCustomerEmailAddress() {
        final UserModel user = orderModel.getUser();
        if (user instanceof B2BCustomerModel) {
            final B2BCustomerModel customer = (B2BCustomerModel) user;
            if (!CustomerType.B2E.equals(customer.getCustomerType())) {
                return customer.getEmail();
            }
        }

        return orderModel.getPaymentAddress().getEmail();
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public String getOrderUrl() {
        return orderUrl.toString();
    }

    public Boolean getShowOrderLink() {
        final UserModel user = orderModel.getUser();
        if (user instanceof B2BCustomerModel) {
            if (CustomerType.GUEST.equals(((B2BCustomerModel) user).getCustomerType())
                    || CustomerType.B2E.equals(((B2BCustomerModel) user).getCustomerType())) {
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    protected Converter<OrderModel, OrderData> getOrderConverter() {
        return orderConverter;
    }

    public void setOrderConverter(final Converter<OrderModel, OrderData> orderConverter) {
        this.orderConverter = orderConverter;
    }

}
