/*
 * Copyright 2000-2017 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.inout.erp.comparator.DistPriceRowInfoComparator;
import com.namics.distrelec.b2b.core.jalo.DistErpPriceConditionType;
import com.namics.distrelec.b2b.core.jalo.DistPriceRow;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.service.product.DistSalesOrgProductService;

import de.hybris.platform.acceleratorservices.constants.GeneratedAcceleratorServicesConstants;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.constants.Europe1Constants;
import de.hybris.platform.europe1.constants.Europe1Tools;
import de.hybris.platform.europe1.enums.ProductTaxGroup;
import de.hybris.platform.europe1.jalo.Europe1PriceFactory;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.enumeration.EnumerationManager;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.PriceFactory;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.order.price.TaxInformation;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.Unit;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.type.JaloAbstractTypeException;
import de.hybris.platform.jalo.type.JaloGenericCreationException;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.DateRange;

/**
 * 
 * {@code AbstractPriceFactory}
 * 
 * @author daehusir, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 */
@SuppressWarnings("deprecation")
public abstract class AbstractPriceFactory {

    private static final Logger LOG = LogManager.getLogger(AbstractPriceFactory.class);
    
    public static final String PRICEPERXUOMDESCRIPTION = "pricePerXUOMDesc";

    private PriceFactory defaultPriceFactory;

    @Autowired
    private DistSalesOrgProductService distSalesOrgProductService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private BaseStoreService baseStoreService;

    protected List getPriceInformations(final SessionContext ctx, final Product product, final EnumerationValue productGroup, final User user,
            final EnumerationValue userGroup, final Currency currency, final boolean net, final Date date, final Collection taxValues)
            throws JaloPriceFactoryException {
        final Europe1PriceFactory europe1PriceFactory = (Europe1PriceFactory) getDefaultPriceFactory();
        final List<PriceRow> priceRowList = europe1PriceFactory.matchPriceRowsForInfo(ctx, product, productGroup, user, userGroup, currency, date, net);
        final Collection<DistPriceRow> priceRows = filterPriceRows(priceRowList, currency, net);
        final List priceInfos = new ArrayList(priceRows.size());
        Collection theTaxValues = taxValues;
        for (final DistPriceRow row : priceRows) {
            PriceInformation pInfo = Europe1Tools.createPriceInformation(row, currency);

            if (pInfo.getPriceValue().isNet() != net) {
                if (theTaxValues == null) {
                    theTaxValues = Europe1Tools.getTaxValues(getTaxInformations(ctx, product, date));
                }

                pInfo = new PriceInformation(pInfo.getQualifiers(), pInfo.getPriceValue().getOtherPrice(theTaxValues));
            }
            priceInfos.add(pInfo);
        }

        // Sort the prices by minqtd asc.
        Collections.sort(priceInfos, new Comparator<PriceInformation>() {
            @Override
            public int compare(final PriceInformation pi1, final PriceInformation pi2) {
                final Long p1Minqtd = (Long) pi1.getQualifierValue(PriceRow.MINQTD);
                final Long p2Minqtd = (Long) pi2.getQualifierValue(PriceRow.MINQTD);
                int x = p1Minqtd.compareTo(p2Minqtd);
                if (x == 0) {
                    // If the two prices have the same scale we check the Priority
                    final DistPriceRow p1 = (DistPriceRow) pi1.getQualifiers().get(PriceRow.PRICEROW);
                    final DistPriceRow p2 = (DistPriceRow) pi2.getQualifiers().get(PriceRow.PRICEROW);
                    x = p1.getErpPriceConditionType().getPriority().compareTo(p2.getErpPriceConditionType().getPriority());
                    if (x == 0) {
                        // If both prices have the same priority, then we check the validity dates
                        x = p2.getStartTime().compareTo(p1.getStartTime());
                    }
                }

                return x;
            }
        });
        return priceInfos;
    }

    protected List getListPriceInformations(final SessionContext ctx, final Product product, final EnumerationValue productGroup, final User user,
            final EnumerationValue userGroup, final Currency currency, final boolean net, final Date date, final Collection taxValues)
            throws JaloPriceFactoryException {
        final Europe1PriceFactory europe1PriceFactory = (Europe1PriceFactory) getDefaultPriceFactory();
        final Collection<PriceRow> priceRows = filterListPriceRows(
                europe1PriceFactory.matchPriceRowsForInfo(ctx, product, productGroup, user, userGroup, currency, date, net), currency, net);
        final List priceInfos = new ArrayList(priceRows.size());
        Collection theTaxValues = taxValues;
        for (final PriceRow row : priceRows) {
            PriceInformation pInfo = Europe1Tools.createPriceInformation(row, currency);

            if (pInfo.getPriceValue().isNet() != net) {
                if (theTaxValues == null) {
                    theTaxValues = Europe1Tools.getTaxValues(getTaxInformations(ctx, product, date));
                }

                pInfo = new PriceInformation(pInfo.getQualifiers(), pInfo.getPriceValue().getOtherPrice(theTaxValues));
            }
            priceInfos.add(pInfo);
        }

        // Sort the prices by minqtd asc.
        Collections.sort(priceInfos, new Comparator<PriceInformation>() {
            @Override
            public int compare(final PriceInformation pi1, final PriceInformation pi2) {
                final Long p1Minqtd = (Long) pi1.getQualifierValue(PriceRow.MINQTD);
                final Long p2Minqtd = (Long) pi2.getQualifierValue(PriceRow.MINQTD);
                return p1Minqtd.compareTo(p2Minqtd);
            }
        });
        return priceInfos;
    }

    protected List getTaxInformations(final SessionContext ctx, final Product product, final Date date) throws JaloPriceFactoryException {
        final EnumerationValue ptg = getSalesOrgSpecificPTG(product);
        if (ptg == null) {
            LOG.error("Can not determine sales org specific product tax class for product " + product
                    + ". Please check the product tax class definitions for this product!");
            return Collections.emptyList();
        } else {
            SessionContext localCtx = null;
            try {
                localCtx = JaloSession.getCurrentSession().createLocalSessionContext(ctx);
                localCtx.setAttribute(Europe1Constants.PARAMS.PTG, ptg);
                final List<TaxInformation> taxInfo = getDefaultPriceFactory().getProductTaxInformations(localCtx, product, date);
                return taxInfo;
            } catch (final JaloPriceFactoryException e) {
                LOG.error(e);
                return Collections.emptyList();
            } finally {
                if (localCtx != null) {
                    JaloSession.getCurrentSession().removeLocalSessionContext();
                }
            }
        }
    }

    protected List filterPriceRows(final List<PriceRow> priceRows, final Currency currency, final boolean net) {

        if (!priceRows.isEmpty()) {
            // Filter by Currency
            priceRows.removeIf(pr -> !pr.getCurrency().equals(currency));
        }
        if (priceRows.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        // Necessary for test class of hybris, because they do import normal price rows for there tests. So we will delegate to the normal
        // filter method.
        if (priceRows.get(0) instanceof DistPriceRow) {
            // sorting is essential to be able to filter in the right way later the price rows.
            Collections.sort(priceRows, new DistPriceRowInfoComparator(currency, net));

            Unit lastUnit = null;
            long lastMin = -1L;
            boolean lastSpecialPrice = false;
            int lastPriority = -1;
            String lastPriceConditionIdErp = null;

            final List<DistPriceRow> ret = new ArrayList(priceRows);

            // filtering rows
            for (final ListIterator<DistPriceRow> it = ret.listIterator(); it.hasNext();) {
                final DistPriceRow row = it.next();
                final long min = row.getMinQuantity();
                final Unit unit = row.getUnit();
                final String priceConditionIdErp = row.getPriceConditionIdErp();
                final int priority = row.getErpPriceConditionType().getPriority().intValue();

                if (lastUnit != null && lastPriceConditionIdErp != null && lastUnit.equals(unit)
                        && (lastMin == min || !lastPriceConditionIdErp.equals(priceConditionIdErp))
                        && ((lastSpecialPrice && row.isSpecialPrice()) || (!lastSpecialPrice && lastPriority <= priority))) {
                    it.remove();
                } else {
                    lastUnit = unit;
                    lastMin = min;
                    lastPriority = row.getErpPriceConditionType().getPriority().intValue();
                    lastSpecialPrice = row.isSpecialPrice();
                    lastPriceConditionIdErp = row.getPriceConditionIdErp();
                }
            }

            return ret;
        } else {
            Unit lastUnit = null;
            long lastMin = -1L;

            final List<PriceRow> ret = new ArrayList<PriceRow>(priceRows);
            for (final ListIterator<PriceRow> it = ret.listIterator(); it.hasNext();) {
                final PriceRow row = it.next();
                final long min = row.getMinQuantity();
                final Unit unit = row.getUnit();

                if (lastUnit != null && lastUnit.equals(unit) && lastMin == min) {
                    it.remove();
                } else {
                    lastUnit = unit;
                    lastMin = min;
                }
            }

            return ret;
        }
    }

    protected List filterListPriceRows(final List<PriceRow> priceRows, final Currency currency, final boolean net) {
        if (priceRows.isEmpty()) {
            return Collections.EMPTY_LIST;
        } else if (priceRows.size() == 1) {
            return priceRows;
        }

        // We should remove all price rows having a different currency.
        priceRows.removeIf(row -> !currency.equals(row.getCurrency()));

        // sorting is essential to be able to filter in the right way later the price rows.
        Collections.sort(priceRows, new DistPriceRowInfoComparator(currency, net));

        // Necessary for test class of hybris, because they do import normal price rows for there tests. So we will delegate to the normal
        // filter method.
        Unit lastUnit = null;
        long lastMin = -1L;
        if (priceRows.get(0) instanceof DistPriceRow) {
            int lastPriority = -1;
            String lastPriceConditionIdErp = null;

            final List<DistPriceRow> ret = new ArrayList(priceRows);
            for (final ListIterator<DistPriceRow> it = ret.listIterator(); it.hasNext();) {
                final DistPriceRow row = it.next();
                final long min = row.getMinQuantity();
                final Unit unit = row.getUnit();
                final String priceConditionIdErp = row.getPriceConditionIdErp();
                final int priority = row.getErpPriceConditionType().getPriority().intValue();

                if (row.isSpecialPrice()) {
                    it.remove();
                } else {
                    if (lastUnit != null && lastPriceConditionIdErp != null && lastUnit.equals(unit)
                            && (lastMin == min || !lastPriceConditionIdErp.equals(priceConditionIdErp)) && lastPriority <= priority) {
                        it.remove();
                    } else {
                        lastUnit = unit;
                        lastMin = min;
                        lastPriority = row.getErpPriceConditionType().getPriority().intValue();
                        lastPriceConditionIdErp = row.getPriceConditionIdErp();
                    }
                }
            }
            return ret;
        } else {
            final List ret = new ArrayList(priceRows);
            for (final ListIterator<PriceRow> it = ret.listIterator(); it.hasNext();) {
                final PriceRow row = it.next();
                final long min = row.getMinQuantity();
                final Unit unit = row.getUnit();

                if (lastUnit != null && lastUnit.equals(unit) && lastMin == min) {
                    it.remove();
                } else {
                    lastUnit = unit;
                    lastMin = min;
                }
            }
            return ret;
        }
    }

    protected EnumerationValue getSalesOrgSpecificPTG(final Product product) {
        final DistSalesOrgProductModel currentSalesOrgProduct = getDistSalesOrgProductService()
                .getCurrentSalesOrgProduct((ProductModel) getModelService().get(product));
        return (currentSalesOrgProduct != null)
                ? EnumerationManager.getInstance().getEnumerationValue(ProductTaxGroup._TYPECODE, currentSalesOrgProduct.getProductTaxGroup().getCode())
                : null;
    }

    protected boolean isNet(final User user) {
        final BaseStoreModel currentbaseStore = getBaseStoreService().getCurrentBaseStore();
        if (currentbaseStore != null) {
            return currentbaseStore.isNet();
        }
        return getDefaultPriceFactory().isNetUser(user);
    }

    protected boolean productHasSpecialPrice(final List<PriceInformation> prices) {
        return prices.stream().anyMatch(price -> ((DistPriceRow) price.getQualifierValue(PriceRow.PRICEROW)).isSpecialPrice());
    }

    //
    // protected boolean isSpecialPrice(final PriceRow priceRow) {
    // if (priceRow instanceof DistPriceRow && ((DistPriceRow) priceRow).getErpPriceConditionType() != null
    // && "ZN00".equals(((DistPriceRow) priceRow).getErpPriceConditionType().getCode())) {
    // return true;
    // }
    // return false;
    // }

    public DistPriceRow createPriceRow(final Product product, final User user, final EnumerationValue userPriceGroup, final long minQuantity,
            final Currency currency, final Unit unit, final int unitFactor, final boolean net, final DateRange dateRange, final double price,
            final String priceConditionIdErp, final DistErpPriceConditionType erpPriceConditionType, final Date lastModifiedErpDate, final Long sequenceId)
            throws JaloPriceFactoryException {
        try {
            return (DistPriceRow) ComposedType.newInstance(JaloSession.getCurrentSession(Registry.getCurrentTenant()).getSessionContext(), DistPriceRow.class,
                    new Object[] { PriceRow.PRODUCT, product, PriceRow.USER, user, PriceRow.UG, userPriceGroup, PriceRow.MINQTD, Long.valueOf(minQuantity),
                            PriceRow.CURRENCY, currency, PriceRow.UNIT, unit, PriceRow.UNITFACTOR, Integer.valueOf(unitFactor), PriceRow.NET,
                            Boolean.valueOf(net), PriceRow.DATERANGE, dateRange, PriceRow.PRICE, Double.valueOf(price), DistPriceRow.PRICECONDITIONIDERP,
                            priceConditionIdErp, DistPriceRow.ERPPRICECONDITIONTYPE, erpPriceConditionType, DistPriceRow.LASTMODIFIEDERP, lastModifiedErpDate,
                            GeneratedAcceleratorServicesConstants.Attributes.PriceRow.SEQUENCEID, sequenceId });
        } catch (final JaloGenericCreationException e) {
            Throwable cause = e.getCause();
            if (cause == null) {
                cause = e;
            }
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof JaloPriceFactoryException) {
                throw (JaloPriceFactoryException) cause;
            }
            throw new JaloSystemException(cause);
        } catch (final JaloAbstractTypeException e) {
            throw new JaloSystemException(e);
        } catch (final JaloItemNotFoundException e) {
            throw new JaloSystemException(e);
        }
    }

    /**
     * Returns the default price factory.
     * 
     * @return the default price factory
     */
    public PriceFactory getDefaultPriceFactory() {
        if (defaultPriceFactory == null) {
            setDefaultPriceFactory();
        }

        return defaultPriceFactory;
    }

    /**
     * Returns the default price factory.
     * 
     * @param salesOrg
     *            the sales organization (not used at the moment because the default price factory is not sales organization specific)
     * @return the default price factory
     */
    public PriceFactory getDefaultPriceFactory(final DistSalesOrgModel salesOrg) {
        return this.getDefaultPriceFactory();
    }

    /**
     * Sets the default price factory (Mostly Europe1).
     */
    private synchronized void setDefaultPriceFactory() {
        LOG.info("Using " + this.getClass().getSimpleName() + ".");
        this.defaultPriceFactory = Europe1PriceFactory.getInstance();
        LOG.info("Using " + this.defaultPriceFactory.getClass().getSimpleName() + " as default price factory.");
    }

    public DistSalesOrgProductService getDistSalesOrgProductService() {
        return distSalesOrgProductService;
    }

    public void setDistSalesOrgProductService(final DistSalesOrgProductService distSalesOrgProductService) {
        this.distSalesOrgProductService = distSalesOrgProductService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }
}
