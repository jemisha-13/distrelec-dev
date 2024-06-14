/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.enums.GenericSanityCheckType;
import com.namics.distrelec.b2b.core.model.jobs.DistGenericSanityCheckCronJobModel;

import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * This interceptor is used to validate the type DistGenericSanityCheckCronJob.
 * 
 * @author pforster, Namics AG
 * @version 3.0.0
 * 
 */
public class DistGenericSanityCheckCronJobValidateInterceptor implements ValidateInterceptor {

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    @Override
    public void onValidate(Object model, InterceptorContext ctx) throws InterceptorException {
        DistGenericSanityCheckCronJobModel cronJobModel = (DistGenericSanityCheckCronJobModel) model;

        if (GenericSanityCheckType.ABSOLUTSANITYCHECK.equals(cronJobModel.getType())) {
            if (cronJobModel.getThreshold() < Double.parseDouble("0.0")) {
                throw new InterceptorException(l10nService.getLocalizedString("validations.genericsanitycheckcronjob.absolute.threshold",
                        new Object[] { cronJobModel.getThreshold() }));
            }
        } else if (GenericSanityCheckType.PERCENTAGESANITYCHECK.equals(cronJobModel.getType())) {
            if (cronJobModel.getThreshold() < Double.parseDouble("0.0") || cronJobModel.getThreshold() > Double.parseDouble("100.0")) {
                throw new InterceptorException(l10nService.getLocalizedString("validations.genericsanitycheckcronjob.percentage.threshold"));
            }

            if (StringUtils.isEmpty(cronJobModel.getBaseQuery())) {
                throw new InterceptorException(l10nService.getLocalizedString("validations.genericsanitycheckcronjob.percentage.basequery"));
            }
        }
    }

    public L10NService getL10nService() {
        return l10nService;
    }

    public void setL10nService(L10NService l10nService) {
        this.l10nService = l10nService;
    }

}
