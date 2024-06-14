package com.namics.distrelec.occ.core.readonly;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.configuration.Configuration;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static de.hybris.platform.jalo.flexiblesearch.internal.ReadOnlyConditionsHelper.CTX_ENABLE_FS_ON_READ_REPLICA;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReadOnlyAspectTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private JaloSession jaloSession;

    @Mock
    private SessionContext sessionContext;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @InjectMocks
    private ReadOnlyAspect readOnlyAspect;


    @Before
    public void setUp() {
        when(configurationService.getConfiguration())
                .thenReturn(configuration);
        Session session = mock(Session.class);
        when(sessionService.getCurrentSession())
                .thenReturn(session);
        when(sessionService.getRawSession(eq(session)))
                .thenReturn(jaloSession);
        when(jaloSession.createLocalSessionContext())
                .thenReturn(sessionContext);
        when(proceedingJoinPoint.getSignature())
                .thenReturn(mock(Signature.class));
    }

    @Test
    public void shouldSkipAspectWhenDisabled() throws Throwable {
        when(configuration.getBoolean(eq(ReadOnlyAspect.READ_ONLY_ASPECT_ENABLED), eq(false)))
                .thenReturn(false);

        readOnlyAspect.executeReadOnlyMethodsInReadOnlyContext(proceedingJoinPoint);

        verify(sessionContext, never()).setAttribute(eq(CTX_ENABLE_FS_ON_READ_REPLICA), eq(true));
        verify(sessionContext, never()).setAttribute(eq(ReadOnlyAspect.READ_ONLY_ASPECT_ACTIVE), eq(true));
        verify(sessionContext, never()).setAttribute(eq(ReadOnlyAspect.READ_ONLY_ASPECT_METHOD_SIGNATURE), anyString());
        verify(proceedingJoinPoint, times(1)).proceed();
    }

    @Test
    public void shouldExecuteInReadOnlyWhenEnabled() throws Throwable {
        when(configuration.getBoolean(eq(ReadOnlyAspect.READ_ONLY_ASPECT_ENABLED), eq(false)))
                .thenReturn(true);

        readOnlyAspect.executeReadOnlyMethodsInReadOnlyContext(proceedingJoinPoint);

        verify(sessionContext, times(1)).setAttribute(eq(CTX_ENABLE_FS_ON_READ_REPLICA), eq(true));
        verify(sessionContext, times(1)).setAttribute(eq(ReadOnlyAspect.READ_ONLY_ASPECT_ACTIVE), eq(true));
        verify(sessionContext, times(1)).setAttribute(eq(ReadOnlyAspect.READ_ONLY_ASPECT_METHOD_SIGNATURE), anyString());
        verify(proceedingJoinPoint, times(1)).proceed();
    }

    @Test
    public void shouldCleanUpSessionOnSuccess() throws Throwable {
        when(configuration.getBoolean(eq(ReadOnlyAspect.READ_ONLY_ASPECT_ENABLED), eq(false)))
                .thenReturn(true);
        when(proceedingJoinPoint.proceed())
                .thenReturn("OK");

        readOnlyAspect.executeReadOnlyMethodsInReadOnlyContext(proceedingJoinPoint);

        verify(jaloSession, times(1)).removeLocalSessionContext();
    }

    @Test
    public void shouldCleanUpSessionOnFailure() throws Throwable {
        when(configuration.getBoolean(eq(ReadOnlyAspect.READ_ONLY_ASPECT_ENABLED), eq(false)))
                .thenReturn(true);
        when(proceedingJoinPoint.proceed())
                .thenThrow(new RuntimeException("FAILURE"));

        try {
            readOnlyAspect.executeReadOnlyMethodsInReadOnlyContext(proceedingJoinPoint);
        } catch (Throwable ignored){
            // IGNORE
        }

        verify(jaloSession, times(1)).removeLocalSessionContext();
    }

}