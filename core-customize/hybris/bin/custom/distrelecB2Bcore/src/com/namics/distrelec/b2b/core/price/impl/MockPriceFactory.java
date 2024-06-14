/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.price.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.namics.distrelec.b2b.core.inout.erp.impl.AbstractPriceFactory;
import com.namics.distrelec.b2b.core.jalo.DistErpPriceConditionType;
import com.namics.distrelec.b2b.core.jalo.DistPriceRow;
import com.namics.distrelec.b2b.core.jalo.DistSalesOrg;
import com.namics.distrelec.b2b.core.price.DistPriceFactory;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.jalo.Europe1PriceFactory;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.AbstractOrderEntry;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.order.price.ProductPriceInformations;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.Unit;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.util.DateRange;
import de.hybris.platform.util.PriceValue;

/**
 * Mock implementation of {@link DistPriceFactory} used for testing.
 * 
 * @author daehusir, Distrelec
 * 
 */
@SuppressWarnings("deprecation")
public class MockPriceFactory extends AbstractPriceFactory implements DistPriceFactory, Serializable {

    @Override
    public ProductPriceInformations getAllPriceInformations(final SessionContext ctx, final Product product, final Date date, final boolean net)
            throws JaloPriceFactoryException {
        return getDefaultPriceFactory().getAllPriceInformations(ctx, product, date, net);
    }

    @Override
    public List getProductPriceInformations(final SessionContext ctx, final Product product, final Date date, final boolean net)
            throws JaloPriceFactoryException {
        final Europe1PriceFactory europe1PriceFactory = (Europe1PriceFactory) getDefaultPriceFactory();
        return getPriceInformations(ctx, product, europe1PriceFactory.getPPG(ctx, product), ctx.getUser(), europe1PriceFactory.getUPG(ctx, ctx.getUser()),
                ctx.getCurrency(), net, date, null);
    }

    @Override
    public List<PriceInformation> getProductListPriceInformations(final SessionContext ctx, final Product product, final Date date, final boolean net)
            throws JaloPriceFactoryException {
        final Europe1PriceFactory europe1PriceFactory = (Europe1PriceFactory) getDefaultPriceFactory();
        return getListPriceInformations(ctx, product, europe1PriceFactory.getPPG(ctx, product), ctx.getUser(), europe1PriceFactory.getUPG(ctx, ctx.getUser()),
                ctx.getCurrency(), net, date, null);
    }

    @Override
    public List<PriceInformation> getProductListPriceInformations(final SessionContext ctx, final Product product, final Date date, final boolean net,
            final boolean onlinePrice) throws JaloPriceFactoryException {
        return getProductListPriceInformations(ctx, product, date, net);
    }

    @Override
    public List getProductTaxInformations(final SessionContext ctx, final Product product, final Date date) throws JaloPriceFactoryException {
        return getTaxInformations(ctx, product, date);
    }

    @Override
    public List getProductDiscountInformations(final SessionContext ctx, final Product product, final Date date, final boolean net)
            throws JaloPriceFactoryException {
        return getDefaultPriceFactory().getProductDiscountInformations(ctx, product, date, net);
    }

    @Override
    public boolean isNetUser(final User user) {
        return getDefaultPriceFactory().isNetUser(user);
    }

    @Override
    public Collection getTaxValues(final AbstractOrderEntry entry) throws JaloPriceFactoryException {
        return getDefaultPriceFactory().getTaxValues(entry);
    }

    @Override
    public PriceValue getBasePrice(final AbstractOrderEntry entry) throws JaloPriceFactoryException {
        return getDefaultPriceFactory().getBasePrice(entry);
    }

    @Override
    public List getDiscountValues(final AbstractOrderEntry entry) throws JaloPriceFactoryException {
        return getDefaultPriceFactory().getDiscountValues(entry);
    }

    @Override
    public List getDiscountValues(final AbstractOrder order) throws JaloPriceFactoryException {
        return getDefaultPriceFactory().getDiscountValues(order);
    }

    @Override
    public DistPriceRow createDistPriceRow(final Product product, final User user, final EnumerationValue userPriceGroup, final DistSalesOrg salesOrg,
            final long minQuantity, final Currency currency, final Unit unit, final int unitFactor, final boolean net, final DateRange dateRange,
            final double price, final String priceConditionIdErp, final DistErpPriceConditionType erpPriceConditionType, final Date lastModifiedErpDate,
            final Long sequenceId) throws JaloPriceFactoryException {
        return createPriceRow(product, user, userPriceGroup, minQuantity, currency, unit, unitFactor, net, dateRange, price, priceConditionIdErp,
                erpPriceConditionType, lastModifiedErpDate, sequenceId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.price.DistPriceFactory#getProductPriceInformations(de.hybris.platform.jalo.SessionContext,
     * de.hybris.platform.jalo.product.Product, java.util.Date, boolean, boolean)
     */
    @Override
    public List<PriceInformation> getProductPriceInformations(final SessionContext ctx, final Product product, final Date date, final boolean net,
            final boolean onlinePrice) throws JaloPriceFactoryException {
        return getDefaultPriceFactory().getProductPriceInformations(ctx, product, date, net);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.price.DistPriceFactory#getProductListPriceInformations(de.hybris.platform.jalo.SessionContext,
     * de.hybris.platform.jalo.product.Product, java.util.Date, boolean, boolean, boolean)
     */
    @Override
    public List<PriceInformation> getProductListPriceInformations(final SessionContext ctx, final ProductModel product, final Date date, final boolean net,
            final boolean onlinePrice, final boolean userRequiresCalculation) throws JaloPriceFactoryException {
        return getDefaultPriceFactory().getProductPriceInformations(ctx, (Product) getModelService().getSource(product), date, net);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.price.DistPriceFactory#getProductPriceInformations(de.hybris.platform.jalo.SessionContext,
     * de.hybris.platform.jalo.product.Product, java.util.Date, boolean, boolean, boolean)
     */
    @Override
    public List<PriceInformation> getProductPriceInformations(final SessionContext ctx, final ProductModel product, final Date date, final boolean net,
            final boolean onlinePrice, final boolean forceOnline) throws JaloPriceFactoryException {
        return getDefaultPriceFactory().getProductPriceInformations(ctx, (Product) getModelService().getSource(product), date, net);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.price.DistPriceFactory#getProductPriceInformations(de.hybris.platform.jalo.SessionContext,
     * java.util.List, java.util.Date, boolean, boolean, boolean)
     */
    @Override
    public Map<String, List<PriceInformation>> getProductPriceInformations(final SessionContext ctx, final List<ProductModel> products, final Date date,
            final boolean net, final boolean onlinePrice, final boolean forceOnline) throws JaloPriceFactoryException {
        return MapUtils.EMPTY_MAP;
    }
}
