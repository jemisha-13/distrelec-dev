package com.namics.distrelec.b2b.core.interceptor;

import com.namics.distrelec.b2b.core.event.DistManufacturerPunchoutEvent;
import com.namics.distrelec.b2b.core.model.DistManufacturerPunchOutFilterModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import org.springframework.beans.factory.annotation.Autowired;

public class DistManufacturerPunchoutInterceptor implements PrepareInterceptor<DistManufacturerPunchOutFilterModel> {

    @Autowired
    private EventService eventService;

    @Override
    public void onPrepare(DistManufacturerPunchOutFilterModel distManufacturerPunchOutFilter, InterceptorContext interceptorContext) {
        eventService.publishEvent(new DistManufacturerPunchoutEvent(distManufacturerPunchOutFilter.getSalesOrg().getCmsSites()));
    }
}
