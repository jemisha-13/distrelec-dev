package com.namics.hybris.webservice.listener;

import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.TenantListener;

import org.apache.log4j.Logger;

import com.namics.hybris.webservice.service.HybrisSoapUiService;

/**
 * Listener listening the hybris startup and shutdown and loading/unloading the soupUI Mock Server.
 * 
 * The listener is registred in the class <code>com.namics.hybris.webservice.jalo.NamwebserviceManager</code>
 * 
 * @author jweiss, Namics AG
 * 
 */
public class SoapUiTenantListener implements TenantListener {

    private static final Logger LOG = Logger.getLogger(SoapUiTenantListener.class.getName());

    protected HybrisSoapUiService hybrisSoapUiService;
    protected boolean loadOnStartup;
    protected String tenantId = "master";

    @Override
    public void afterSetActivateSession(final Tenant tenant) {
        // do nothing
    }

    @Override
    public void beforeUnsetActivateSession(final Tenant tenant) {
        // do nothing
    }

    @Override
    public void afterTenantStartUp(final Tenant tenant) {
        LOG.info("Tenant '" + tenant.getTenantID() + "' started up.");
        if (isLoadOnStartup() && tenantId.equals(tenant.getTenantID())) {
            LOG.info("SoapUI starting after start up of tenant '" + tenant.getTenantID() + "'...");
            hybrisSoapUiService.startSoapUiServer();
            LOG.info("SoapUI started.");
        }

    }

    @Override
    public void beforeTenantShutDown(final Tenant tenant) {
        LOG.info("Shut down SoapUI before shuting down tenant '" + tenant.getTenantID() + "'...");
        hybrisSoapUiService.stopSoapUiServer();
        LOG.info("SoapUI shuted down.");
    }

    public void setHybrisSoapUiService(final HybrisSoapUiService hybrisSoapUiService) {
        this.hybrisSoapUiService = hybrisSoapUiService;
    }

    public boolean isLoadOnStartup() {
        return loadOnStartup;
    }

    public void setLoadOnStartup(final boolean loadOnStartup) {
        this.loadOnStartup = loadOnStartup;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(final String tenantId) {
        this.tenantId = tenantId;
    }

}
