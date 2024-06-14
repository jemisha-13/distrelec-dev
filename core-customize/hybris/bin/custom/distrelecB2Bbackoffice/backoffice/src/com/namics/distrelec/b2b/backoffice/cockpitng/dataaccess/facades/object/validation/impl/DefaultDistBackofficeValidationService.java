package com.namics.distrelec.b2b.backoffice.cockpitng.dataaccess.facades.object.validation.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.hybris.backoffice.cockpitng.dataaccess.facades.object.validation.impl.DefaultBackofficeValidationService;
import com.hybris.cockpitng.validation.ValidationContext;
import com.hybris.cockpitng.validation.model.ValidationInfo;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorRegistry;
import de.hybris.platform.servicelayer.interceptor.PersistenceOperation;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelService;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelServiceInterceptorContext;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Introduced because original validation service does not call prepare interceptors before validation as hmc.
 */
public class DefaultDistBackofficeValidationService extends DefaultBackofficeValidationService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistBackofficeValidationService.class);

    private InterceptorRegistry interceptorRegistry;

    @Override
    public List<ValidationInfo> validate(Object objectToValidate, ValidationContext validationContext) {
        if (objectToValidate != null && objectToValidate instanceof ItemModel) {
            invokePrepareInterceptors(objectToValidate);
        }

        return super.validate(objectToValidate, validationContext);
    }

    private void invokePrepareInterceptors(Object objectToValidate) {
        ItemModel itemModel = (ItemModel) objectToValidate;
        Collection<PrepareInterceptor> prepareInterceptors = getInterceptorRegistry().getPrepareInterceptors(itemModel.getItemtype());
        if (CollectionUtils.isNotEmpty(prepareInterceptors)) {
            InterceptorContext interceptorContext = new DefaultModelServiceInterceptorContext(
                (DefaultModelService) getModelService(), PersistenceOperation.SAVE, Collections.emptyList(), null);
            for (PrepareInterceptor prepareInterceptor : prepareInterceptors) {
                try {
                    prepareInterceptor.onPrepare(objectToValidate, interceptorContext);
                } catch (Exception e) {
                    LOG.warn("Unable to prepare model", e);
                    // just continue
                }
            }
        }
    }


    public InterceptorRegistry getInterceptorRegistry() {
        return interceptorRegistry;
    }

    @Required
    public void setInterceptorRegistry(InterceptorRegistry interceptorRegistry) {
        this.interceptorRegistry = interceptorRegistry;
    }
}
