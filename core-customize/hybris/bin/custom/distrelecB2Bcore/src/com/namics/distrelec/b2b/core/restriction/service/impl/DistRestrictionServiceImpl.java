package com.namics.distrelec.b2b.core.restriction.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.restriction.dao.DistRestrictionDao;
import com.namics.distrelec.b2b.core.restriction.service.DistRestrictionService;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.restrictions.CMSTimeRestrictionModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

public class DistRestrictionServiceImpl implements DistRestrictionService {

	private static final String BLANK_STRING = "";

	private static final int MINIMUM_SIZE = 1;

	private static final String INTERNATIONAL_CONTENT_CATALOG_ID = "distrelec_IntContentCatalog";

	private static final String INTERNATIONAL_SITE_ID = "distrelec_EX";

	@Autowired
	private CMSSiteService cmsSiteService;

	@Autowired
	private DistRestrictionDao distRestrictionDao;

	@Override
	public Set<String> getCDNSitesForClearingCache(Date startDate) {
		List<CMSTimeRestrictionModel> eligibleTimeRestrictions = distRestrictionDao.getEligibleRestrictions(startDate, new Date());
		Set<String> websiteSet = new HashSet<String>();
		for (CMSTimeRestrictionModel restriction : eligibleTimeRestrictions) {
			addWebsitesForComponent(restriction, websiteSet);
			addWebsitesForPages(restriction, websiteSet);
		}
		return websiteSet;
	}

	private String getWebsiteForCatalog(CatalogModel catalogModel) {
		if (catalogModel.getId().equalsIgnoreCase(INTERNATIONAL_CONTENT_CATALOG_ID)) {
			return INTERNATIONAL_SITE_ID;
		} else {
			List<CMSSiteModel> cmsSites = cmsSiteService.getSites().stream()
					.filter(cmsSite -> cmsSite.getCountryContentCatalog() != null
							&& cmsSite.getCountryContentCatalog().equals(catalogModel))
					.collect(Collectors.toList());
			return CollectionUtils.isNotEmpty(cmsSites) ? cmsSites.get(0).getUid() : BLANK_STRING;
		}
	}

	private void addWebsitesForComponent(CMSTimeRestrictionModel restriction, Set<String> websiteSet) {
		Iterator<AbstractCMSComponentModel> componentsIterator = restriction.getComponents().iterator();
		while (componentsIterator.hasNext()) {
			// Get the next element from the list
			websiteSet.add(getWebsiteForCatalog(componentsIterator.next().getCatalogVersion().getCatalog()));
		}
	}

	private void addWebsitesForPages(CMSTimeRestrictionModel restriction, Set<String> websiteSet) {
		Iterator<AbstractPageModel> pagesIterator = restriction.getPages().iterator();
		while (pagesIterator.hasNext()) {
			// Get the next element from the list
			websiteSet.add(getWebsiteForCatalog(pagesIterator.next().getCatalogVersion().getCatalog()));
		}
	}

}
