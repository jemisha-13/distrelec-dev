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
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Action for placing orders.
 */
public class PlaceOrderAction extends AbstractProceduralAction<ReplenishmentProcessModel>
{
	private BaseSiteService baseSiteService;
	private UserService userService;
	private CatalogVersionService catalogVersionService;


	@Override
	public void executeAction(final ReplenishmentProcessModel process) throws Exception
	{
		final BusinessProcessParameterModel clonedCartParameter = processParameterHelper.getProcessParameterByName(process, "cart");
		final CartModel clonedCart = (CartModel) clonedCartParameter.getValue();
		this.modelService.refresh(clonedCart);
		getCatalogVersionService().setSessionCatalogVersions(
				filterCatalogVersions(getCatalogVersionService().getSessionCatalogVersions()));
		userService.setCurrentUser(clonedCart.getUser());
		getBaseSiteService().setCurrentBaseSite(clonedCart.getSite(), false);
		final OrderModel orderModel = getCommerceCheckoutService().placeOrder(clonedCart);
		orderModel.setSchedulingCronJob(process.getCartToOrderCronJob());
		modelService.save(orderModel);
		processParameterHelper.setProcessParameter(process, "order", orderModel);
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

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}


	protected CommerceCheckoutService getCommerceCheckoutService()
	{
		throw new UnsupportedOperationException(
				"Please define in the spring configuration a <lookup-method> for getCommerceCheckoutService().");
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService siteService)
	{
		this.baseSiteService = siteService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}
