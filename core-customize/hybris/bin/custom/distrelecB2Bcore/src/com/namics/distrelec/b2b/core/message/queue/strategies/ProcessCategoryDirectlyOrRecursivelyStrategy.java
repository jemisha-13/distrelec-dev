/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.message.queue.strategies;

import java.util.Collection;

import de.hybris.platform.category.model.CategoryModel;

public interface ProcessCategoryDirectlyOrRecursivelyStrategy {

    public boolean shouldProcessDirectly(CategoryModel category);

    public Collection<CategoryModel> getCategoriesToSearchInto(final CategoryModel category);

}
