package com.namics.distrelec.b2b.core.service.seo;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.marketing.DistHeroProductsModel;
import com.namics.distrelec.b2b.core.model.seo.DistMetaDataModel;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;

public interface DistSeoService {

    public DistMetaDataModel getMetaDataForProduct(final ProductModel product, final LanguageModel language, final CountryModel country);

    public DistMetaDataModel getMetaDataForCategory(final CategoryModel category, final LanguageModel language, final CountryModel country);

    public DistHeroProductsModel getActiveHeroProducts();
}
