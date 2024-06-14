package com.distrelec.solrfacetsearch.indexer.impl;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.flexiblesearch.internal.ReadOnlyConditionsHelper;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerContextFactory;

import static de.hybris.platform.jalo.flexiblesearch.internal.ReadOnlyConditionsHelper.CTX_ENABLE_FS_ON_READ_REPLICA;
import static java.lang.Boolean.FALSE;

public class DistIndexerContextFactory extends DefaultIndexerContextFactory {

    private static final Logger LOG = LogManager.getLogger(DistIndexerContextFactory.class);

    private static final String DISABLE_READONLY_FOR_AFTER_LISTENERS = "distrelecfusionintegration.disable.readonly.for.after.listeners";

    @Autowired
    private ConfigurationService configurationService;

    private ReadOnlyConditionsHelper readOnlyConditionsHelper = new ReadOnlyConditionsHelper();

    @Override
    public void destroyContext() throws IndexerException {
        handleDataSourceForLocalSession();
        super.destroyContext();
        LOG.debug("Read only replica is enabled outside local session context: {}", isReadOnlyDataSourceEnabled());
    }

    @Override
    public void destroyContext(Exception failureException) {
        handleDataSourceForLocalSession();
        super.destroyContext(failureException);
        LOG.debug("Read only replica is enabled outside local session context: {}", isReadOnlyDataSourceEnabled());
    }

    private void handleDataSourceForLocalSession() {
        LOG.debug("Read only replica is enabled for the job: {}", isReadOnlyDataSourceEnabled());
        if (shouldDisableReadOnlyDataSource()) {
            turnOffReadOnlyReplicaForLocalSessionContext();
        }
        LOG.debug("Read only replica is enabled before executing listeners and destroying context? {}", isReadOnlyDataSourceEnabled());
    }

    private boolean isReadOnlyDataSourceEnabled() {
        Optional<Boolean> isReadOnlyDataSourceEnabled = readOnlyConditionsHelper.readOnlyDataSourceEnabledInSessionContext(getSessionContext());
        return isReadOnlyDataSourceEnabled.isEmpty() ? FALSE : isReadOnlyDataSourceEnabled.get();
    }

    private SessionContext getSessionContext() {
        return getJaloSession().getSessionContext();
    }

    private JaloSession getJaloSession() {
        Session currentSession = getSessionService().getCurrentSession();
        return (JaloSession) getSessionService().getRawSession(currentSession);
    }

    /**
     * @return true - if we should disable readonly data source for execution of:
     * <p>
     * AfterIndexListeners in destroyContext() method
     * AfterIndexErrorListeners in destroyContext(Exception ex) method
     */
    private boolean shouldDisableReadOnlyDataSource() {
        return isReadOnlyDataSourceEnabled()
               && configurationService.getConfiguration().getBoolean(DISABLE_READONLY_FOR_AFTER_LISTENERS, true);
    }

    /**
     * There is a small delay between sync of master/readonly replicas, so this is making sure job can read appropriate data to finish updating job statuses.
     */
    private void turnOffReadOnlyReplicaForLocalSessionContext() {
        LOG.debug("Turning off read only replica before executing listeners and destroying context");
        SessionContext sessionContext = getSessionContext();
        sessionContext.removeAttribute(CTX_ENABLE_FS_ON_READ_REPLICA);
    }
}
