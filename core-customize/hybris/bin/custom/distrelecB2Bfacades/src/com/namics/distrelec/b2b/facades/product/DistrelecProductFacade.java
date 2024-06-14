/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult;
import com.namics.distrelec.b2b.facades.product.data.EnergyEfficencyData;
import com.namics.distrelec.b2b.facades.product.data.ProductAvailabilityData;
import com.namics.distrelec.b2b.facades.product.data.ProductEnhancementData;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * Distrelec-specific Product facade interface.
 *
 * @author pnueesch, Namics AG
 */
public interface DistrelecProductFacade extends ProductFacade {

    /**
     * Gets the product by code. The current session data (catalog versions, user) are used, so the valid product for the current session
     * parameters will be returned. Use {@link ProductFacade#getProductForOptions(ProductModel, Collection)} if you have the model already.
     *
     * @param code
     *            the code of the product to be found
     * @param options
     *            options set that determines amount of information that will be attached to the returned product. If empty or null default
     *            BASIC option is assumed
     * @return the {@link ProductData}
     * @throws IllegalArgumentException
     *             when given product code is null
     * @throws UnknownIdentifierException
     *             when product with the given code is not found
     */
    ProductData getProductForCodeWithOptions(final String code, final Collection<ProductOption> options)
                                                                                                         throws UnknownIdentifierException,
                                                                                                         IllegalArgumentException;

    List<ProductData> getProductListForCodesAndOptions(final List<String> codes, final Collection<ProductOption> options);

    List<ProductAvailabilityData> getAvailabilityForEntries(final List<AbstractOrderEntryModel> entries);

    List<ProductAvailabilityData> getAvailabilityForEntries(final List<AbstractOrderEntryModel> entries, final Boolean detailInfo);

    /**
     * Checks the availability of the products in the current site. This method is a shortcut for the call of the method
     * {@link #getAvailability(List, Boolean)} with {@code detailInfo = Boolean.FALSE}.
     *
     * @param productCodes
     * @return the availability information for the given products
     * @see #getAvailability(List, Boolean)
     */
    List<ProductAvailabilityData> getAvailability(final List<String> productCodes);

    /**
     * Checks the availability of the given products for the current country. If {@code detailInfo == Boolean.TRUE} then the availability
     * information will be more detailed, otherwise, it contains only the total stock level.
     *
     * @param productCodes
     * @param detailInfo
     * @return the availability information for the given products
     */
    List<ProductAvailabilityData> getAvailability(final List<String> productCodes, final Boolean detailInfo);

    Pair<Boolean, Date> getPickupAvailabilityDate(Map<String, Integer> productQuantityMap, String warehouseCode);

    /**
     * Checks the availability of the given products for all countries. This method is a shortcut for the call of the method
     * {@link #getAvailabilityAllCountries(List, Boolean)} with {@code detailInfo = Boolean.FALSE}.
     *
     * @param productCodes
     * @return the availability information for the given products in all countries.
     * @see #getAvailabilityAllCountries(List, Boolean)
     */
    List<ProductAvailabilityData> getAvailabilityAllCountries(final List<String> productCodes);

    /**
     * Checks the availability of the given products for all countries. If {@code detailInfo == Boolean.TRUE} then the availability
     * information will be more detailed, otherwise, it contains only the total stock level.
     *
     * @param productCodes
     * @return the availability information for the given products in all countries.
     */
    List<ProductAvailabilityData> getAvailabilityAllCountries(final List<String> productCodes, final Boolean detailInfo);

    /**
     * Gets the availability of the product based on the quantity requested (for example in the cart) and the quantity available in stock.
     *
     * @param productCodesQuantity
     *            The string separated by comma of string of product;requestedQuantity,...
     * @param detailInfo
     * @return the {@link List<ProductAvailabilityData>}
     */
    List<ProductAvailabilityData> getAvailability(final Map<String, Integer> productCodesQuantity, final Boolean detailInfo);

    List<ProductAvailabilityData> getAvailability(final Map<String, Integer> productCodesQuantity, final Boolean detailInfo, final boolean useCache);

    /**
     * Checks if for this product and the current session user a punchout filter exists.
     *
     * @param productCode
     *            the product code
     * @return true if a punchout exists, otherwise false
     */
    boolean isProductBuyable(final String productCode);

    boolean isProductBuyable(final String productCode, boolean isPunchedOut);

    /**
     * Checks whether the product given by its code is punched out or not.
     *
     * @param productCode
     *            the product code
     * @return {@code true} if the product is punched out, {@code false} otherwise.
     */
    boolean isProductPunchedOut(final String productCode);

    /**
     * Checks whether the product given by its code can be treated as EOL.
     *
     * @param productCode
     *            the product code
     * @return {@code true} if the product can be treated as EOL, {@code false} otherwise.
     */
    boolean treatAsEOL(final String productCode);

    /**
     * Checks if for this product and the current session user a punchout filter exists. If yes the result gets returned
     *
     * @param productCode
     *            the product code
     * @return the result of the punchout filter check
     */
    Collection<PunchoutFilterResult> getPunchOutFilters(final String productCode);

    /**
     * Checks if punchout filters exist for given productCode for given site channel.
     *
     * @param productCode
     *            the product code
     * @param channel
     *            site channel
     * @return
     */
    Collection<PunchoutFilterResult> getPunchOutFilters(final String productCode, SiteChannel channel);

    /**
     * Returns enhancement data for Products.
     * <p>
     * Primarily used on PLP for data which is not contained in Fusion index.
     *
     * @param productCodes list of product codes
     * @param limitSearchSizeTo maximum number of product codes in a list
     *
     * @return list of objects for each product with enhancement fields filled out
     */
    List<ProductEnhancementData> getEnhancementsForProducts(List<String> productCodes, int limitSearchSizeTo);

    /**
     * Checks if for the current session user a punchout filter exists. If yes the result gets returned
     *
     * @return the result of the punchout filter check
     */
    Collection<PunchoutFilterResult> getPunchOutFiltersForCustomer();

    /**
     * Checks if the product is end of life
     *
     * @param productCode
     *            the product code
     * @return true if the product is EOL, otherwise false
     */
    boolean isEndOfLife(final String productCode);

    /**
     * Gets the product by {@code typeName} or {@code code}. The current session data (catalog versions, user) are used, so the valid
     * product for the current session parameters will be returned.
     *
     * @param typeName
     *            the type name of the product to be found
     * @param options
     *            options set that determines amount of information that will be attached to the returned product. If empty or null default
     *            BASIC option is assumed
     * @return the {@link ProductData}
     * @throws IllegalArgumentException
     *             when given product code is null
     * @throws UnknownIdentifierException
     *             when product with the given code is not found
     */
    ProductData getProductForTypeOrCodeAndOptions(final String typeName, final Collection<ProductOption> options)
                                                                                                                  throws UnknownIdentifierException,
                                                                                                                  IllegalArgumentException;

    /**
     * Fetch the similar products for the specified product.
     *
     * @param productModel
     *            the product for which we look for similar products.
     * @param offset
     *            the position of the first similar product
     * @param size
     *            the max number of similar products
     * @return a list of {@code ProductData}
     */
    List<ProductData> getSimilarProducts(final ProductModel productModel, final int offset, final int size);

    /**
     * Fetch the similar products for the specified product.
     *
     * @param productCode
     *            the product code for which we look for similar products.
     * @param offset
     *            the position of the first similar product
     * @param size
     *            the max number of similar products
     * @return a list of {@code ProductData}
     * @see #getSimilarProducts(ProductModel, int, int)
     */
    List<ProductData> getSimilarProducts(final String productCode, final int offset, final int size);

    /**
     * If a product has a replacement product and if current date is in between ReplacementFromDate & ReplacementUntilDate Then product is
     * consider as replaced
     *
     * @param ProductCode
     * @return true if product is replaced other wise false
     */
    boolean isProductReplaced(final String ProductCode);

    /**
     * Gets the product data. The current session data (catalog versions, user) are used, so the valid product for the current session
     * parameters will be returned. Use {@link ProductFacade#getProductForCodeAndOptions(String, Collection)} if you only have the code.
     *
     * @param productCode
     *            the product code
     * @param options
     *            options set that determines amount of information that will be attached to the returned product. If empty or null default
     *            BASIC option is assumed
     * @return the {@link ProductData}
     * @see #getProductForOptions(ProductModel, Collection)
     */
    ProductData getProductForOptions(final String productCode, Collection<? extends ProductOption> options);

    /**
     * A product is consider dangerous if it belongs to some predefine transport group, based on that it returns true or false
     *
     * @param productCode
     * @return true if it is dangerous product
     */
    boolean isDangerousProduct(final String productCode);

    /**
     * @param productCode
     * @returns the salesStatus for the given product in the current sales org
     */
    String getProductSalesStatus(final String productCode);

    /**
     * @param productCode
     * @return DistSalesStatusModel
     */
    DistSalesStatusModel getProductSalesStatusModel(final String productCode);

    /**
     * @param product
     * @param options
     * @return ProductData
     * @throws UnknownIdentifierException
     * @throws IllegalArgumentException
     */
    ProductData getProductForCodeAndOptions(final ProductModel product, final Collection<ProductOption> options)
                                                                                                                 throws UnknownIdentifierException,
                                                                                                                 IllegalArgumentException;

    /**
     * Look for the sales statuses of the provided products, by their codes, in the current sales organization.
     *
     * @param entries
     *            the list of entries.
     * @return a {@link Map} contains the sales statuses of the provided products mapped by the product codes.
     */
    Map<String, String> getSalesStatusForEntries(final List<AbstractOrderEntryModel> entries);

    /**
     * @param productCode
     * @returns the energy efficency data object or null
     */
    EnergyEfficencyData getEnergyEfficencyData(final String productCode);

    /**
     * @param product
     * @returns a list of cmsSite
     */
    List<CMSSiteModel> getAvailableCMSSitesForProduct(final ProductModel product);

    /**
     * @param ProductModel
     *            source
     * @returns ProductData target
     */
    @SuppressWarnings("javadoc")
    ProductData getProductDataBasicPopulated(final ProductModel product);

    /**
     * @param productCode
     * @return boolean
     */
    boolean isProductExcluded(final String productCode);

    boolean isProductExcludedForSiteChannel(final String productCode, SiteChannel siteChannel);

    /**
     * @return boolean
     */
    boolean enablePunchoutFilterLogic();

    /**
     * @param productCode
     * @return String
     */
    String getRelevantSalesUnit(final String productCode);

    /**
     * @param productCode
     * @param referenceTypes
     * @param offset
     * @param size
     * @return List
     */
    List<ProductData> getProductsReferences(String productCode, List<ProductReferenceTypeEnum> referenceTypes, int offset, int size);

    /**
     * @param sources
     * @param referenceTypes
     * @param offset
     * @param size
     * @return List
     */
    List<ProductData> getProductsReferences(List<ProductModel> sources, List<ProductReferenceTypeEnum> referenceTypes, int offset, int size);

    /**
     * Fetch the Multiple Product Alternatives for Product from PIM
     *
     * @param productCode
     *            the product for which we look for alternatives.
     * @return a list of {@code ProductData}
     */
    List<ProductData> getMultipleProductAlternatives(final String productCode);

    /**
     * Fetch the Multiple Product Alternatives for Product from PIM
     *
     * @param productCode
     *            the product for which we look for alternatives.
     * @param offset
     *            the position of the first similar product
     * @param size
     *            the max number of similar products
     * @return a list of {@code ProductData}
     */
    List<ProductData> getMultipleProductAlternatives(final String productCode, final int offset, final int size);

    Long getMinimumOrderQty(String productCode);

    boolean isPhasedOutOrSuspended(String productCode);

    List<String> getProductCodesForBlockedSalesStatus();

    boolean isBlockedProduct(ProductModel product);

    boolean isBetterWorldProduct(String productCode);
}
