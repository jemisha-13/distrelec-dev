/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.product.DistProductReferenceService;
import com.namics.distrelec.b2b.core.service.product.DistProductService;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.catalog.references.impl.DefaultProductReferenceService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * {@code DefaultDistProductReferenceService}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class DefaultDistProductReferenceService extends DefaultProductReferenceService implements DistProductReferenceService {

    private static final String HAS_REF_QUERY = "SELECT count({" + ProductReferenceModel.PK + "}) FROM {" + ProductReferenceModel._TYPECODE + "} WHERE {"
            + ProductReferenceModel.SOURCE + "}=?" + ProductReferenceModel.SOURCE + " AND {" + ProductReferenceModel.REFERENCETYPE + "} IN (?"
            + ProductReferenceModel.REFERENCETYPE + ")";

    private static final String REF_LIST_QUERY = "SELECT {" + ProductReferenceModel.PK + "} FROM {" + ProductReferenceModel._TYPECODE + "} WHERE {"
            + ProductReferenceModel.SOURCE + "} IN (?" + ProductReferenceModel.SOURCE + ") AND {" + ProductReferenceModel.REFERENCETYPE + "} IN (?"
            + ProductReferenceModel.REFERENCETYPE + ")";

    private static final String REF_LIST_ACTIVE_ONLY_QUERY = REF_LIST_QUERY + " AND {" + ProductReferenceModel.ACTIVE + "}=1";

    private static final List<Class<Long>> R_CLASS_LIST = Arrays.asList(Long.class);

    private DistProductService distProductService;
    @Autowired
    private FlexibleSearchService flexibleSearchService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.hybris.platform.catalog.references.impl.DefaultProductReferenceService#getProductReferencesForSourceProduct(de.hybris.platform
     * .core.model.product.ProductModel, de.hybris.platform.catalog.enums.ProductReferenceTypeEnum, boolean)
     */
    @Override
    public Collection<ProductReferenceModel> getProductReferencesForSourceProduct(final ProductModel source, final ProductReferenceTypeEnum referenceType,
            final boolean activeOnly) {

        if (referenceType == ProductReferenceTypeEnum.DIST_ACCESSORY) {
            return getProductReferencesForSourceProduct(source, referenceType, activeOnly, 0, 4);
        } else {
            return super.getProductReferencesForSourceProduct(source, referenceType, activeOnly);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.product.DistProductReferenceService#getProductReferencesForSourceProduct(de.hybris.platform
     * .core.model.product.ProductModel, de.hybris.platform.catalog.enums.ProductReferenceTypeEnum, boolean, int, int)
     */
    @Override
    public Collection<ProductReferenceModel> getProductReferencesForSourceProduct(final ProductModel source, ProductReferenceTypeEnum referenceType,
            boolean activeOnly, int offset, int size) {
        return selectBuyable(super.getProductReferencesForSourceProduct(source, referenceType, activeOnly), offset, size);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.product.DistProductReferenceService#hasReferences(de.hybris.platform.core.model.product.
     * ProductModel, de.hybris.platform.catalog.enums.ProductReferenceTypeEnum, boolean)
     */
    @Override
    public boolean hasReferences(final ProductModel source, final ProductReferenceTypeEnum referenceType, final boolean activeOnly) {
        return hasAnyReferences(source, Arrays.asList(referenceType), activeOnly);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.product.DistProductReferenceService#hasAnyReferences(de.hybris.platform.core.model.product.
     * ProductModel, java.util.List, boolean)
     */
    @Override
    public boolean hasAnyReferences(final ProductModel source, final List<ProductReferenceTypeEnum> referenceTypes, final boolean activeOnly) {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(HAS_REF_QUERY + (activeOnly ? " AND {" + ProductReferenceModel.ACTIVE + "}=1" : ""));
        query.addQueryParameter(ProductReferenceModel.SOURCE, source);
        query.addQueryParameter(ProductReferenceModel.REFERENCETYPE, referenceTypes);
        query.setResultClassList(R_CLASS_LIST);
        return getFlexibleSearchService().<Long> search(query).getResult().get(0) > 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.product.DistProductReferenceService#getProductReferencesForSourceProduct(de.hybris.platform
     * .core.model.product.ProductModel, java.util.List, boolean, int, int)
     */
    @Override
    public Collection<ProductReferenceModel> getProductReferencesForSourceProduct(final ProductModel source,
            final List<ProductReferenceTypeEnum> referenceTypes, final boolean activeOnly, final int offset, final int size) {
        return getProductReferencesForSourceProducts(Arrays.asList(source), referenceTypes, activeOnly, offset, size);
    }

    @Override
    public Collection<ProductReferenceModel> getProductReferencesForSourceProducts(List<ProductModel> sources, List<ProductReferenceTypeEnum> referenceTypes, boolean activeOnly) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(activeOnly ? REF_LIST_ACTIVE_ONLY_QUERY : REF_LIST_QUERY);
        searchQuery.addQueryParameter(ProductReferenceModel.SOURCE, sources);
        searchQuery.addQueryParameter(ProductReferenceModel.REFERENCETYPE, referenceTypes);
        // searchQuery.setCount(Math.min(size * 2, 20));

        final Collection<ProductReferenceModel> productReferences = getFlexibleSearchService().<ProductReferenceModel> search(searchQuery).getResult();
        return selectBuyable(productReferences);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.product.DistProductReferenceService#getProductReferencesForSourceProducts(java.util.List,
     * java.util.List, boolean, int, int)
     */
    @Override
    public Collection<ProductReferenceModel> getProductReferencesForSourceProducts(final List<ProductModel> sources,
            final List<ProductReferenceTypeEnum> referenceTypes, final boolean activeOnly, final int offset, final int size) {

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(activeOnly ? REF_LIST_ACTIVE_ONLY_QUERY : REF_LIST_QUERY);
        searchQuery.addQueryParameter(ProductReferenceModel.SOURCE, sources);
        searchQuery.addQueryParameter(ProductReferenceModel.REFERENCETYPE, referenceTypes);
        // searchQuery.setCount(Math.min(size * 2, 20));

        final Collection<ProductReferenceModel> productReferences = getFlexibleSearchService().<ProductReferenceModel> search(searchQuery).getResult();
        return selectBuyable(productReferences, offset, size);
    }

    /**
     * Select only the buyable products among the specified list.
     *
     * @param productReferences
     * @return a list of {@code ProductReferenceModel}
     */
    private Collection<ProductReferenceModel> selectBuyable(final Collection<ProductReferenceModel> productReferences) {
        return productReferences.stream()
                .filter(reference -> getDistProductService().isProductBuyable(reference.getTarget()))
                .collect(Collectors.toList());
    }

    /**
     * Select only the buyable products among the specified list.
     * 
     * @param productReferences
     * @param offset
     * @param size
     * @return a list of {@code ProductReferenceModel}
     */
    private Collection<ProductReferenceModel> selectBuyable(final Collection<ProductReferenceModel> productReferences, final int offset, final int size) {
        final int new_offset = offset < 0 ? 0 : offset;

        if (CollectionUtils.isEmpty(productReferences) || new_offset >= productReferences.size()) {
            return Collections.<ProductReferenceModel> emptyList();
        }

        final int max_pos = new_offset + size;
        return CollectionUtils.select(productReferences, new Predicate() {
            private int pos = 0;

            @Override
            public boolean evaluate(final Object paramObject) {
                // Consider only the buyable products
                if (getDistProductService().isProductBuyable(((ProductReferenceModel) paramObject).getTarget())) {
                    return pos >= new_offset & pos++ < max_pos;
                }
                return false;
            }
        });
    }

    public DistProductService getDistProductService() {
        return distProductService;
    }

    public void setDistProductService(final DistProductService distProductService) {
        this.distProductService = distProductService;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
