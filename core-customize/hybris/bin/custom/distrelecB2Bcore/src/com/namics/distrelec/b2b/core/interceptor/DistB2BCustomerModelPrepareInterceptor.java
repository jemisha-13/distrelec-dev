package com.namics.distrelec.b2b.core.interceptor;

import com.namics.distrelec.b2b.core.service.i18n.impl.DistCommerceCommonI18NService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import org.springframework.beans.factory.annotation.Autowired;

public class DistB2BCustomerModelPrepareInterceptor implements PrepareInterceptor<B2BCustomerModel> {

    @Autowired
    private DistCommerceCommonI18NService distCommerceCommonI18NService;

    @Override
    public void onPrepare(B2BCustomerModel b2BCustomerModel, InterceptorContext interceptorContext) {
        LanguageModel currentLanguage = b2BCustomerModel.getSessionLanguage();

        if (currentLanguage != null) {
            LanguageModel baseLanguage = getDistCommerceCommonI18NService().getBaseLanguage(currentLanguage);
            b2BCustomerModel.setSessionLanguage(baseLanguage);
        }
    }

    public DistCommerceCommonI18NService getDistCommerceCommonI18NService() {
        return distCommerceCommonI18NService;
    }

    public void setDistCommerceCommonI18NService(final DistCommerceCommonI18NService distCommerceCommonI18NService) {
        this.distCommerceCommonI18NService = distCommerceCommonI18NService;
    }
}
