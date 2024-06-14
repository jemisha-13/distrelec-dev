package com.namics.hybris.webservice.service.impl;

import org.apache.log4j.Logger;

import com.namics.hybris.webservice.service.HybrisSoapUiService;
import com.namics.hybris.webservice.soapui.SoapUiProcessLoader;

/**
 * Default implementation of <code>HybrisSoapUiService</code>.
 * 
 * @author jweiss, Namics AG
 * 
 */
public class DefaultHybrisSoapUiService implements HybrisSoapUiService {

    private static final Logger LOG = Logger.getLogger(DefaultHybrisSoapUiService.class.getName());

    protected String soapUiBaseLocation;
    protected String soapUiProjectLocation;
    protected String soapUiListeningPort;
    protected boolean printJvmSummary;
    protected boolean printClasspathSummary;
    protected boolean printStandardOutputToSysout;
    protected int waitForSeconds;

    /**
     * The modifier is not static, so spring has to grant a singleton access to the server!
     */
    protected SoapUiProcessLoader soapUiServer;

    /**
     * Creates a SoapUiProcessLoader-Instance and initialize this instance with the parameters configured in hybris config properties files.
     */
    protected void createSoapUiProcessLoader() {
        soapUiServer = new SoapUiProcessLoader(soapUiProjectLocation, soapUiBaseLocation, getSoapUiListeningPort());
        soapUiServer.setPrintJvmSummary(printJvmSummary);
        soapUiServer.setPrintClasspathSummary(printClasspathSummary);
        soapUiServer.setPrintStandardOutputToSysout(printStandardOutputToSysout);
        soapUiServer.setWaitTimoutMilliseconds(waitForSeconds * 1000);

    }

    @Override
    public synchronized void restartSoapUiServer() {
        try {
            if (soapUiServer != null) {
                LOG.debug("SoapUi process restarting...");
                soapUiServer.restartSoapUiServer();
                LOG.debug("SoapUi process restarted.");
            } else {
                startSoapUiServer();
            }
        } catch (final Exception e) {
            LOG.error("Exception restarting SoapUI integrated mock server.", e);
        }
    }

    @Override
    public synchronized void stopSoapUiServer() {
        try {
            if (soapUiServer != null) {
                LOG.debug("SoapUi process stopping...");
                soapUiServer.stopSoapUiServer();
                LOG.debug("SoapUi process stopped.");
            }
        } catch (final Exception e) {
            LOG.error("Exception stopping SoapUI integrated mock server.", e);
        }
    }

    @Override
    public synchronized void startSoapUiServer() {
        try {
            if (soapUiServer != null) {
                LOG.debug("SoapUi process starting...");
                soapUiServer.startSoapUiServer();
            } else {
                LOG.debug("SoapUi process starting (Creating new SoapUiProcessLoader instance)...");
                createSoapUiProcessLoader();
                LOG.debug("SoapUi process starting (Start SoapUiProcessLoader instance)...");
                soapUiServer.startSoapUiServer();
                LOG.debug("SoapUi process started.");
            }
        } catch (final Exception e) {
            LOG.error("Exception starting SoapUI integrated mock server.", e);
        }
    }

    public String getSoapUiBaseLocation() {
        return soapUiBaseLocation;
    }

    public void setSoapUiBaseLocation(final String soapUiBaseLocation) {
        this.soapUiBaseLocation = soapUiBaseLocation;
    }

    public String getSoapUiProjectLocation() {
        return soapUiProjectLocation;
    }

    public void setSoapUiProjectLocation(final String soapUiProjectLocation) {
        this.soapUiProjectLocation = soapUiProjectLocation;
    }

    public boolean isPrintJvmSummary() {
        return printJvmSummary;
    }

    public void setPrintJvmSummary(final boolean printJvmSummary) {
        this.printJvmSummary = printJvmSummary;
    }

    public boolean isPrintClasspathSummary() {
        return printClasspathSummary;
    }

    public void setPrintClasspathSummary(final boolean printClasspathSummary) {
        this.printClasspathSummary = printClasspathSummary;
    }

    public boolean isPrintStandardOutputToSysout() {
        return printStandardOutputToSysout;
    }

    public void setPrintStandardOutputToSysout(final boolean printStandardOutputToSysout) {
        this.printStandardOutputToSysout = printStandardOutputToSysout;
    }

    public int getWaitForSeconds() {
        return waitForSeconds;
    }

    public void setWaitForSeconds(final int waitForSeconds) {
        this.waitForSeconds = waitForSeconds;
    }

    public String getSoapUiListeningPort() {
        return soapUiListeningPort;
    }

    public void setSoapUiListeningPort(final String soapUiListeningPort) {
        this.soapUiListeningPort = soapUiListeningPort;
    }

}
