/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.process.DistrelecCustomerErrorFeedbackProcessModel;

import de.hybris.platform.servicelayer.i18n.CommonI18NService;

/**
 * {@code DistCustomerErrorFeedbackEventListener}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.13
 */
public class DistCustomerErrorFeedbackEventListener
        extends AbstractDistEventListener<DistCustomerErrorFeedbackEvent, DistrelecCustomerErrorFeedbackProcessModel> {

    @Autowired
    private CommonI18NService commonI18NService;

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.event.AbstractDistEventListener#createTarget()
     */
    @Override
    public DistrelecCustomerErrorFeedbackProcessModel createTarget() {
        return (DistrelecCustomerErrorFeedbackProcessModel) getBusinessProcessService()
                .createProcess("distrelecCustomerErrorFeedbackProcess" + System.currentTimeMillis(), "distrelecCustomerErrorFeedbackProcess");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.event.AbstractDistEventListener#validate(de.hybris.platform.servicelayer.event.events.AbstractEvent)
     */
    @Override
    protected boolean validate(final DistCustomerErrorFeedbackEvent event) {
        return event != null && event.getCustomerEmailId() != null && event.getCustomerName() != null && event.getErrorDescription() != null
                && event.getErrorReason() != null && event.getProductId() != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.event.AbstractDistEventListener#populate(de.hybris.platform.servicelayer.event.events.AbstractEvent,
     * de.hybris.platform.processengine.model.BusinessProcessModel)
     */
    @Override
    public void populate(final DistCustomerErrorFeedbackEvent event, final DistrelecCustomerErrorFeedbackProcessModel target) {
        super.populate(event, target);
        target.setProductId(event.getProductId());
        target.setProductName(event.getProductName());
        target.setCustomerEmailId(event.getCustomerEmailId());
        target.setCustomerName(event.getCustomerName());
        target.setErrorDescription(event.getErrorDescription());
        target.setErrorReason(event.getErrorReason());
        target.setErpCustomerId(event.getErpCustomerId());
        if (StringUtils.isNotBlank(event.getLanguageIso())) {
            target.setLanguage(getCommonI18NService().getLanguage(event.getLanguageIso()));
        }
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }
}
