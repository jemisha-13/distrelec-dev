package com.namics.distrelec.b2b.core.inout.pim;

import com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Import;
import com.namics.distrelec.b2b.core.event.PimImportNotificationEvent;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.composite.ParseResult;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.composite.PimExportParserComposite;
import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.CleanupHelper;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.io.File;
import java.util.Date;

/**
 * Integrates distrelec PIM import into hybris spring-integration.
 * <ul>
 * <li>run parser
 * <li>handle result
 * </ul>
 * 
 * @author rhaemmerli, Namics AG
 * @author ceberle, Namics AG
 */
public class PimImportTask {

    private static final Logger LOG = LogManager.getLogger(PimImportTask.class);

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private CatalogVersionService catalogVersionService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("eventService")
    private EventService eventService;

    @Autowired
    private I18NService i18NService;

    private PimExportParserComposite pimExportParserComposite;

    /** cleanup helper injected via spring */
    private CleanupHelper cleanupHelper;

    public void execute(final File file) {
        final long startTimeMillis = System.currentTimeMillis();
        LOG.info("start importing of file [" + file + "]");
        Assert.notNull(file);

        // decrease thread priority
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY - 1);

        final ParseResult result = processFile(file);
        final boolean successful = result.isSuccess();
        final String pimLogStatistics = result.getMessage();
        cleanup(file, !successful);
        final long endTimeMillis = System.currentTimeMillis();
        final long durationSeconds = (endTimeMillis - startTimeMillis) / 1000;
        LOG.info("import of file [" + file + "] took " + durationSeconds + "s and " + (successful ? "succeeded" : "failed"));
        final String message = "Import of file [" + file + "] took " + durationSeconds + "s and " + (successful ? "succeeded" : "failed");
        getEventService()
                .publishEvent(new PimImportNotificationEvent(successful, message, new Date(startTimeMillis), new Date(endTimeMillis), pimLogStatistics));
    }

    /**
     * Cleanup files after processing
     * 
     * @param file
     *            the imported file
     * @param error
     *            true if an error occured, otherwise false
     */
    private void cleanup(final File file, final boolean error) {
        final BatchHeader batchHeader = new BatchHeader();
        batchHeader.setFile(file);
        getCleanupHelper().cleanup(batchHeader, error);
    }

    /**
     * Process the file using a proper (fresh) JaloSession.
     * 
     * @param file
     *            the file to import
     * @return true if the import was successful, otherwise false
     */
    private ParseResult processFile(final File file) {
        JaloSession js = setupSession();

        try {
            final Configuration configuration = getConfigurationService().getConfiguration();
            getCatalogVersionService().setSessionCatalogVersion(configuration.getString(Import.PRODUCT_CATALOG_ID),
                    configuration.getString(Import.PRODUCT_CATALOG_VERSION));

            getI18NService().setLocalizationFallbackEnabled(true);

            UserModel user = getUserService().getUserForUID(getConfigurationService().getConfiguration().getString("import.pim.user"));
            getUserService().setCurrentUser(user);
            return getPimExportParserComposite().parse(file);
        } catch (Exception e)
        {
            LOG.error("error processFile",e);
            return new ParseResult(false, e.getMessage());
        }
        finally {
            js.close();
        }
    }

    /**
     * Ensure we use a proper JaloSession having no rubbish (like httpSessionID) attached during last usage. <br/>
     * Important: Disable timeout to ensure session does not expire.
     * 
     * @return a proper JaloSession
     */
    private JaloSession setupSession() {
        if (!Registry.hasCurrentTenant()) {
            Registry.activateMasterTenant();
        }

        if (JaloSession.hasCurrentSession()) {
            JaloSession currentSession = JaloSession.getCurrentSession();
            LOG.info("Current session exists: sessionID [" + currentSession.getSessionID() + "], httpSessionId [" + currentSession.getHttpSessionId()
                    + "], user [" + currentSession.getUser() + "]. Deactivate current session and create a new one.");
            JaloSession.deactivate();
        }

        JaloSession newSession = JaloSession.getCurrentSession();
        newSession.activate();
        newSession.setTimeout(-1);
        LOG.info("New session: sessionID [" + newSession.getSessionID() + "], httpSessionId [" + newSession.getHttpSessionId() + "], user ["
                + newSession.getUser() + "]. Deactivate current session and create a new one.");
        return newSession;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public CatalogVersionService getCatalogVersionService() {
        return catalogVersionService;
    }

    public void setCatalogVersionService(CatalogVersionService catalogVersionService) {
        this.catalogVersionService = catalogVersionService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    public CleanupHelper getCleanupHelper() {
        return cleanupHelper;
    }

    public void setCleanupHelper(final CleanupHelper cleanupHelper) {
        this.cleanupHelper = cleanupHelper;
    }

    public PimExportParserComposite getPimExportParserComposite() {
        return pimExportParserComposite;
    }

    @Required
    public void setPimExportParserComposite(PimExportParserComposite pimExportParserComposite) {
        this.pimExportParserComposite = pimExportParserComposite;
    }

    public I18NService getI18NService() {
        return i18NService;
    }

    public void setI18NService(I18NService i18NService) {
        this.i18NService = i18NService;
    }
}
