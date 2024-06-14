/*
 * Copyright 2000-2017 Distrelec AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.message.queue.receive;

import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Import;
import com.namics.distrelec.b2b.core.message.queue.handlers.ILMessageHandler;
import com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

/**
 * {@code DefaultMessageQueueDelegate}
 * 
 *
 * @author <a href="abhinay.jadhav@distrelec.com">Abhinay Jadhav</a>, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.1
 */
public class DefaultMessageQueueDelegate implements MessageQueueDelegate {

    private static final Logger LOG = LogManager.getLogger(DefaultMessageQueueDelegate.class);

    @Autowired
    private ILMessageHandler<ProductModel> productMessageHandler;

    @Autowired
    private ILMessageHandler<CategoryModel> categoryMessageHandler;

    @Autowired
    private ILMessageHandler<DistManufacturerModel> manufacturerMessageHandler;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private BaseSiteService baseSiteService;


    @Autowired
    private CatalogVersionService catalogVersionService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.message.queue.receive.MessageQueueDelegate#handleILMessage(com.namics.distrelec.b2b.core.message.queue.
     * message.InternalLinkMessage)
     */
    @Override
    public void handleILMessage(final InternalLinkMessage message) {
        checkSession(message);
        switch (message.getType()) {
            case PRODUCT:
                productMessageHandler.handle(message);
                break;
            case CATEGORY:
                categoryMessageHandler.handle(message);
                break;
            case MANUFACTURER:
                manufacturerMessageHandler.handle(message);
                break;
            default:
                break;
        }
    }

    /**
     * Check the current #JaloSession. If no session is present, then create and configure a new one.
     */
    private void checkSession(final InternalLinkMessage message) {
        if (!Registry.hasCurrentTenant()) {
            Registry.activateMasterTenant();
        }

        if (!JaloSession.hasCurrentSession()) {
            final JaloSession newSession = JaloSession.getCurrentSession();
            LOG.info("New session: sessionID [" + newSession.getSessionID() + "], httpSessionId [" + newSession.getHttpSessionId() + "], user ["
                    + newSession.getUser() + "]. Deactivate current session and create a new one.");
        }

        if (getCatalogVersionService().getSessionCatalogVersions().isEmpty()) {
            final Configuration configuration = getConfigurationService().getConfiguration();
            getCatalogVersionService().setSessionCatalogVersion(configuration.getString(Import.PRODUCT_CATALOG_ID),
                    configuration.getString(Import.PRODUCT_CATALOG_VERSION));
            final UserModel user = getUserService().getUserForUID(getConfigurationService().getConfiguration().getString("import.pim.user"));
            getUserService().setCurrentUser(user);
        }

        if (getCmsSiteService().getCurrentSite() == null) {
            getCmsSiteService().setCurrentSite((CMSSiteModel) getBaseSiteService().getBaseSiteForUID(message.getSite()));
            LOG.info("Changed currentSite to {}", message.getSite());
        }
    }

    // Getters & Setters

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public CatalogVersionService getCatalogVersionService() {
        return catalogVersionService;
    }

    public void setCatalogVersionService(final CatalogVersionService catalogVersionService) {
        this.catalogVersionService = catalogVersionService;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

}
