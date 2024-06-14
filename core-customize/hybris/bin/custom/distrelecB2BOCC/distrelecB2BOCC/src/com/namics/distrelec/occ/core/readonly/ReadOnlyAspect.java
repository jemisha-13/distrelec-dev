package com.namics.distrelec.occ.core.readonly;

import static de.hybris.platform.jalo.flexiblesearch.internal.ReadOnlyConditionsHelper.CTX_ENABLE_FS_ON_READ_REPLICA;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Aspect
public class ReadOnlyAspect {

    public static final String READ_ONLY_ASPECT_ACTIVE = "readOnlyAspectActive";

    public static final String READ_ONLY_ASPECT_METHOD_SIGNATURE = "readOnlyAspectMethodSignature";

    public static final String READ_ONLY_ASPECT_ENABLED = "distrelecB2BOCC.read-only-aspect.enabled";

    private static final Logger LOG = LoggerFactory.getLogger(ReadOnlyAspect.class);

    private final ConfigurationService configurationService;

    private final SessionService sessionService;

    public ReadOnlyAspect(ConfigurationService configurationService, SessionService sessionService) {
        this.configurationService = configurationService;
        this.sessionService = sessionService;
    }

    @Pointcut("execution(public * *(..)) && @annotation(com.namics.distrelec.occ.core.readonly.ReadOnly)")
    private void readOnlyMethods() {}

    @Around("readOnlyMethods()")
    public Object executeReadOnlyMethodsInReadOnlyContext(ProceedingJoinPoint pjp) throws Throwable {
        if(!isReadOnlyAspectEnabled()){
            LOG.debug("Skipping read-replica activation because aspect is disabled");
            return pjp.proceed();
        }

        try {
            SessionContext ctx = createLocalSessionContext();
            ctx.setAttribute(CTX_ENABLE_FS_ON_READ_REPLICA, true);
            ctx.setAttribute(READ_ONLY_ASPECT_ACTIVE, true);
            ctx.setAttribute(READ_ONLY_ASPECT_METHOD_SIGNATURE, pjp.getSignature().toString());
            LOG.debug("ReadOnlyAspect activated read-replica data source before {}", pjp.getSignature());
            return pjp.proceed();
        } finally {
            removeLocalSessionContext();
            LOG.debug("ReadOnlyAspect deactivated read-replica data source after {}", pjp.getSignature());
        }
    }

    private boolean isReadOnlyAspectEnabled() {
        return configurationService.getConfiguration().getBoolean(READ_ONLY_ASPECT_ENABLED, false);
    }

    private SessionContext createLocalSessionContext() {
        Session session = this.sessionService.getCurrentSession();
        JaloSession jaloSession = (JaloSession)this.sessionService.getRawSession(session);
        return jaloSession.createLocalSessionContext();
    }

    private void removeLocalSessionContext() {
        Session session = this.sessionService.getCurrentSession();
        JaloSession jaloSession = (JaloSession)this.sessionService.getRawSession(session);
        jaloSession.removeLocalSessionContext();
    }

}
