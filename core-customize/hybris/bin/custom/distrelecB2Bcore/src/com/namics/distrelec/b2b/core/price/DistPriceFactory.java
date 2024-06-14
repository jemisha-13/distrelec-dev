/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.price;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.namics.distrelec.b2b.core.jalo.DistErpPriceConditionType;
import com.namics.distrelec.b2b.core.jalo.DistPriceRow;
import com.namics.distrelec.b2b.core.jalo.DistSalesOrg;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.PriceFactory;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.Unit;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.util.DateRange;

/**
 * DistPriceFacory extends PriceFactory.
 * 
 * @author DAEHUSIR, Distrelec
 * 
 */
@SuppressWarnings("deprecation")
public interface DistPriceFactory extends PriceFactory {

    /**
     * Returns the default price factory.
     * 
     * @return the default price factory
     */
    PriceFactory getDefaultPriceFactory();

    /**
     * Returns the default price factory.
     * 
     * @param salesOrg
     *            the sales organization used to determine the ERP implementation
     * 
     * @return the default price factory
     */
    PriceFactory getDefaultPriceFactory(final DistSalesOrgModel salesOrg);

    /**
     * Creates a new DistPriceRow with the given parameter.
     * 
     * @param product
     *            the product
     * @param user
     *            the user
     * @param userPriceGroup
     *            the user price group
     * @param salesOrg
     *            the sales organisation
     * @param minQuantity
     *            the min quantity
     * @param currency
     *            the currency
     * @param unit
     *            the unit
     * @param unitFactor
     *            the unit factor
     * @param net
     *            true for net, false for gross
     * @param dateRange
     *            the date range
     * @param price
     *            the price
     * @param priceConditionIdErp
     *            the id of this price condition in the ERP is this price a special price
     * @param lastModifiedErpDate
     *            the last modified date of the ERP
     * @param sequenceId
     *            the sequence id
     * @return a new DistpriceRow
     * @throws JaloPriceFactoryException
     */
    DistPriceRow createDistPriceRow(final Product product, final User user, final EnumerationValue userPriceGroup, final DistSalesOrg salesOrg,
            final long minQuantity, final Currency currency, final Unit unit, final int unitFactor, final boolean net, final DateRange dateRange,
            final double price, final String priceConditionIdErp, final DistErpPriceConditionType erpPriceConditionType, final Date lastModifiedErpDate,
            final Long sequenceId) throws JaloPriceFactoryException;

    /**
     * 
     * @param ctx
     * @param product
     * @param date
     * @param net
     * @param onlinePrice
     * @return a list of {@code PriceInformation}
     * @throws JaloPriceFactoryException
     */
    public List<PriceInformation> getProductPriceInformations(final SessionContext ctx, final Product product, final Date date, final boolean net,
            final boolean onlinePrice) throws JaloPriceFactoryException;

    /**
     * 
     * @param ctx
     * @param product
     * @param date
     * @param net
     * @param onlinePrice
     * @return a list of {@code PriceInformation}
     * @throws JaloPriceFactoryException
     */
    public List<PriceInformation> getProductPriceInformations(final SessionContext ctx, final ProductModel product, final Date date, final boolean net,
            final boolean onlinePrice, final boolean forceOnline) throws JaloPriceFactoryException;

    /**
     * 
     * @param ctx
     * @param products
     * @param date
     * @param net
     * @param onlinePrice
     * @return a list of {@code PriceInformation}
     * @throws JaloPriceFactoryException
     */
    public Map<String, List<PriceInformation>> getProductPriceInformations(final SessionContext ctx, final List<ProductModel> products, final Date date,
            final boolean net, final boolean onlinePrice, final boolean forceOnline) throws JaloPriceFactoryException;

    /**
     * Returns a list of list prices for the given parameters.
     * 
     * @param ctx
     *            the session context
     * @param product
     *            the product
     * @param date
     *            the date
     * @param net
     *            net or gross
     * @return a list of list prices for the given parameters
     * @throws JaloPriceFactoryException
     */
    List<PriceInformation> getProductListPriceInformations(final SessionContext ctx, final Product product, final Date date, final boolean net)
            throws JaloPriceFactoryException;

    /**
     * Returns a list of list prices for the given parameters.
     * 
     * @param ctx
     *            the session context
     * @param product
     *            the product
     * @param date
     *            the date
     * @param net
     *            net or gross
     * @param onlinePrice
     *            a flag to force getting prices from the ERP directly. Used mainly for SAP case.
     * @return a list of list prices for the given parameters
     * @throws JaloPriceFactoryException
     */
    List<PriceInformation> getProductListPriceInformations(final SessionContext ctx, final Product product, final Date date, final boolean net,
            final boolean onlinePrice) throws JaloPriceFactoryException;

    /**
     * Returns a list of list prices for the given parameters.
     * 
     * @param ctx
     *            the session context
     * @param product
     *            the product
     * @param date
     *            the date
     * @param net
     *            net or gross
     * @param onlinePrice
     *            a flag to force getting prices from the ERP directly. Used mainly for SAP case.
     * @param userRequiresCalculation
     *            a flag indicating if the user requires to force the online prices.
     * @return a list of list prices for the given parameters
     * @throws JaloPriceFactoryException
     */
    List<PriceInformation> getProductListPriceInformations(final SessionContext ctx, final ProductModel product, final Date date, final boolean net,
            final boolean onlinePrice, final boolean userRequiresCalculation) throws JaloPriceFactoryException;

}
