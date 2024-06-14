package com.namics.distrelec.b2b.core.service.classification.dao.impl;

import com.namics.distrelec.b2b.core.service.classification.dao.DistProductFeaturesDao;
import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.classification.daos.impl.DefaultProductFeaturesDao;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultDistProductFeaturesDao extends DefaultProductFeaturesDao implements DistProductFeaturesDao {

    private static final Logger LOG = Logger.getLogger(DefaultDistProductFeaturesDao.class);

    private FlexibleSearchService flexibleSearchService;

    @Override
    public List<List<ItemModel>> findProductFeaturesByProductAndLanguage(final ProductModel product,
            final LanguageModel language, final List<PK> excludes) {
        FlexibleSearchQuery query = getQuery(product, language, excludes);
        final SearchResult<List<ItemModel>> searchResult = flexibleSearchService.search(query);
        return searchResult.getResult();
    }

    private FlexibleSearchQuery getQuery(final ProductModel product, final LanguageModel language,
            final List<PK> excludes) {
        final Map<String, Object> params = new HashMap<>();
        final StringBuilder builder = new StringBuilder();

        builder.append("SELECT {").append(ProductFeatureModel.PK)
                .append("},{" + ProductFeatureModel.CLASSIFICATIONATTRIBUTEASSIGNMENT + "} ");
        builder.append("FROM {").append(ProductFeatureModel._TYPECODE).append("} ");
        builder.append("WHERE {").append(ProductFeatureModel.PRODUCT).append("}=?product AND ");
        builder.append("{").append(ProductFeatureModel.LANGUAGE).append("} ");
        if (language != null) {
            builder.append(" = ?language ");
            params.put("language", language.getPk());
        } else {
            builder.append(" IS NULL ");
        }
        params.put("product", product.getPk());

        if (excludes != null && !excludes.isEmpty()) {
            builder.append(" AND {").append(ProductFeatureModel.PK).append("} ");
            if (excludes.size() == 1) {
                builder.append("<> ?excludes ");
                params.put("excludes", excludes.iterator().next());
            } else {
                builder.append(" NOT IN ( ?excludes )");
                params.put("excludes", excludes);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Sql query: [" + builder.toString() + "], Params: [" + params + "]");
        }

        final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(builder.toString());
        fQuery.addQueryParameters(params);
        fQuery.setResultClassList(Arrays.asList(ProductFeatureModel.class, ClassAttributeAssignmentModel.class));

        return fQuery;
    }

    @Override
    public List<Integer> getProductFeatureMinValuePosition(final ProductModel product,
            final ClassAttributeAssignmentModel assignment) {
        final StringBuilder builder = new StringBuilder();
        builder.append("SELECT min({").append(ProductFeatureModel.VALUEPOSITION).append("}) ");
        builder.append("FROM {").append(ProductFeatureModel._TYPECODE).append("} WHERE {");
        builder.append(ProductFeatureModel.PRODUCT).append("}=?product").append(" AND {");
        if (assignment == null) {
            builder.append(ProductFeatureModel.CLASSIFICATIONATTRIBUTEASSIGNMENT).append("} IS NULL");
        } else {
            builder.append(ProductFeatureModel.CLASSIFICATIONATTRIBUTEASSIGNMENT).append("}=?assignment");
        }

        final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(builder.toString());
        fQuery.setResultClassList(Collections.singletonList(Integer.class));
        fQuery.addQueryParameter("product", product);
        if (assignment != null) {
            fQuery.addQueryParameter("assignment", assignment);
        }

        final SearchResult<Integer> result = flexibleSearchService.search(fQuery);

        return result.getResult();
    }

    @Override
    public List<ProductFeatureModel> findProductFeaturesByProduct(ProductModel product) {
        String queryText = "SELECT {pk} FROM {ProductFeature}\n" +
                " WHERE {product}=?product";
        FlexibleSearchQuery query = new FlexibleSearchQuery(queryText);
        query.addQueryParameter("product", product);
        return flexibleSearchService.<ProductFeatureModel>search(query).getResult();
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    @Override
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        super.setFlexibleSearchService(flexibleSearchService);
        this.flexibleSearchService = flexibleSearchService;
    }
}
