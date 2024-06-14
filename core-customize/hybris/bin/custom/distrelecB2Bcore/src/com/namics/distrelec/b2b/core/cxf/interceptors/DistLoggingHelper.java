/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.cxf.interceptors;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.model.MessageInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DistLoggingHelper {
    private static final Logger LOG = LogManager.getLogger(DistLoggingHelper.class);

    public static final String WHITELIST_ALL = "_ALL_";

    protected static final String NAMESPACEURI_KEY = "NAMESPACEURI_KEY";
    protected static final String OPERATION_KEY = "OPERATION_KEY";
    protected static final String WHITE_LIST_KEY = "com.namics.distrelec.b2b.core.cxf.interceptors.DistLoggingInInterceptor.WHITE_LIST";

    private static final String[] EMPTY_STRING_ARRAY = { "" };

    protected static Map<String, List<String>> parseConfiguration(final Configuration configuration) throws Exception {
        final String config = configuration == null ? "" : configuration.getString(DistLoggingHelper.WHITE_LIST_KEY, "");

        if (StringUtils.isEmpty(config)) {
            // no logging if no configuration is provided
            LOG.warn("No logging configuration provided. Please configure the property: " + DistLoggingHelper.WHITE_LIST_KEY + " to activate soap logging.");
            return Collections.emptyMap();
        }

        return DistLoggingHelper.parseConfiguration(config);
    }

    /**
     * We can configure the logging with property {@code WHITE_LIST_KEY}.
     * 
     * The configuration must follow this pattern: <br/>
     * Service Namespace URI 1;Service Operation 1,Service Operation 2|Service Namespace URI 2;Service Operation 1|...;... <br/>
     * There is the possibility to activate the logging for ALL services or operation using {@code WHITELIST_ALL}
     * 
     * Example: http://ws69.webservice.adapters.factfinder.de;getProductCampaigns,logInformation|http://www.distrelec.com/hybris;_ALL_
     * 
     * @param config
     * @return a map, namespaceUri is the key, a list of methodNames is the value
     * @throws Exception
     */
    protected static Map<String, List<String>> parseConfiguration(final String config) throws Exception {
        try {
            final List<String> confEntries = Arrays.asList(config.split("\\|"));

            final Map<String, List<String>> result = new HashMap<>();

            for (final String confEntry : confEntries) {
                final List<String> entry = Arrays.asList(confEntry.split(";"));

                final String namespaceUri = CollectionUtils.isEmpty(entry) || entry.size() != 2 || StringUtils.isEmpty(entry.get(0)) ? "" : entry.get(0).trim();
                final List<String> operations = Arrays.asList(CollectionUtils.isEmpty(entry) || entry.size() != 2 || StringUtils.isEmpty(entry.get(1))
                        ? EMPTY_STRING_ARRAY : entry.get(1).trim().split((",")));

                result.put(namespaceUri, operations);
            }
            LOG.debug("parsedConfiguration: {}", result);
            return result;
        } catch (final Exception ex) {
            throw new Exception("Exception during parse configuration value" + config, ex);
        }
    }

    protected static String getNamespaceURIFromMessage(final Message message) {
        return getMessageInfo(message).getName().getNamespaceURI();
    }

    protected static String getOperationFromMessage(final Message message) {
        return getMessageInfo(message).getOperation().getInputName();
    }

    protected static MessageInfo getMessageInfo(final Message message) {
        final Message inMessage = message.getExchange().getInMessage();
        message.getExchange().getOutMessage();
        return (MessageInfo) inMessage.get("org.apache.cxf.service.model.MessageInfo");
    }

    public static boolean isLoggingAllowed(final Map<String, List<String>> configContext, final String namespaceUri, final String methodName) {
        if (configContext == null || namespaceUri == null || methodName == null) {
            return false;
        }

        return isNamespaceAllowed(configContext, namespaceUri) ? isNamespaceOperationAllowed(configContext, namespaceUri, methodName, true) : false;
    }

    private static boolean isNamespaceOperationAllowed(final Map<String, List<String>> configContext, final String namespaceUri, final String methodName,
            final boolean namespaceAllowed) {
        if (namespaceAllowed && MapUtils.isNotEmpty(configContext) && configContext.containsKey(WHITELIST_ALL)) {
            return true;
        }
        final List<String> whiteListOperations = configContext.get(namespaceUri);
        return namespaceAllowed && (whiteListOperations.contains(WHITELIST_ALL) || whiteListOperations.contains(methodName));
    }

    private static boolean isNamespaceAllowed(final Map<String, List<String>> configContext, final String namespaceUri) {
        return configContext.containsKey(WHITELIST_ALL) || (configContext.containsKey(namespaceUri));
    }

}
