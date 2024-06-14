/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.storesession.converters;

import com.namics.distrelec.b2b.facades.storesession.data.ChannelData;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.enumeration.EnumerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Converts a SiteChannel to a ChannelData.
 *
 * @author daehusir, Distrelec
 * @author rmeier, Namics AG
 *
 */
public class ChannelConverter extends AbstractPopulatingConverter<SiteChannel, ChannelData> {

    @Autowired
    private EnumerationService enumerationService;

    @Override
    protected ChannelData createTarget() {
        return new ChannelData();
    }

    @Override
    public void populate(final SiteChannel source, final ChannelData target) {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        target.setUid(source.getCode());
        target.setNativeName(getEnumerationService().getEnumerationName(source));
        target.setType(source.getCode());
        super.populate(source, target);
    }

    public EnumerationService getEnumerationService() {
        return enumerationService;
    }

    public void setEnumerationService(final EnumerationService enumerationService) {
        this.enumerationService = enumerationService;
    }

}
