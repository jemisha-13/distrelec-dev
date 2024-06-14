package com.namics.distrelec.b2b.core.service.classification.dao;

import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.classification.daos.ProductFeaturesDao;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;

public interface DistProductFeaturesDao extends ProductFeaturesDao {

    List<List<ItemModel>> findProductFeaturesByProductAndLanguage(ProductModel product, LanguageModel language,
        List<PK> excludes);

    /**
     * Gets the product feature's max value position.
     *
     * @param product    the product
     * @param assignment the assignment
     * @return the product feature max value position
     */
    List<Integer> getProductFeatureMinValuePosition(ProductModel product, ClassAttributeAssignmentModel assignment);

    List<ProductFeatureModel> findProductFeaturesByProduct(ProductModel product);
}
