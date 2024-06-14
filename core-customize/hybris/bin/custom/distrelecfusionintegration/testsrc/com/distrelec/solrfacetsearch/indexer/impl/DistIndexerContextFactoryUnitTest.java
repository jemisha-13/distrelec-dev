package com.distrelec.solrfacetsearch.indexer.impl;

import java.util.List;
import java.util.Optional;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.flexiblesearch.internal.ReadOnlyConditionsHelper;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.common.ListenersFactory;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;

import static de.hybris.platform.jalo.flexiblesearch.internal.ReadOnlyConditionsHelper.CTX_ENABLE_FS_ON_READ_REPLICA;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistIndexerContextFactoryUnitTest {

    private static final String DISABLE_READONLY_FOR_AFTER_LISTENERS = "distrelecfusionintegration.disable.readonly.for.after.listeners";

    @InjectMocks
    private DistIndexerContextFactory distIndexerContextFactory;

    @Mock
    private ListenersFactory listenersFactory;

    @Mock
    private SessionService sessionService;

    @Mock
    private Session currentSession;

    @Mock
    private JaloSession rawSession;

    @Mock
    private SessionContext sessionContext;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    @Mock
    private ReadOnlyConditionsHelper readOnlyConditionsHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(distIndexerContextFactory, "readOnlyConditionsHelper", readOnlyConditionsHelper);

        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getBoolean(DISABLE_READONLY_FOR_AFTER_LISTENERS, true)).thenReturn(true);

        when(sessionService.getCurrentSession()).thenReturn(currentSession);
        when(sessionService.getRawSession(currentSession)).thenReturn(rawSession);
        when(rawSession.getSessionContext()).thenReturn(sessionContext);

        distIndexerContextFactory.createContext(1, IndexOperation.FULL, false,
                                                new FacetSearchConfig(), new IndexedType(), List.of());
    }

    @Test
    public void testDestroyContextReadOnlyEnabledFlagEnabled() throws IndexerException {
        when(configuration.getBoolean(DISABLE_READONLY_FOR_AFTER_LISTENERS, true)).thenReturn(true);
        when(readOnlyConditionsHelper.readOnlyDataSourceEnabledInSessionContext(sessionContext)).thenReturn(Optional.of(TRUE));

        distIndexerContextFactory.destroyContext();

        verify(sessionContext, times(1)).removeAttribute(eq(CTX_ENABLE_FS_ON_READ_REPLICA));
    }

    @Test
    public void testDestroyContextReadOnlyEnabledFlagDisabled() throws IndexerException {
        when(configuration.getBoolean(DISABLE_READONLY_FOR_AFTER_LISTENERS, true)).thenReturn(false);
        when(readOnlyConditionsHelper.readOnlyDataSourceEnabledInSessionContext(sessionContext)).thenReturn(Optional.of(TRUE));

        distIndexerContextFactory.destroyContext();

        verify(sessionContext, times(0)).removeAttribute(eq(CTX_ENABLE_FS_ON_READ_REPLICA));
    }

    @Test
    public void testDestroyContextReadOnlyDisableddFlagEnabled() throws IndexerException {
        when(readOnlyConditionsHelper.readOnlyDataSourceEnabledInSessionContext(sessionContext)).thenReturn(Optional.of(FALSE));

        distIndexerContextFactory.destroyContext();

        verify(sessionContext, times(0)).removeAttribute(eq(CTX_ENABLE_FS_ON_READ_REPLICA));
    }

    @Test
    public void testDestroyContextReadOnlyDisabledFlagDisabled() throws IndexerException {
        when(readOnlyConditionsHelper.readOnlyDataSourceEnabledInSessionContext(sessionContext)).thenReturn(Optional.of(FALSE));

        distIndexerContextFactory.destroyContext();

        verify(sessionContext, times(0)).removeAttribute(eq(CTX_ENABLE_FS_ON_READ_REPLICA));
    }

    @Test
    public void testDestroyContextExceptionReadOnlyEnabledFlagEnabled() throws IndexerException {
        when(configuration.getBoolean(DISABLE_READONLY_FOR_AFTER_LISTENERS, true)).thenReturn(true);
        when(readOnlyConditionsHelper.readOnlyDataSourceEnabledInSessionContext(sessionContext)).thenReturn(Optional.of(TRUE));

        distIndexerContextFactory.destroyContext(new UnknownIdentifierException("operation not found: {id=112004011835797669}"));

        verify(sessionContext, times(1)).removeAttribute(eq(CTX_ENABLE_FS_ON_READ_REPLICA));
    }

    @Test
    public void testDestroyContextExceptionReadOnlyEnabledFlagDisabled() throws IndexerException {
        when(configuration.getBoolean(DISABLE_READONLY_FOR_AFTER_LISTENERS, true)).thenReturn(false);
        when(readOnlyConditionsHelper.readOnlyDataSourceEnabledInSessionContext(sessionContext)).thenReturn(Optional.of(TRUE));

        distIndexerContextFactory.destroyContext(new UnknownIdentifierException("operation not found: {id=112004011835797669}"));

        verify(sessionContext, times(0)).removeAttribute(eq(CTX_ENABLE_FS_ON_READ_REPLICA));
    }

    @Test
    public void testDestroyContextExceptionReadOnlyDisableddFlagEnabled() throws IndexerException {
        when(readOnlyConditionsHelper.readOnlyDataSourceEnabledInSessionContext(sessionContext)).thenReturn(Optional.of(FALSE));

        distIndexerContextFactory.destroyContext(new UnknownIdentifierException("operation not found: {id=112004011835797669}"));

        verify(sessionContext, times(0)).removeAttribute(eq(CTX_ENABLE_FS_ON_READ_REPLICA));
    }

    @Test
    public void testDestroyContextExceptionReadOnlyDisableddFlagDisabled() throws IndexerException {
        when(readOnlyConditionsHelper.readOnlyDataSourceEnabledInSessionContext(sessionContext)).thenReturn(Optional.of(FALSE));

        distIndexerContextFactory.destroyContext(new UnknownIdentifierException("operation not found: {id=112004011835797669}"));

        verify(sessionContext, times(0)).removeAttribute(eq(CTX_ENABLE_FS_ON_READ_REPLICA));
    }

}
