/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.errorFeedback.impl;

import com.namics.distrelec.b2b.core.customer.error.feedback.data.DistCustomerErrorFeedbackData;
import com.namics.distrelec.b2b.core.service.customer.error.feedback.DistCustomerErrorFeedbackService;
import com.namics.distrelec.b2b.facades.errorFeedback.DistCustomerFeedbackFacade;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

/**
 * {@code DefaultDistCustomerFeedbackFacade}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.13
 */
public class DefaultDistCustomerFeedbackFacade implements DistCustomerFeedbackFacade {

    @Autowired
    private DistCustomerErrorFeedbackService customerErrorFeedbackService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private ProductService productService;

    @Autowired
    private I18NService i18NService;

    /*
     * (non-Javadoc)
     *
     * @see
     * com.namics.distrelec.b2b.facades.errorFeedback.DistCustomerFeedbackFacade#saveFeedbackReport(com.namics.distrelec.b2b.core.customer.
     * error.feedback.data.DistCustomerErrorFeedbackData)
     */
    @Override
    public boolean saveFeedbackReport(final DistCustomerErrorFeedbackData data) {
        addMoreData(data);
        return getCustomerErrorFeedbackService().saveCustomerErrorFeedback(data);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.namics.distrelec.b2b.facades.errorFeedback.DistCustomerFeedbackFacade#disseminateFeedbackRepor(com.namics.distrelec.b2b.core.
     * customer.error.feedback.data.DistCustomerErrorFeedbackData)
     */
    @Override
    public void disseminateFeedbackRepor(final DistCustomerErrorFeedbackData data) {
        addMoreData(data);
        getCustomerErrorFeedbackService().disseminateCustomerErrorFeedback(data);
    }

    private void addMoreData(final DistCustomerErrorFeedbackData data) {
        data.setLanguageIso(i18NService.getCurrentLocale().getLanguage());
        data.setProductName(getProductService().getProductForCode(data.getProductId()).getName(new Locale("en")));

        if (!(getUserService().isAnonymousUser(getUserService().getCurrentUser()))) {
            if (getCurrentSessionCustomer().getDefaultB2BUnit() != null) {
                data.setErpCustomerId(getCurrentSessionCustomer().getDefaultB2BUnit().getErpCustomerID());
            }
            if (StringUtils.isEmpty(data.getCustomerEmailId())) {
                data.setCustomerName(getUserService().getCurrentUser().getName());
                data.setCustomerEmailId(getCurrentSessionCustomer().getEmail());
            }
        }
    }

    private B2BCustomerModel getCurrentSessionCustomer() {
        return (B2BCustomerModel) getUserService().getCurrentUser();
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public DistCustomerErrorFeedbackService getCustomerErrorFeedbackService() {
        return customerErrorFeedbackService;
    }

    public void setCustomerErrorFeedbackService(final DistCustomerErrorFeedbackService customerErrorFeedbackService) {
        this.customerErrorFeedbackService = customerErrorFeedbackService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }
}
