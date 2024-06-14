/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.namics.hybris.ffsearch.model.DistFactFinderExportChannelModel;
import com.namics.hybris.ffsearch.service.DistFactFinderChannelDao;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

/**
 * Default implementation of {@link DistFactFinderChannelDao}.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class DistFactFinderChannelDaoImpl extends DefaultGenericDao<DistFactFinderExportChannelModel> implements DistFactFinderChannelDao {

    private static final Logger LOG = LoggerFactory.getLogger(DistFactFinderChannelDaoImpl.class);

    public DistFactFinderChannelDaoImpl() {
        super(DistFactFinderExportChannelModel._TYPECODE);
    }

    @Override
    public List<DistFactFinderExportChannelModel> getAllChannels() {
        return find();
    }

    @Override
    public DistFactFinderExportChannelModel getChannel(final CMSSiteModel cmsSite, final LanguageModel language) {
        if (cmsSite == null) {
            LOG.error("The parameter salesOrg must not be null.");
            return null;
        }
        if (language == null) {
            LOG.error("The parameter language must not be null.");
            return null;
        }

        final Map paramsWhere = new HashMap();
        paramsWhere.put(DistFactFinderExportChannelModel.CMSSITE, cmsSite);
        paramsWhere.put(DistFactFinderExportChannelModel.LANGUAGE, language);

        final List<DistFactFinderExportChannelModel> channels = find(paramsWhere);

        if (CollectionUtils.isEmpty(channels)) {
            LOG.error("No factfinder channel with given CMS Site [" + ToStringBuilder.reflectionToString(cmsSite) + "] and language ["
                    + ToStringBuilder.reflectionToString(language) + "] was found");
            return null;
        }
        if (CollectionUtils.size(channels) > 1) {
            LOG.error("More than one factfinder channel with given CMS Site [" + ToStringBuilder.reflectionToString(cmsSite) + "] and language ["
                    + ToStringBuilder.reflectionToString(language) + "] was found");
        }
        return channels.get(0);
    }

}
