/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.cxf.interceptors;

import java.util.List;
import java.util.Map;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.model.MessageInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * {@code DistLoggingOutInterceptor}
 * 
 * @author DAEBERSANIF, Distrelec Group AG
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.1
 */
public class DistLoggingOutInterceptor extends LoggingOutInterceptor {

    private static final Logger LOG = LogManager.getLogger(DistLoggingOutInterceptor.class);

    @Autowired
    private ConfigurationService configurationService;

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cxf.interceptor.LoggingOutInterceptor#handleMessage(org.apache.cxf.message.Message)
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

        final Message outMessage = message.getExchange().getOutMessage();
        final MessageInfo mi = (MessageInfo) outMessage.get("org.apache.cxf.service.model.MessageInfo");

        // extract namespace and operation from webservice message
        final String namespaceUri = mi.getName().getNamespaceURI();
        final String methodName = mi.getOperation().getInputName();

        // finally check the logging
        if (DistLoggingHelper.isLoggingAllowed(configContext, namespaceUri, methodName)) {
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
