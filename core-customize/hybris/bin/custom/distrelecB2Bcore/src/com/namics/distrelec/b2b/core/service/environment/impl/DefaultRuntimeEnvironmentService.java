package com.namics.distrelec.b2b.core.service.environment.impl;

import com.namics.distrelec.b2b.core.service.environment.RuntimeEnvironmentConstants;
import com.namics.distrelec.b2b.core.service.environment.RuntimeEnvironmentService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.lang3.BooleanUtils;

public class DefaultRuntimeEnvironmentService implements RuntimeEnvironmentService {

    private final SessionService sessionService;

    public DefaultRuntimeEnvironmentService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public boolean isHeadless() {
        return BooleanUtils.isTrue(sessionService.getAttribute(RuntimeEnvironmentConstants.HEADLESS));
    }

    @Override
    public void setHeadless(boolean headless) {
        sessionService.setAttribute(RuntimeEnvironmentConstants.HEADLESS, headless);
    }
}
