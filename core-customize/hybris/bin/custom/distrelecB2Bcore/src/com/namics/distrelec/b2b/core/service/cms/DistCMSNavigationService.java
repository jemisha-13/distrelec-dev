/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.cms;

import java.util.ArrayList;
import java.util.List;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.servicelayer.services.CMSNavigationService;

/**
 * {@code DistCMSNavigationService}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.12
 */
public interface DistCMSNavigationService extends CMSNavigationService {

    public List<CMSNavigationNodeModel> getNavigationNodesForId(final String navNodeId, final CatalogVersionModel catalogVersion);

    /**
     * 
     * @param navNodeId
     * @param catalogVersion
     * @return an instance of {@code CMSNavigationNodeModel} if any, {@code null} otherwise.
     */
    public CMSNavigationNodeModel getNavigationNodeForId(final String navNodeId, final CatalogVersionModel catalogVersion);

    /**
     * Returns an {@link ArrayList} (so it is modifiable) of the upper hierarchy of {@link CMSNavigationNodeModel}, with the root at
     * position 0 and the input at last position
     * 
     * @param node
     *            the {@link CMSNavigationNodeModel} from which we want the hierarchy.
     * @return an {@link ArrayList} (so it is modifiable) of the upper hierarchy
     * @throws IllegalArgumentException
     *             - if the object is {@code null}
     */
    public ArrayList<CMSNavigationNodeModel> getNavigationNodeHierarchy(final CMSNavigationNodeModel node);

}
