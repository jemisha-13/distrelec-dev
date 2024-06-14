/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.service.product.data.PIMAlternateResult;
import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.ProductService;

/**
 * Distrelec specific extension of {@link ProductService}.
 * 
 * @since Distrelec 1.0
 */
public interface DistProductService extends ProductService {

    /**
     * Checks if the product is buyable. A product is buyable if it is listed for the current sales org and if the product has no punchout
     * filters.
     * 
     * @param product
     *            the product
     * @return true if the product is buyable, otherwise false
     */
    boolean isProductBuyable(final ProductModel product);

    boolean isProductBuyable(final ProductModel product, boolean isPunchedOut);

    /**
     * Checks if the product is EOL
     * 
     * @param product
     *            the product to check
     * @return {@code true} if the current date is after the product EOL date, {@code false} otherwise.
     */
    boolean isEndOfLife(final ProductModel product);

    /**
     * Checks if the given product is listed for the current sales org.
     * 
     * @param product
     *            the product
     * @return true if the product is listed for the current session sales org, otherwise false
     */
    boolean isProductListedForCurrentSalesOrg(final ProductModel product);

    /**
     * Checks if there are any punchout filter that get applied for the given product.
     * 
     * @param product
     *            the product
     * @return true if there are any punchout filters defined for the the given product, otherwise false
     */
    boolean isProductPunchedOut(final ProductModel product);

    /**
     *
     * Checks if there are any punchout filter that get applied for the give product for users of given channel.
     *
     * @param product
     * @param channel
     * @return
     */
    boolean isProductPunchedOutForSiteChannel(final ProductModel product, SiteChannel channel);

    /**
     * Returns all punchout filters for the given product.
     * 
     * @param product
     *            the product
     * @return a {@link Collection } of {@link PunchoutFilterResult} if there are any punchout filters for the given product, otherwise an
     *         empty list.
     */
    Collection<PunchoutFilterResult> getPunchOutFilters(final ProductModel product);

    /**
     * Returns all punchout filters for the given product on given site channel.
     *
     * @param product
     *            the product
     * @param channel
     *            site channel
     * @return a {@link Collection } of {@link PunchoutFilterResult} if there are any punchout filters for the given product, otherwise an
     *         empty list.
     */
    Collection<PunchoutFilterResult> getPunchOutFilters(final ProductModel product, SiteChannel channel);

    /**
     * Checks if the given cart contains a product that is not buyable.
     * 
     * @param cart
     *            the cart
     * @return a {@link Collection} of {@link PunchoutFilterResult} including the which and why the product is punched out for this cart and
     *         needs to be removed
     */
    Collection<PunchoutFilterResult> isProductPunchedOutForCart(final CartModel cart);

    /**
     * Returns the product for the given PIM ID.
     * 
     * @param pimId
     *            the PIM ID
     * @return the {@link ProductModel} for the given PIM ID.
     */
    ProductModel getProductForPimId(final String pimId);

    /**
     * Returns the product for the given Catalog+ Supplier ID.
     * 
     * @param catPlusSupplierAID
     *            the Catalog+ Supplier ID.
     * @return the {@link ProductModel} for the given Catalog+ Supplier ID.
     */
    ProductModel getCatalogPlusProductForCode(final UserModel customer, final String catPlusSupplierAID);

    /**
     * Look for the product with its typeName or Code
     * 
     * @param code
     *            the product type name or code
     * @return the {@code ProductModel} having the specified typeName or code
     */
    ProductModel getProductForTypeOrCode(final String code);

    /**
     * @param product
     * @return List
     */
    List<ProductModel> getSimilarProducts(final ProductModel product);

    /**
     * @param product
     * @param offset
     * @param size
     * @return List
     */
    List<ProductModel> getSimilarProducts(final ProductModel product, final int offset, final int size);

    /**
     * Return the list of {@code ProductModel}s having the specified codes.
     * 
     * @param productCodes
     *            the list of product codes
     * @return a list of {@code ProductModel}
     */
    List<ProductModel> getProductListForCodes(final List<String> productCodes);

    /**
     * Return the list of {@code ProductModel}s having the specified SAP codes.
     * 
     * @param productCodes
     *            the list of product codes
     * @return a list of {@code ProductModel}
     */
    List<ProductModel> getProductListForSapCodes(final List<String> productCodes);

    /**
     * Checks whether the product given by its code has download documents
     * 
     * @param code
     *            the product code.
     * @return {@code true} if the product has some download documents, {@code false} otherwise.
     */
    boolean hasDownloads(final String code);

    /**
     * Checks whether the product given by its code has similar products
     * 
     * @param code
     *            the product code.
     * @return {@code true} if the product has some similar products, {@code false} otherwise.
     */
    boolean hasSimilarProducts(final String code);

    /**
     * Checks whether the product given by its code has some accessories.
     * 
     * @param code
     *            the product code.
     * @return {@code true} if the product has some accessories, {@code false} otherwise.
     */
    boolean hasAccessories(final String code);

    /**
     * @param product
     * @returns the salesStatus for the given product in the current salesorg
     */
    String getProductSalesStatus(final ProductModel product);

    /**
     * 
     * @param product
     * @return
     */
    DistSalesStatusModel getProductSalesStatusModel(final ProductModel product);

    /**
     * Retrieve the Product Sales Org.
     * 
     * @param product
     * @param salesOrg
     * @return the {@link DistSalesOrgProductModel} for the given {@link ProductModel} and {@link DistSalesOrgModel}
     */
    DistSalesOrgProductModel getDistSalesOrgProductModel(final ProductModel product, final DistSalesOrgModel salesOrg);

    /**
     * Retrieve the Product Sales Org.
     * 
     * @param product
     * @return the {@link DistSalesOrgProductModel} for the given {@link ProductModel} and the current SalesOrg.
     * @see #getDistSalesOrgProductModel(ProductModel, DistSalesOrgModel)
     */
    DistSalesOrgProductModel getDistSalesOrgProductModel(final ProductModel product);

    /**
     * Retrieve the Product Sales Org.
     * 
     * @param productCode
     * @return the {@link DistSalesOrgProductModel} for the {@link ProductModel} given by its code and the current SalesOrg.
     * @see #getDistSalesOrgProductModel(ProductModel)
     */
    DistSalesOrgProductModel getDistSalesOrgProductModel(final String productCode);

    /**
     * Look for the sales statuses of the provided products, by their codes, in the current sales organization.
     * 
     * @param entries
     *            the list of entries.
     * @return a {@link Map} contains the sales statuses of the provided products mapped by the product codes.
     */
    public Map<String, String> getSalesStatusForEntries(final List<AbstractOrderEntryModel> entries);

    /**
     * Removes all products that are not listed for the current sales org in the session or are not visible or buyable in the shop.
     * 
     * @param products
     *            the products to filter
     */
    void removeNonBuyableProducts(final Collection<ProductModel> products);

    /**
     * @param product
     *            *
     * @returns a list of cmsSite
     */
    List<CMSSiteModel> getAvailableCMSSitesByProduct(final ProductModel product);

    /**
     * @param product
     * @param from
     * @param until
     */
    void updateDistSalesOrgProductNewLabel(final ProductModel product, final Date from, final Date until);

    /**
     * 
     */
    void initialUpdateDistSalesOrgProductNewLabel();

    /**
     * Return the list of available hero products
     * 
     * @return a list of {@code ProductData}
     */
    public List<ProductModel> getHeroProducts();

    /**
     * Returns a {@link List} of {@link ProductModel} that are referenced to the {@code sources} via any of {@code referenceTypes}
     * 
     * @param sources
     *            the {@link List} of {@link ProductModel} for which we want to know the references
     * @param referenceTypes
     *            a {@link List} of {@link ProductReferenceTypeEnum}
     * @param offset
     *            the start position to start
     * @param size
     *            the max number of references to return.
     * @return a {@link List} of {@link ProductModel}
     */
    List<ProductModel> getProductsReferences(List<ProductModel> sources, List<ProductReferenceTypeEnum> referenceTypes, int offset, int size);

    ProductModel findProductByCodeOrMPN(String productCodes, String mpn);

    List<ProductModel> findProductByMPN(String mpn);

    /**
     * Returns a {@link List} of {@link ProductModel} Alternatives that are referenced to the {@code sources} via any of {@code referenceTypes}
     *
     * @param sources
     *            the {@link List} of {@link ProductModel} for which we want to know the references
     * @param referenceTypes
     *            a {@link List} of {@link ProductReferenceTypeEnum}
     * @return a {@link List} of {@link ProductModel}
     */
    PIMAlternateResult getProductsReferencesForAlternative(List<ProductModel> sources, List<ProductReferenceTypeEnum> referenceTypes);

    /**
     * Returns a {@link List} of {@link ProductModel} Alternatives that are referenced to the {@code sources} via any of {@code referenceTypes}
     *
     * @param sources
     *            the {@link List} of {@link ProductModel} for which we want to know the references
     * @param referenceTypes
     *            a {@link List} of {@link ProductReferenceTypeEnum}
     * @param offset
     *            the start position to start
     * @param size
     *            the max number of references to return.
     * @return a {@link List} of {@link ProductModel}
     */
    PIMAlternateResult getProductsReferencesForAlternative(List<ProductModel> sources, List<ProductReferenceTypeEnum> referenceTypes, int offset, int size);

    /**
     * Returns a {@link List} of {@link ProductModel} Alternatives that are referenced to the {@code sources} via any of {@code referenceTypes}
     *
     * @param sources
     *            the {@link List} of {@link ProductModel} for which we want to know the references
     * @param referenceTypes
     *            a {@link List} of {@link ProductReferenceTypeEnum}
     * @param offset
     *            the start position to start
     * @param size
     *            the max number of references to return.
     * @param realStock
     *            use the fresh real ERP stock levels
     * @return a {@link List} of {@link ProductModel}
     */
    PIMAlternateResult getProductsReferencesForAlternative(List<ProductModel> sources, List<ProductReferenceTypeEnum> referenceTypes, int offset, int size,
                                                           boolean realStock);

    boolean isDuplicateMPNProduct(String mpn);

    boolean isProductBuyableOutOfStock(ProductModel product);

    boolean isProductBANS(ProductModel product);

    List<String> findExistingProductCodes(List<String> codes) throws SQLException;

    boolean isProductNotForSale(ProductModel product);

    boolean isSuspendedProduct(ProductModel product);

    boolean isSAPCatalogProduct(ProductModel product);

    List<ProductModel> getProductsBySiteIdAndSalesStatus(String site, String salesStatus, String itemCategoryGroup, int count, int page);
}
