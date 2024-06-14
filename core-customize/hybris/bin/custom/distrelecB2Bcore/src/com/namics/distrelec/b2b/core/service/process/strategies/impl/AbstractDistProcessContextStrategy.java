
package com.namics.distrelec.b2b.core.service.process.strategies.impl;

import java.util.List;

import de.hybris.platform.acceleratorservices.process.strategies.impl.AbstractProcessContextStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;


/**
 * Default strategy to impersonate site and initialize session context from the process model.
 */
public abstract class AbstractDistProcessContextStrategy extends AbstractProcessContextStrategy 
{
	@Override
	public CatalogVersionModel getContentCatalogVersion(final BusinessProcessModel businessProcess)
	{
		ServicesUtil.validateParameterNotNull(businessProcess, BUSINESS_PROCESS_MUST_NOT_BE_NULL_MSG);

		final BaseSiteModel baseSite = getCmsSite(businessProcess);

		if (baseSite instanceof CMSSiteModel)
		{
			final List<ContentCatalogModel> contentCatalogs = ((CMSSiteModel) baseSite).getContentCatalogs();
			if (!contentCatalogs.isEmpty())
			{
				final ContentCatalogModel contentCatalogFiltered  = contentCatalogs.stream().filter(catalog -> null!=catalog.getSuperCatalog()).findAny().orElse(contentCatalogs.get(0));
				  return getCatalogVersionService().getSessionCatalogVersionForCatalog(contentCatalogFiltered.getId()); // Shouldn't be more than one
			}
		}

		return null;
	}


}
