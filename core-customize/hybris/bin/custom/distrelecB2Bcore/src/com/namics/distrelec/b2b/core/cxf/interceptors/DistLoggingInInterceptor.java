/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.cxf.interceptors;

import java.util.List;
import java.util.Map;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.message.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * {@code DistLoggingInInterceptor}
 * 
 * @author DAEBERSANIF, Distrelec Group AG
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.1
 */
public class DistLoggingInInterceptor extends LoggingInInterceptor {

    private static final Logger LOG = LogManager.getLogger(DistLoggingInInterceptor.class);

    @Autowired
    private ConfigurationService configurationService;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cxf.interceptor.LoggingInInterceptor#handleMessage(org.apache.cxf.message.Message)
     */
    @Override
    public void handleMessage(final Message message) throws Fault {

        // configuration context
        final Map<String, List<String>> configContext;
        try {
            // parse the configuration
            configContext = DistLoggingHelper.parseConfiguration(getConfigurationService().getConfiguration());
        } catch (final Exception ex) {
            // no logging even in this case
            LOG.error("Error during parsing configuration", ex);
            return;
        }

        // extract namespace and operation from webservice message
        final String namespaceUri = message.getExchange().getBindingOperationInfo().getName().getNamespaceURI();
        final String methodName = message.getExchange().getBindingOperationInfo().getName().getLocalPart();

        int responseCode = (int) message.get(Message.RESPONSE_CODE);
        boolean serverErrorResponseCode = responseCode == 500;

        // finally check the logging
        if (serverErrorResponseCode || DistLoggingHelper.isLoggingAllowed(configContext, namespaceUri, methodName)) {
            super.handleMessage(message);
        } else {
            LOG.debug("SOAP CALL NOT LOGGED: {} {}", namespaceUri, methodName);
        }
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
