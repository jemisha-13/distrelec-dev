/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.core.actions.replenishment;

import de.hybris.platform.b2bacceleratorservices.model.process.ReplenishmentProcessModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Action for cart recalculation
 */
public class RecalculateCartAction extends AbstractProceduralAction<ReplenishmentProcessModel>
{
	private CalculationService calculationService;
	private CatalogVersionService catalogVersionService;

	@Override
	public void executeAction(final ReplenishmentProcessModel process) throws Exception
	{
		final BusinessProcessParameterModel clonedCartParameter = processParameterHelper.getProcessParameterByName(process, "cart");
		final CartModel clonedCart = (CartModel) clonedCartParameter.getValue();
		this.modelService.refresh(clonedCart);
		getCatalogVersionService().setSessionCatalogVersions(
				filterCatalogVersions(getCatalogVersionService().getSessionCatalogVersions()));
		getCalculationService().recalculate(clonedCart);
		this.modelService.save(clonedCart);
	}

	protected Collection<CatalogVersionModel> filterCatalogVersions(final Collection<CatalogVersionModel> sessionCatalogVersions)
	{
		final List<CatalogVersionModel> result = new ArrayList<CatalogVersionModel>(sessionCatalogVersions.size());

		for (final CatalogVersionModel catalogVersion : sessionCatalogVersions)
		{
			if (!(catalogVersion instanceof ClassificationSystemVersionModel)
					&& !(catalogVersion.getCatalog() instanceof ContentCatalogModel))
			{
				result.add(catalogVersion);
			}
		}

		return result;
	}

	protected CalculationService getCalculationService()
	{
		return calculationService;
	}

	@Required
	public void setCalculationService(final CalculationService calculationService)
	{
		this.calculationService = calculationService;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}
}
