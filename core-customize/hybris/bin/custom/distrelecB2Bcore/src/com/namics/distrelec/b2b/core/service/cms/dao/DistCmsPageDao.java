/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.cms.dao;

import com.namics.distrelec.b2b.core.model.DistContentPageMappingModel;
import com.namics.distrelec.b2b.core.model.cms.ProductFamilyPageModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSPageDao;

import java.util.Collection;
import java.util.Optional;

public interface DistCmsPageDao extends CMSPageDao {

    DistContentPageMappingModel findContentPageMapping(String shortURL, CMSSiteModel cmsSite, Boolean active);

    DistContentPageMappingModel findActiveContentPageMapping(String shortURL, CMSSiteModel cmsSite);

    Collection<DistContentPageMappingModel> findAllActiveContentPageMappings(CMSSiteModel cmsSite);

    Optional<ProductFamilyPageModel> findProductFamilySpecificPage(String productFamilyCode, Collection<CatalogVersionModel> catalogVersions);

    Optional<ProductFamilyPageModel> findDefaultProductFamilyPage(Collection<CatalogVersionModel> catalogVersions);

}
