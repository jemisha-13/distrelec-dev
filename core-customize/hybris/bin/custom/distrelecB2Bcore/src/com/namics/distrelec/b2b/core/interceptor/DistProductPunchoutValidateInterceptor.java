package com.namics.distrelec.b2b.core.interceptor;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.DistCOPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistCUPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistProductPunchOutFilterModel;

import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

public class DistProductPunchoutValidateInterceptor implements ValidateInterceptor {
    
    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;
    
    public DistProductPunchoutValidateInterceptor() {
    }

    @Override
    public void onValidate(Object model, InterceptorContext ctx) throws InterceptorException {
        if (!(model instanceof DistProductPunchOutFilterModel) || model instanceof DistCOPunchOutFilterModel || model instanceof DistCUPunchOutFilterModel) {
            return;
        }
        final DistProductPunchOutFilterModel punchout = (DistProductPunchOutFilterModel) model;
        if (StringUtils.isBlank(punchout.getProductHierarchy()) && punchout.getProduct() == null) {
            throw new InterceptorException(l10nService.getLocalizedString("validations.punchout.producthiearchy"));
        }
        else if (StringUtils.isNotBlank(punchout.getProductHierarchy()) && punchout.getProduct() != null) {
            throw new InterceptorException(l10nService.getLocalizedString("validations.punchout.onlyonevalue"));
        }
    }

}
