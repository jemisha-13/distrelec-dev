package com.namics.distrelec.b2b.core.interceptor;

import com.namics.distrelec.b2b.core.interceptor.exceptions.DistValidationInterceptorException;
import com.namics.distrelec.b2b.core.model.blocking.rule.DistBlockedUserAddressRuleModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class DistBlockedUserAddressRuleValidateInterceptor implements ValidateInterceptor<DistBlockedUserAddressRuleModel> {

    private static final String EXCEPTION_MESSAGE_KEY_BLANK = "validations.distblockeduseraddressrule.regex.blank";

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    @Override
    public void onValidate(DistBlockedUserAddressRuleModel addressRule, InterceptorContext interceptorContext)
      throws DistValidationInterceptorException {
        if (isBlank(addressRule.getPostalCode()) && isBlank(addressRule.getStreetName()) && isBlank(addressRule.getStreetNumber())
              && isBlank(addressRule.getCity())) {
            throw new DistValidationInterceptorException(getExceptionMessageForBlankElements());
        }
    }

    private String getExceptionMessageForBlankElements() {
        return l10nService.getLocalizedString(EXCEPTION_MESSAGE_KEY_BLANK);
    }

}
