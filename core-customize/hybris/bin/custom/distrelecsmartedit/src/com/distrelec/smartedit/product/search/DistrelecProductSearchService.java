package com.distrelec.smartedit.product.search;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.enums.SortDirection;
import de.hybris.platform.cms2.namedquery.NamedQuery;
import de.hybris.platform.cms2.namedquery.Sort;
import de.hybris.platform.cms2.namedquery.service.NamedQueryService;
import de.hybris.platform.cmsfacades.products.service.ProductSearchService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistrelecProductSearchService implements ProductSearchService {

    public static final String NAMED_QUERY_PRODUCT_SEARCH_BY_TEXT = "distrelecNamedQueryProductByText";

    private NamedQueryService namedQueryService;

    @Override
    public SearchResult<ProductModel> findProducts(final String text, final PageableData pageableData, final CatalogVersionModel catalogVersion) {
        final NamedQuery namedQuery = getNamedQueryForProductSearch(text, pageableData, catalogVersion);
        return getNamedQueryService().getSearchResult(namedQuery);
    }

    /**
     * Get the NamedQuery data bean for searching products.
     * @param text the free text search that will be used in product name, description and code
     * @param pageableData the pageable data
     * @param catalogVersion the catalog version where the category lives
     * @return the named query bean
     */
    protected NamedQuery getNamedQueryForProductSearch(final String text, final PageableData pageableData, final CatalogVersionModel catalogVersion) {
        ServicesUtil.validateParameterNotNull(pageableData, "PageableData object cannot be null.");
        ServicesUtil.validateParameterNotNull(catalogVersion, "CatalogVersion object cannot be null.");
        final String queryText = StringUtils.isEmpty(text) ? "" : text + "%";
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(ProductModel.CODE, queryText);
        parameters.put(ProductModel.CATALOGVERSION, catalogVersion);

        if (StringUtils.isEmpty(pageableData.getSort())) {
            pageableData.setSort(CategoryModel.NAME);
        }
        final List<Sort> sortList = Arrays.asList(new Sort().withParameter(pageableData.getSort()).withDirection(SortDirection.ASC));

        return new NamedQuery()
                .withQueryName(NAMED_QUERY_PRODUCT_SEARCH_BY_TEXT)
                .withCurrentPage(pageableData.getCurrentPage())
                .withPageSize(pageableData.getPageSize())
                .withParameters(parameters)
                .withSort(sortList);
    }

    protected NamedQueryService getNamedQueryService() {
        return namedQueryService;
    }

    @Required
    public void setNamedQueryService(final NamedQueryService namedQueryService) {
        this.namedQueryService = namedQueryService;
    }
}
