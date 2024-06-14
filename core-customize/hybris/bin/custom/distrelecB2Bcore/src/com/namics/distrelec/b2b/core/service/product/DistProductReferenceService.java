/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product;

import java.util.Collection;
import java.util.List;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.catalog.references.ProductReferenceService;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * {@code DistProductReferenceService}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public interface DistProductReferenceService extends ProductReferenceService {

    /**
     * Returns all {@link ProductReferenceModel}s for the given (persisted) <b>source</b> {@link ProductModel} and for the specific
     * {@link ProductReferenceTypeEnum}.
     * <p/>
     * Example: Using {@link ProductReferenceTypeEnum#ACCESSORIES} returns the related (accessories) {@link ProductReferenceModel}s,
     * <b>NOT</b> the actual products (accessories), use {@link ProductReferenceModel#getTarget()} to get the actual accessory product.
     * 
     * @param source
     *            the source {@link ProductModel}
     * @param referenceType
     *            the type of the reference - {@link ProductReferenceTypeEnum}. If this argument is
     *            <code>null<code>, the productReferences of all types will be returned.
     * @param activeOnly
     *            - if <code>true<code>, only active product references will be returned.
     * @param offset
     *            the position from which the references will be considered
     * @param size
     *            the maximum number of product references
     * @throws IllegalArgumentException
     *             if <code>source<code> product is null.
     * @return a collection of {@link ProductReferenceModel}s or an empty list if there are no source references for the given product
     */
    public Collection<ProductReferenceModel> getProductReferencesForSourceProduct(final ProductModel source, final ProductReferenceTypeEnum referenceType,
            final boolean activeOnly, final int offset, final int size);

    /**
     * Returns all {@link ProductReferenceModel}s for the given (persisted) <b>source</b> {@link ProductModel} and for the specific
     * {@link ProductReferenceTypeEnum}.
     * <p/>
     * Example: Using {@link ProductReferenceTypeEnum#ACCESSORIES} returns the related (accessories) {@link ProductReferenceModel}s,
     * <b>NOT</b> the actual products (accessories), use {@link ProductReferenceModel#getTarget()} to get the actual accessory product.
     * 
     * @param source
     *            the source {@link ProductModel}
     * @param referenceTypes
     *            a list of reference types - {@link ProductReferenceTypeEnum}. If this argument is
     *            <code>null<code>, the productReferences of all types will be returned.
     * @param activeOnly
     *            - if <code>true<code>, only active product references will be returned.
     * @param offset
     *            the position from which the references will be considered
     * @param size
     *            the maximum number of product references
     * @throws IllegalArgumentException
     *             if <code>source<code> product is null.
     * @return a collection of {@link ProductReferenceModel}s or an empty list if there are no source references for the given product
     * @see #getProductReferencesForSourceProducts(List, List, boolean, int, int)
     */
    public Collection<ProductReferenceModel> getProductReferencesForSourceProduct(final ProductModel source,
            final List<ProductReferenceTypeEnum> referenceTypes, final boolean activeOnly, final int offset, final int size);

    /**
     * Returns all {@link ProductReferenceModel}s for the given (persisted) <b>source</b> {@link ProductModel} and for the specific
     * {@link ProductReferenceTypeEnum}.
     * <p/>
     * Example: Using {@link ProductReferenceTypeEnum#ACCESSORIES} returns the related (accessories) {@link ProductReferenceModel}s,
     * <b>NOT</b> the actual products (accessories), use {@link ProductReferenceModel#getTarget()} to get the actual accessory product.
     *
     * @param sources
     *            a list of {@link ProductModel}
     * @param referenceTypes
     *            a list of reference types - {@link ProductReferenceTypeEnum}. If this argument is
     *            <code>null<code>, the productReferences of all types will be returned.
     * @param activeOnly
     *            - if <code>true<code>, only active product references will be returned.
     * @throws IllegalArgumentException
     *             if <code>source<code> product is null.
     * @return a collection of {@link ProductReferenceModel}s or an empty list if there are no source references for the given product
     */
    public Collection<ProductReferenceModel> getProductReferencesForSourceProducts(final List<ProductModel> sources,
                                                                                   final List<ProductReferenceTypeEnum> referenceTypes, final boolean activeOnly);

    /**
     * Returns all {@link ProductReferenceModel}s for the given (persisted) <b>source</b> {@link ProductModel} and for the specific
     * {@link ProductReferenceTypeEnum}.
     * <p/>
     * Example: Using {@link ProductReferenceTypeEnum#ACCESSORIES} returns the related (accessories) {@link ProductReferenceModel}s,
     * <b>NOT</b> the actual products (accessories), use {@link ProductReferenceModel#getTarget()} to get the actual accessory product.
     * 
     * @param sources
     *            a list of {@link ProductModel}
     * @param referenceTypes
     *            a list of reference types - {@link ProductReferenceTypeEnum}. If this argument is
     *            <code>null<code>, the productReferences of all types will be returned.
     * @param activeOnly
     *            - if <code>true<code>, only active product references will be returned.
     * @param offset
     *            the position from which the references will be considered
     * @param size
     *            the maximum number of product references
     * @throws IllegalArgumentException
     *             if <code>source<code> product is null.
     * @return a collection of {@link ProductReferenceModel}s or an empty list if there are no source references for the given product
     */
    public Collection<ProductReferenceModel> getProductReferencesForSourceProducts(final List<ProductModel> sources,
            final List<ProductReferenceTypeEnum> referenceTypes, final boolean activeOnly, final int offset, final int size);

    /**
     * Checks whether there exist at least one {@link ProductReferenceModel} for the given {@code source} product with the given type (
     * {@code ProductReferenceTypeEnum}).
     * 
     * @param source
     *            the source product
     * @param referenceType
     *            the reference type.
     * @param activeOnly
     *            a boolean flag telling whether we should consider only the active references or not.
     * @return {@code true} if at least one reference exists for the given product within the specified type, {@code false} otherwise.
     * @see #hasAnyReferences(ProductModel, List, boolean)
     */
    public boolean hasReferences(final ProductModel source, final ProductReferenceTypeEnum referenceType, final boolean activeOnly);

    /**
     * Checks whether there exist at least one {@link ProductReferenceModel} for the given {@code source} product with the given type (
     * {@code ProductReferenceTypeEnum})s.
     * 
     * @param source
     *            the source product
     * @param referenceTypes
     *            the reference types.
     * @param activeOnly
     *            a boolean flag telling whether we should consider only the active references or not.
     * @return {@code true} if at least one reference exists for the given product within the specified types, {@code false} otherwise.
     */
    public boolean hasAnyReferences(final ProductModel source, final List<ProductReferenceTypeEnum> referenceTypes, final boolean activeOnly);

}
