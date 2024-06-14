/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.cms.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.service.cms.DistCMSNavigationService;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSNavigationDao;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSNavigationService;

/**
 * {@code DefaultDistCMSNavigationService}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.12
 */
public class DefaultDistCMSNavigationService extends DefaultCMSNavigationService implements DistCMSNavigationService {

    private static final Logger LOG = LogManager.getLogger(DefaultDistCMSNavigationService.class);

    @Autowired
    private CMSNavigationDao cmsNavigationDao;

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.cms.DistCMSNavigationService#getNavigationNodeForId(java.lang.String,
     * de.hybris.platform.catalog.model.CatalogVersionModel)
     */
    @Override
    public CMSNavigationNodeModel getNavigationNodeForId(final String navNodeId, final CatalogVersionModel catalogVersion) {
        try {
            final List<CMSNavigationNodeModel> nodes = getNavigationNodesForId(navNodeId, catalogVersion);
            return nodes != null && !nodes.isEmpty() ? nodes.get(0) : null;
        } catch (final Exception exp) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.cms.DistCMSNavigationService#getNavigationNodesForId(java.lang.String,
     * de.hybris.platform.catalog.model.CatalogVersionModel)
     */
    @Override
    public List<CMSNavigationNodeModel> getNavigationNodesForId(final String navNodeId, final CatalogVersionModel catalogVersion) {
        return getCmsNavigationDao().findNavigationNodesById(navNodeId, Collections.singleton(catalogVersion));
    }

    @Override
    public ArrayList<CMSNavigationNodeModel> getNavigationNodeHierarchy(final CMSNavigationNodeModel node) {
        Assert.notNull(node);
        final ArrayList<CMSNavigationNodeModel> result;
        if (null == node.getParent()) {
            result = new ArrayList<>();
        } else {
            result = getNavigationNodeHierarchy(node.getParent());
        }
        result.add(node);
        if (LOG.isDebugEnabled()) {
            LOG.debug("hierarchy of node: {} is {}", node.getUid(), result.stream().map(n -> n.getUid()).collect(Collectors.joining(", ")));
        }
        return result;
    }

    public CMSNavigationDao getCmsNavigationDao() {
        return cmsNavigationDao;
    }

    @Override
    public void setCmsNavigationDao(final CMSNavigationDao cmsNavigationDao) {
        super.setCmsNavigationDao(cmsNavigationDao);
        this.cmsNavigationDao = cmsNavigationDao;
    }
}
