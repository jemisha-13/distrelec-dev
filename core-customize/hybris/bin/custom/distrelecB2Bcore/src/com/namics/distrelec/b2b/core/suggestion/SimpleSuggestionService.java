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
package com.namics.distrelec.b2b.core.suggestion;

import java.util.List;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;

/**
 * Dao to retrieve product related data for {@link SimpleSuggestionService}.
 */
public interface SimpleSuggestionService {
    /**
     * Returns a list of referenced products for a product purchased in a category identified by categoryCode.
     * 
     * @param category
     *            to search in
     * @param user
     *            for whom orders will be searched
     * @param referenceType
     *            optional referenceType
     * @param excludePurchased
     *            if true, only retrieve products that have not been purchased by the user
     * @param limit
     *            if not null: limit the amount of returned products to the given number
     * @return a list with referenced products
     */
    List<ProductModel> getReferencesForPurchasedInCategory(CategoryModel category, UserModel user, ProductReferenceTypeEnum referenceType,
            boolean excludePurchased, Integer limit);

}
