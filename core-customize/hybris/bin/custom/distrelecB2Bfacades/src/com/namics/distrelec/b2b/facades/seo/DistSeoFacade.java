package com.namics.distrelec.b2b.facades.seo;

import com.namics.distrelec.b2b.facades.marketing.data.DistHeroProductsData;
import com.namics.distrelec.b2b.facades.seo.data.MetaData;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;

public interface DistSeoFacade {

    public MetaData getMetaDataForProduct(final ProductModel product);

    public MetaData getMetaDataForCategory(final CategoryModel category);

    public DistHeroProductsData getActiveHeroProducts();

}
