/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.process.email.context;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.process.PaymentNotifyProcessModel;
import com.namics.distrelec.b2b.core.service.order.daos.DistB2BOrderDao;
import com.namics.distrelec.b2b.core.service.process.email.context.AbstractDistEmailContext;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;

/**
 * {@code PaymentNotifyOrderEmailContext}
 * <p>
 * Email context for the Payment notify order creation email
 * </p>
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.4
 */
public class PaymentNotifyOrderEmailContext extends AbstractDistEmailContext<PaymentNotifyProcessModel> {

    private String userUID;
    private String userEmail;
    private String customerType;
    private String cartNumber;
    private String erpOrderNumber;
    private String notifyTime;

    // Spring Beans
    private DistB2BOrderDao b2bOrderDao;

    /*
     * (non-Javadoc)
     *
     * @see
     * com.namics.distrelec.b2b.core.service.process.email.context.AbstractDistEmailContext#init(de.hybris.platform.processengine.model.
     * BusinessProcessModel, de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel)
     */
    @Override
    public void init(final PaymentNotifyProcessModel paymentNotifyProcess, final EmailPageModel emailPageModel) {
        put(THEME, null);
        put(FROM_EMAIL, emailPageModel.getFromEmail());
        put(FROM_DISPLAY_NAME, emailPageModel.getFromName());
        put(EMAIL, getConfigurationService().getConfiguration().getProperty(DistConstants.PropKey.Email.PAYMENT_NOTIFY_ORDER_EMAIL));
        put(DISPLAY_NAME, getConfigurationService().getConfiguration().getProperty(DistConstants.PropKey.Email.PAYMENT_NOTIFY_ORDER_DISPLAY_NAME));

        // DISTRELEC-2427: add string escape utility to email context
        put("StringEscapeUtils", StringEscapeUtils.class);

        final String cartNr = paymentNotifyProcess.getCartCode();
        final OrderModel order = getB2bOrderDao().findOrderByCode(cartNr);
        if (order == null) {
            throw new IllegalArgumentException("No order found for code: " + cartNr);
        }
        // Customer related data
        final CustomerModel customer = (order.getUser() instanceof CustomerModel) ? (CustomerModel) order.getUser() : null;
        setUserUID(customer != null ? customer.getUid() : StringUtils.EMPTY);
        setUserEmail(customer != null ? customer.getContactEmail() : StringUtils.EMPTY);
        setCustomerType(customer != null ? customer.getCustomerType().getCode() : StringUtils.EMPTY);
        // Order related data
        setCartNumber(cartNr);
        setErpOrderNumber(order.getErpOrderCode());
        // Payment notify request time
        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss",
                getCommonI18NService().getLocaleForLanguage(getEmailLanguage(paymentNotifyProcess)));
        setNotifyTime(sdf.format(paymentNotifyProcess.getNotifyTime()));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext#getSite(de.hybris.platform.processengine.model.
     * BusinessProcessModel)
     */
    @Override
    protected BaseSiteModel getSite(final PaymentNotifyProcessModel businessProcessModel) {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext#getCustomer(de.hybris.platform.processengine.model.
     * BusinessProcessModel)
     */
    @Override
    protected CustomerModel getCustomer(final PaymentNotifyProcessModel businessProcessModel) {
        return null;
    }

    // Getters & Setters

    public DistB2BOrderDao getB2bOrderDao() {
        return b2bOrderDao;
    }

    public void setB2bOrderDao(final DistB2BOrderDao b2bOrderDao) {
        this.b2bOrderDao = b2bOrderDao;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(final String userUID) {
        this.userUID = userUID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(final String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(final String customerType) {
        this.customerType = customerType;
    }

    public String getCartNumber() {
        return cartNumber;
    }

    public void setCartNumber(final String cartNumber) {
        this.cartNumber = cartNumber;
    }

    public String getErpOrderNumber() {
        return erpOrderNumber;
    }

    public void setErpOrderNumber(final String erpOrderNumber) {
        this.erpOrderNumber = erpOrderNumber;
    }

    public String getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(final String notifyTime) {
        this.notifyTime = notifyTime;
    }
}
