/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.daos.ProductDao;

/**
 * Distrelec specific extension of {@link ProductDao}.
 * 
 * @since Distrelec 1.0
 */
public interface DistProductDao extends ProductDao {

    /**
     * Checks if the product is punched out for the given parameters.
     * 
     * @param salesOrg
     *            the sales org
     * @param company
     *            the company the customer belongs to
     * @param customerType
     *            the customer type
     * @param country
     *            the country a punchout might be applied
     * @param product
     *            the product a punchout might be applied
     * @param validity
     *            a date range the punchout has to fulfill
     * @return if there is a punchout the product that is punched out gets returned
     */
    List<PunchoutFilterResult> findPunchOutFilters(final DistSalesOrgModel salesOrg, final B2BUnitModel company, final SiteChannel customerType,
                                                   final CountryModel country, final ProductModel product, final Date validity);

    /**
     * Checks if the product is punched out for the given parameters.
     * 
     * @param salesOrg
     *            the sales org
     * @param company
     *            the company the customer belongs to
     * @param customerType
     *            the customer type
     * @param countries
     *            the countries a punchout might be applied
     * @param product
     *            the product a punchout might be applied
     * @param validity
     *            a date range the punchout has to fulfill
     * @return if there is a punchout the product that is punched out gets returned
     */
    List<PunchoutFilterResult> findPunchOutFilters(final DistSalesOrgModel salesOrg, final B2BUnitModel company, final SiteChannel customerType,
                                                   final Collection<CountryModel> countries, final ProductModel product, final Date validity);

    /**
     * Checks if the product is punched out for the given parameters.
     * 
     * @param salesOrg
     *            the sales org
     * @param company
     *            the company the customer belongs to
     * @param customerType
     *            the customer type
     * @param country
     *            the country a punchout might be applied
     * @param products
     *            the products a punchout might be applied
     * @param validity
     *            a date range the punchout has to fulfill
     * @return if there is a punchout the product that is punched out gets returned
     */
    List<PunchoutFilterResult> findPunchOutFilters(final DistSalesOrgModel salesOrg, final B2BUnitModel company, final SiteChannel customerType,
                                                   final CountryModel country, final List<ProductModel> products, final Date validity);

    /**
     * Checks if the product is punched out for the given parameters.
     * 
     * @param salesOrg
     *            the sales org
     * @param company
     *            the company the customer belongs to
     * @param customerType
     *            the customer type
     * @param countries
     *            the countries a punchout might be applied
     * @param products
     *            the products a punchout might be applied
     * @param validity
     *            a date range the punchout has to fulfill
     * @return if there is a punchout the product that is punched out gets returned
     */
    List<PunchoutFilterResult> findPunchOutFilters(final DistSalesOrgModel salesOrg, final B2BUnitModel company, final SiteChannel customerType,
                                                   final Collection<CountryModel> countries, final List<ProductModel> products, final Date validity);

    /**
     * Checks if the product is listed in this sales org. If not, the product is not visible and not buyable in the current shop.
     * 
     * @param product
     *            the product to check
     * @param salesOrg
     *            the sales org to check
     * @return true if the product is listed, otherwise false
     */
    boolean productIsListedForSalesOrg(final ProductModel product, final DistSalesOrgModel salesOrg);

    /**
     * Returns the product for the given pimId.
     * 
     * @param pimId
     *            the PIM ID
     * @return the product for the given pimId. Returns null if the product can not be found.
     */
    List<ProductModel> getProductForPimId(String pimId);

    /**
     * Look for the product with its typeName or code
     * 
     * @param code
     *            the product type name or code
     * @return the {@code ProductModel} having the specified type name
     */
    ProductModel findProductByTypeOrCode(final String code);

    /**
     * Look for the products having the specified codes
     * 
     * @param productCodes
     *            the product codes
     * @return a list of {@code ProductModel}
     */
    List<ProductModel> findProductsByCodes(final List<String> productCodes);

    /**
     * Look for the products having the specified SAP codes
     * 
     * @param productCodes
     *            the product codes
     * @return a list of {@code ProductModel}
     */
    List<ProductModel> findProductsBySapCodes(final List<String> productCodes);

    /**
     * Returns the product for the given Catalog+ supplier AID
     * 
     * @param catalogVersion
     *            the catalogversion the product is searched in
     * @param catPlusSupplierAID
     *            the Catalog+ supplier AID
     * @return the product for the given Catalog+ supplier AID
     */
    ProductModel findCatalogPlusProduct(final CatalogVersionModel catalogVersion, final String catPlusSupplierAID);

    /**
     * @param product
     * @param salesOrg
     * @returns the salesStatus for the given product in the given salesorg
     */
    String getProductSalesStatus(ProductModel product, DistSalesOrgModel salesOrg);

    /**
     * @param product
     * @param salesOrg
     * @returns the salesStatus for the given product in the given salesorg
     */
    DistSalesStatusModel getProductSalesStatusModel(ProductModel product, DistSalesOrgModel salesOrg);

    /**
     * Retrieve the Product Sales Org.
     * 
     * @param product
     * @param salesOrg
     * @return the {@link DistSalesOrgProductModel} for the given {@link ProductModel} and {@link DistSalesOrgModel}
     */
    DistSalesOrgProductModel getDistSalesOrgProductModel(final ProductModel product, final DistSalesOrgModel salesOrg);

    /**
     * Returns true if there is a sales status persisted for a product and a sales org, but which is not in the required sales status.
     *
     * @param productCode
     *            the product code
     * @param salesOrgCode
     *            the sales organisation code
     * @param salesStatus
     *            the sales status which record must not match
     * @return if there is a sales status
     */
    boolean containsSalesStatusForProductWhichIsNotInStatus(String productCode, String salesOrgCode, String salesStatus);

    /**
     * Look for the sales statuses of the provided products, by their codes, in the provided sales organization.
     * 
     * @param productCodes
     *            the list of product codes.
     * @param salesOrg
     *            the target sales organization.
     * @return a {@link Map} contains the sales statuses of the provided products mapped by the product codes.
     */
    Map<String, String> getSalesStatusForProducts(final List<String> productCodes, final DistSalesOrgModel salesOrg);

    /**
     * @param product
     *            *
     * @returns a list of cmsSite
     */
    List<CMSSiteModel> getCMSSitesByProduct(ProductModel product);

    List<ProductModel> findProductByMPN(final String productCodes);

    List<String> findExistingProductCodes(final List<String> codes) throws SQLException;

    List<ProductModel> findEOLProductsForRemoval(Date expirationDateWithAlternatives, Date expirationDateWithoutAlternatives, Integer resultSize);

}
