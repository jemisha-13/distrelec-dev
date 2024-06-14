package com.namics.distrelec.b2b.core.interceptor;

import com.namics.distrelec.b2b.core.interceptor.exceptions.DistValidationInterceptorException;
import com.namics.distrelec.b2b.core.model.pim.config.PimImportConfigModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimConstants.PIM_IMPORT_CONFIG_CODE;

public class PimImportConfigValidateInterceptor implements ValidateInterceptor<PimImportConfigModel> {

    private static final String EXCEPTION_MESSAGE_KEY_SINGLE_RECORD = "validations.pimimportconfig.single.record";

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    @Override
    public void onValidate(PimImportConfigModel pimImportConfig, InterceptorContext interceptorContext)
      throws DistValidationInterceptorException {
        if (!PIM_IMPORT_CONFIG_CODE.equals(pimImportConfig.getCode())) {
            throw new DistValidationInterceptorException(getExceptionMessageForSingleRecord());
        }
    }

    private String getExceptionMessageForSingleRecord() {
        return getL10nService().getLocalizedString(EXCEPTION_MESSAGE_KEY_SINGLE_RECORD);
    }

    public L10NService getL10nService() {
        return l10nService;
    }

    public void setL10nService(final L10NService l10nService) {
        this.l10nService = l10nService;
    }
}
