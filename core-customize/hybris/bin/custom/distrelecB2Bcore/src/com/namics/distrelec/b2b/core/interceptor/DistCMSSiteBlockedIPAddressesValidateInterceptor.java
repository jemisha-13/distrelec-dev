package com.namics.distrelec.b2b.core.interceptor;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.namics.distrelec.b2b.core.interceptor.util.ValidationUtil.ifAnyElementIsNullThrowValidationException;
import static com.namics.distrelec.b2b.core.interceptor.util.ValidationUtil.ifAnyRegexIsInvalidThrowValidationException;

public class DistCMSSiteBlockedIPAddressesValidateInterceptor implements ValidateInterceptor<CMSSiteModel> {

    private static final String EXCEPTION_MESSAGE_KEY_INVALID = "validations.cmssite.blocked.ip.addresses.regex.invalid";

    private static final String EXCEPTION_MESSAGE_KEY_NULL = "validations.cmssite.blocked.ip.addresses.regex.null";

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    @Override
    public void onValidate(CMSSiteModel cmsSiteModel, InterceptorContext interceptorContext) throws InterceptorException {
        List<String> blockedIPAddresses = Optional.ofNullable(cmsSiteModel)
                                                  .map(CMSSiteModel::getBlockedIPAddresses)
                                                  .orElse(Collections.emptyList());
        ifAnyElementIsNullThrowValidationException(blockedIPAddresses, this::getExceptionMessageForNullElements);

        ifAnyRegexIsInvalidThrowValidationException(blockedIPAddresses, this::getExceptionMessageForInvalidElements);
    }

    private String getExceptionMessageForNullElements() {
        return l10nService.getLocalizedString(EXCEPTION_MESSAGE_KEY_NULL);
    }

    private String getExceptionMessageForInvalidElements(String element) {
        return l10nService.getLocalizedString(EXCEPTION_MESSAGE_KEY_INVALID, new Object[] {element});
    }
}
