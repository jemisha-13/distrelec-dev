/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.pdf;

import com.namics.distrelec.b2b.core.service.process.pdfgeneration.PDFContext;
import com.namics.distrelec.b2b.facades.product.DistPriceDataFactory;
import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.media.MediaService;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Order Confirmation PDF Context.
 *
 * @author lstuker, Namics AG
 * @since Distrelec 1.0
 *
 */
public class OrderConfirmationPDFContext extends PDFContext {

    private DistPriceDataFactory priceDataFactory;

    private MediaService mediaService;
    private ConfigurationService configurationService;
    private CommonI18NService commonI18NService;

    private OrderProcessModel orderProcessModel;
    private OrderModel orderModel;

    private final static String LOGO_MEDIA_CODE = "/images/theme/distrelec_logo.png";

    @Override
    public void init(final BusinessProcessModel businessProcessModel) {
        super.init(businessProcessModel);
        if (businessProcessModel instanceof OrderProcessModel) {
            orderProcessModel = (OrderProcessModel) businessProcessModel;
            setOrderModel(orderProcessModel.getOrder());
        }
        final B2BCustomerModel customerModel = (B2BCustomerModel) getOrderModel().getUser();
        final Locale locale = new Locale(getPDFLanguage(businessProcessModel).getIsocode());
        final SimpleDateFormat dateFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale);

        put("site", orderModel.getSite().getUid());
        put("customerModel", customerModel);
        put("orderModel", getOrderModel());
        put("country", getOrderModel().getPaymentAddress().getCountry().getIsocode());

        put("formattedDate", dateFormat.format(getOrderModel().getDate()));
        put("StringEscapeUtils", StringEscapeUtils.class);

        put("paymentAddress", createPaymentAddressXML());
        if (getOrderModel().getDeliveryAddress() != null) {
            put("deliveryAddress", createDeliveryAddressXML());
        }

        if (getOrderModel().getDeliveryMode() != null) {
            put("deliveryMode", getOrderModel().getDeliveryMode().getName(locale));
        }
        if (getOrderModel().getPaymentMode() != null) {
            put("paymentMode", getOrderModel().getPaymentMode().getName(locale));
        }

        // get logo image URL from media model
        final BaseSiteModel site = getProcessContextResolutionStrategy().getCmsSite(orderProcessModel);
        final MediaModel logoMediaModel = getMediaService().getMedia(((CMSSiteModel) site).getContentCatalogs().get(0).getActiveCatalogVersion(),
                LOGO_MEDIA_CODE);

        put("logoURL", getMediaFilePath(logoMediaModel));

    }

    @Override
    protected LanguageModel getPDFLanguage(final BusinessProcessModel businessProcessModel) {
        final String localeString = ((OrderProcessModel) businessProcessModel).getOrder().getLocale();
        if (localeString != null) {
            final String[] localeStringArray = localeString.split("_");
            return getCommonI18NService().getLanguage(localeStringArray[0]);
        } else {
            return ((OrderProcessModel) businessProcessModel).getOrder().getUser().getSessionLanguage();
        }
    }

    public String getFormattedPrice(final Double value) {
        return (null == value) ? "0.00" : getPriceDataFactory().create(new BigDecimal(value.doubleValue())).getFormattedValue();
    }

    public String createPaymentAddressXML() {
        final StringBuffer buffer = new StringBuffer(200);
        final UserModel user = getOrderModel().getUser();
        if (user instanceof B2BCustomerModel) {
            final B2BCustomerModel customer = (B2BCustomerModel) user;
            final AddressModel address = getOrderModel().getPaymentAddress();
            if (CustomerType.B2C.equals(customer.getCustomerType()) || CustomerType.GUEST.equals(customer.getCustomerType())
                    || CustomerType.B2E.equals(customer.getCustomerType())) {
                buffer.append("<NAME>");
                buffer.append(StringEscapeUtils.escapeXml(address.getFirstname()));
                buffer.append(' ');
                buffer.append(StringEscapeUtils.escapeXml(address.getLastname()));
                buffer.append("</NAME>");

                if (StringUtils.isNotBlank(address.getAdditionalAddressCompany())) {
                    buffer.append("<ADDITIONAL_ADDRESS>");
                    buffer.append(StringEscapeUtils.escapeXml(address.getAdditionalAddressCompany()));
                    buffer.append("</ADDITIONAL_ADDRESS>");
                }

                buffer.append("<STREET>");
                buffer.append(StringEscapeUtils.escapeXml(address.getLine1()));
                buffer.append(' ');
                buffer.append(StringEscapeUtils.escapeXml(address.getLine2()));
                buffer.append("</STREET>");

                if (StringUtils.isNotBlank(address.getPobox())) {
                    buffer.append("<PO_BOX>");
                    buffer.append(StringEscapeUtils.escapeXml(address.getPobox()));
                    buffer.append("</PO_BOX>");
                }

                buffer.append("<CITY>");
                buffer.append(address.getPostalcode());
                buffer.append(' ');
                buffer.append(StringEscapeUtils.escapeXml(address.getTown()));
                buffer.append("</CITY>");

                buffer.append("<COUNTRY>");
                buffer.append(StringEscapeUtils.escapeXml(address.getCountry().getName()));
                buffer.append("</COUNTRY>");

                buffer.append("<EMAIL>");
                if (CustomerType.B2E.equals(customer.getCustomerType())) {
                    buffer.append(address.getEmail());
                } else {
                    buffer.append(customer.getEmail());
                }
                buffer.append("</EMAIL>");
            } else {
                buffer.append("<COMPANY_NAME>");
                buffer.append(StringEscapeUtils.escapeXml(address.getCompany()));
                buffer.append("</COMPANY_NAME>");

                if (StringUtils.isNotBlank(address.getCompanyName2())) {
                    buffer.append("<COMPANY_NAME_2>");
                    buffer.append(StringEscapeUtils.escapeXml(address.getCompanyName2()));
                    buffer.append("</COMPANY_NAME_2>");
                }

                if (StringUtils.isNotBlank(address.getCompanyName3())) {
                    buffer.append("<ADDITIONAL_ADDRESS>");
                    buffer.append(StringEscapeUtils.escapeXml(address.getCompanyName3()));
                    buffer.append("</ADDITIONAL_ADDRESS>");
                }

                buffer.append("<STREET>");
                buffer.append(StringEscapeUtils.escapeXml(address.getLine1()));
                buffer.append(' ');
                buffer.append(StringEscapeUtils.escapeXml(address.getLine2()));
                buffer.append("</STREET>");

                if (StringUtils.isNotBlank(address.getPobox())) {
                    buffer.append("<PO_BOX>");
                    buffer.append(StringEscapeUtils.escapeXml(address.getPobox()));
                    buffer.append("</PO_BOX>");
                }

                buffer.append("<CITY>");
                buffer.append(address.getPostalcode());
                buffer.append(' ');
                buffer.append(StringEscapeUtils.escapeXml(address.getTown()));
                buffer.append("</CITY>");

                buffer.append("<COUNTRY>");
                buffer.append(StringEscapeUtils.escapeXml(address.getCountry().getName()));
                buffer.append("</COUNTRY>");

                buffer.append("<EMAIL>");
                buffer.append(customer.getEmail());
                buffer.append("</EMAIL>");
            }
        } else {
            final AddressModel address = getOrderModel().getPaymentAddress();
            buffer.append("<NAME>");
            buffer.append(StringEscapeUtils.escapeXml(address.getFirstname()));
            buffer.append(' ');
            buffer.append(StringEscapeUtils.escapeXml(address.getLastname()));
            buffer.append("</NAME>");

            buffer.append("<STREET>");
            buffer.append(StringEscapeUtils.escapeXml(address.getLine1()));
            buffer.append(' ');
            buffer.append(StringEscapeUtils.escapeXml(address.getLine2()));
            buffer.append("</STREET>");

            if (StringUtils.isNotBlank(address.getPobox())) {
                buffer.append("<PO_BOX>");
                buffer.append(StringEscapeUtils.escapeXml(address.getPobox()));
                buffer.append("</PO_BOX>");
            }

            buffer.append("<CITY>");
            buffer.append(address.getPostalcode());
            buffer.append(' ');
            buffer.append(StringEscapeUtils.escapeXml(address.getTown()));
            buffer.append("</CITY>");

            buffer.append("<COUNTRY>");
            buffer.append(StringEscapeUtils.escapeXml(address.getCountry().getName()));
            buffer.append("</COUNTRY>");

            buffer.append("<EMAIL>");
            buffer.append(address.getEmail());
            buffer.append("</EMAIL>");
        }

        return buffer.toString();
    }

    public String createDeliveryAddressXML() {
        final StringBuffer buffer = new StringBuffer(200);
        final UserModel user = orderModel.getUser();
        if (user instanceof B2BCustomerModel) {
            final B2BCustomerModel customer = (B2BCustomerModel) user;
            final AddressModel address = getOrderModel().getDeliveryAddress();
            if (CustomerType.B2C.equals(customer.getCustomerType()) || CustomerType.GUEST.equals(customer.getCustomerType())
                    || CustomerType.B2E.equals(customer.getCustomerType())) {
                buffer.append("<NAME>");
                buffer.append(StringEscapeUtils.escapeXml(address.getFirstname()));
                buffer.append(' ');
                buffer.append(StringEscapeUtils.escapeXml(address.getLastname()));
                buffer.append("</NAME>");

                if (StringUtils.isNotBlank(address.getAdditionalAddressCompany())) {
                    buffer.append("<ADDITIONAL_ADDRESS>");
                    buffer.append(StringEscapeUtils.escapeXml(address.getAdditionalAddressCompany()));
                    buffer.append("</ADDITIONAL_ADDRESS>");
                }

                buffer.append("<STREET>");
                buffer.append(StringEscapeUtils.escapeXml(address.getLine1()));
                buffer.append(' ');
                buffer.append(StringEscapeUtils.escapeXml(address.getLine2()));
                buffer.append("</STREET>");

                if (StringUtils.isNotBlank(address.getPobox())) {
                    buffer.append("<PO_BOX>");
                    buffer.append(StringEscapeUtils.escapeXml(address.getPobox()));
                    buffer.append("</PO_BOX>");
                }

                buffer.append("<CITY>");
                buffer.append(address.getPostalcode());
                buffer.append(' ');
                buffer.append(StringEscapeUtils.escapeXml(address.getTown()));
                buffer.append("</CITY>");

                buffer.append("<COUNTRY>");
                buffer.append(StringEscapeUtils.escapeXml(address.getCountry().getName()));
                buffer.append("</COUNTRY>");

                buffer.append("<EMAIL>");
                if (CustomerType.B2E.equals(customer.getCustomerType())) {
                    buffer.append(address.getEmail());
                } else {
                    buffer.append(customer.getEmail());
                }
                buffer.append("</EMAIL>");
            } else {
                buffer.append("<COMPANY_NAME>");
                buffer.append(StringEscapeUtils.escapeXml(address.getCompany()));
                buffer.append("</COMPANY_NAME>");

                if (StringUtils.isNotBlank(address.getCompanyName2())) {
                    buffer.append("<COMPANY_NAME_2>");
                    buffer.append(StringEscapeUtils.escapeXml(address.getCompanyName2()));
                    buffer.append("</COMPANY_NAME_2>");
                }

                if (StringUtils.isNotBlank(address.getCompanyName3())) {
                    buffer.append("<ADDITIONAL_ADDRESS>");
                    buffer.append(StringEscapeUtils.escapeXml(address.getCompanyName3()));
                    buffer.append("</ADDITIONAL_ADDRESS>");
                }

                buffer.append("<NAME>");
                buffer.append(StringEscapeUtils.escapeXml(address.getFirstname()));
                buffer.append(' ');
                buffer.append(StringEscapeUtils.escapeXml(address.getLastname()));
                buffer.append("</NAME>");

                buffer.append("<STREET>");
                buffer.append(StringEscapeUtils.escapeXml(address.getLine1()));
                buffer.append(' ');
                buffer.append(StringEscapeUtils.escapeXml(address.getLine2()));
                buffer.append("</STREET>");

                if (StringUtils.isNotBlank(address.getPobox())) {
                    buffer.append("<PO_BOX>");
                    buffer.append(StringEscapeUtils.escapeXml(address.getPobox()));
                    buffer.append("</PO_BOX>");
                }

                buffer.append("<CITY>");
                buffer.append(address.getPostalcode());
                buffer.append(' ');
                buffer.append(StringEscapeUtils.escapeXml(address.getTown()));
                buffer.append("</CITY>");

                buffer.append("<COUNTRY>");
                buffer.append(StringEscapeUtils.escapeXml(address.getCountry().getName()));
                buffer.append("</COUNTRY>");

                buffer.append("<EMAIL>");
                buffer.append(customer.getEmail());
                buffer.append("</EMAIL>");
            }
        } else {
            final AddressModel address = getOrderModel().getDeliveryAddress();
            buffer.append("<NAME>");
            buffer.append(StringEscapeUtils.escapeXml(address.getFirstname()));
            buffer.append(' ');
            buffer.append(StringEscapeUtils.escapeXml(address.getLastname()));
            buffer.append("</NAME>");

            buffer.append("<STREET>");
            buffer.append(StringEscapeUtils.escapeXml(address.getLine1()));
            buffer.append(' ');
            buffer.append(StringEscapeUtils.escapeXml(address.getLine2()));
            buffer.append("</STREET>");

            if (StringUtils.isNotBlank(address.getPobox())) {
                buffer.append("<PO_BOX>");
                buffer.append(StringEscapeUtils.escapeXml(address.getPobox()));
                buffer.append("</PO_BOX>");
            }

            buffer.append("<CITY>");
            buffer.append(address.getPostalcode());
            buffer.append(' ');
            buffer.append(StringEscapeUtils.escapeXml(address.getTown()));
            buffer.append("</CITY>");

            buffer.append("<COUNTRY>");
            buffer.append(StringEscapeUtils.escapeXml(address.getCountry().getName()));
            buffer.append("</COUNTRY>");

            buffer.append("<EMAIL>");
            buffer.append(address.getEmail());
            buffer.append("</EMAIL>");
        }

        return buffer.toString();
    }

    public OrderModel getOrderModel() {
        return orderModel;
    }

    public void setOrderModel(final OrderModel orderModel) {
        this.orderModel = orderModel;
    }

    public ProcessContextResolutionStrategy getProcessContextResolutionStrategy() {
        return (ProcessContextResolutionStrategy) Registry.getApplicationContext().getBean("processContextResolutionStrategy");
    }

    public DistPriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    @Required
    public void setPriceDataFactory(final DistPriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

    public MediaService getMediaService() {
        return mediaService;
    }

    @Required
    public void setMediaService(final MediaService mediaService) {
        this.mediaService = mediaService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Required
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }
}
