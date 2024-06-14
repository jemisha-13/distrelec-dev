package com.namics.distrelec.occ.core.readonly;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadOnlyAspectMonitorInterceptor implements ValidateInterceptor<ItemModel> {

    private static final Logger LOG = LoggerFactory.getLogger(ReadOnlyAspectMonitorInterceptor.class);

    private final SessionService sessionService;

    public ReadOnlyAspectMonitorInterceptor(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void onValidate(ItemModel item, InterceptorContext interceptorContext) {
        Session session = sessionService.getCurrentSession();

        boolean isReadOnlyAspectActive = BooleanUtils.isTrue(session.getAttribute(ReadOnlyAspect.READ_ONLY_ASPECT_ACTIVE));
        if (isReadOnlyAspectActive) {
            String readOnlyMethodSignature = session.getAttribute(ReadOnlyAspect.READ_ONLY_ASPECT_METHOD_SIGNATURE);
            LOG.warn("Item {} with PK {} was being saved to database while executing readonly method {}", item.toString(), item.getPk(), readOnlyMethodSignature);
        }
    }
}
