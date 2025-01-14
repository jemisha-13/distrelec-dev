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
package com.namics.distrelec.b2b.facades.suggestion;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.commercefacades.product.data.ProductData;

import java.util.List;


/**
 * Facade to provide simple suggestions for a customer.
 */
public interface SimpleSuggestionFacade
{
	/**
	 * Returns a list of referenced products for a product purchased in a category identified by categoryCode.
	 * 
	 * @param categoryCode
	 * @param referenceType
	 *           referenceType, can be null
	 * @param excludePurchased
	 *           if true, only retrieve products that were not yet bought by the user
	 * @param limit
	 *           if not null: limit the amount of returned products to the given number
	 * @return a list with referenced products
	 */
	List<ProductData> getReferencesForPurchasedInCategory(String categoryCode, ProductReferenceTypeEnum referenceType,
			boolean excludePurchased, Integer limit);
}
