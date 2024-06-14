/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.product.strategies;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.europe1.channel.strategies.impl.DefaultRetrieveChannelStrategy;
import de.hybris.platform.europe1.enums.PriceRowChannel;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * {@code DefaultDistRetrieveChannelStrategy}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.0
 */
public class DefaultDistRetrieveChannelStrategy extends DefaultRetrieveChannelStrategy {

    private static final Logger LOG = LogManager.getLogger(DefaultRetrieveChannelStrategy.class);
    protected static final String PRICE_ROW_CHANNEL = "pricerow-channel";
    private EnumerationService enumerationService;

    @Override
    public PriceRowChannel getChannel(final SessionContext ctx) {
        if (ctx != null && ctx.getAttribute(DETECTED_UI_EXPERIENCE_LEVEL) != null) {
            PriceRowChannel priceRowChannel = (PriceRowChannel) ctx.getAttribute(PRICE_ROW_CHANNEL);
            if (priceRowChannel == null) {
                EnumerationValue enumUIExpLevel = (EnumerationValue) ctx.getAttribute(DETECTED_UI_EXPERIENCE_LEVEL);
                priceRowChannel = this.getEnumValueForCode(enumUIExpLevel.getCode().toLowerCase());
                if (priceRowChannel != null) {
                    ctx.setAttribute(PRICE_ROW_CHANNEL, priceRowChannel);
                }
            }

            return priceRowChannel;
        }

        return null;
    }

    @Override
    public List<PriceRowChannel> getAllChannels() {
        return getEnumerationService().getEnumerationValues(PriceRowChannel._TYPECODE);
    }

    /**
     * @param channel
     * @return the {@code PriceRowChannel} having the given name
     */
    private PriceRowChannel getEnumValueForCode(final String channel) {
        try {
            return (PriceRowChannel) getEnumerationService().getEnumerationValue(PriceRowChannel._TYPECODE, channel);
        } catch (final UnknownIdentifierException arg2) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("This Enum is not setup in PriceRowChannel dynamic enum");
            }
        }

        return null;
    }

    public EnumerationService getEnumerationService() {
        return enumerationService;
    }

    @Override
    @Required
    public void setEnumerationService(final EnumerationService enumerationService) {
        this.enumerationService = enumerationService;
        super.setEnumerationService(enumerationService);
    }
}
