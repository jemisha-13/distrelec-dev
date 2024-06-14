/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.customer.error.feedback.dao.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.customer.error.feedback.data.DistCustomerErrorFeedbackData;
import com.namics.distrelec.b2b.core.model.customer.DistCustomerErrorFeedbackModel;
import com.namics.distrelec.b2b.core.service.customer.error.feedback.DistCustomerErrorFeedbackService;
import com.namics.distrelec.b2b.core.service.customer.error.feedback.dao.DistCustomerErrorFeedbackDao;

import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;

/**
 * {@code DefaultDistCustomerErrorFeedbackDao}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.13
 */
public class DefaultDistCustomerErrorFeedbackDao extends AbstractItemDao implements DistCustomerErrorFeedbackDao {

    private static final Logger LOG = Logger.getLogger(DistCustomerErrorFeedbackService.class);

    @Autowired
    private CommonI18NService commonI18NService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.customer.error.feedback.dao.DistCustomerErrorFeedbackDao#create(com.namics.distrelec.b2b.core.
     * customer.error.feedback.data.DistCustomerErrorFeedbackData)
     */
    @Override
    public boolean create(final DistCustomerErrorFeedbackData data) {
        try {
            final DistCustomerErrorFeedbackModel model = getModelService().create(DistCustomerErrorFeedbackModel.class);
            model.setErrorDescription(data.getErrorDescription());
            model.setErrorReason(data.getErrorReason());
            model.setCustomerEmailId(data.getCustomerEmailId());
            model.setCustomerName(data.getCustomerName());
            model.setProductId(data.getProductId());
            model.setErpCustomerId(data.getErpCustomerId());
            if (StringUtils.isNotBlank(data.getLanguageIso())) {
                model.setLanguage(getCommonI18NService().getLanguage(data.getLanguageIso()));
            }
            getModelService().save(model);
            return true;
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }
}
