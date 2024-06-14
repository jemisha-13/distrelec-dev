package com.namics.hybris.webservice.service;

/**
 * Hybris Service to start and stop the configured soap-ui Mock server.
 * 
 * @author jweiss, Namics AG
 * 
 */
public interface HybrisSoapUiService {

    /**
     * Restarts the soapUI mock server (to load the new definitions)
     */
    public void restartSoapUiServer();

    /**
     * Stops the soapUI mock server
     */
    public void stopSoapUiServer();

    /**
     * Starts the soapUI mock server
     */
    public void startSoapUiServer();

}