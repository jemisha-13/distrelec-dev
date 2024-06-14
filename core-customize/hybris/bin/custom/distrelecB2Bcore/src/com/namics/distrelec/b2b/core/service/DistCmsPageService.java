/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service;

import com.namics.distrelec.b2b.core.model.DistContentPageMappingModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.cms.ProductFamilyPageModel;
import com.namics.distrelec.b2b.core.model.pages.DistManufacturerPageModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;

import java.util.Collection;
import java.util.Optional;

public interface DistCmsPageService extends CMSPageService {

    DistManufacturerPageModel getPageForManufacturer(DistManufacturerModel paramDistManufacturerModel) throws CMSItemNotFoundException;

    DistContentPageMappingModel getContentPageMappingForShortURL(String shortURL);

    Collection<DistContentPageMappingModel> getAllActiveContentPageMappings(String baseSiteId);

    Optional<ProductFamilyPageModel> findProductFamilyPage(String code, Collection<CatalogVersionModel> catalogVersions);
}
