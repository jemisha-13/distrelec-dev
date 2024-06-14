/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.inout.erp.CustomerPriceService;
import com.namics.distrelec.b2b.core.inout.erp.OrderCalculationService;
import com.namics.distrelec.b2b.core.jalo.DistErpPriceConditionType;
import com.namics.distrelec.b2b.core.jalo.DistPriceRow;
import com.namics.distrelec.b2b.core.jalo.DistSalesOrg;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.price.DistPriceFactory;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.commercefacades.product.data.ProductPriceData;
import de.hybris.platform.commercefacades.product.data.VolumePriceData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.constants.Europe1Constants;
import de.hybris.platform.europe1.jalo.Europe1PriceFactory;
import de.hybris.platform.europe1.jalo.PDTRow;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.JaloSession;
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
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.DateRange;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.TaxValue;

/**
 * PriceFactory for Distrelec SAP behaviors.
 * 
 * @author daehusir, Distrelec
 * 
 */
@SuppressWarnings("deprecation")
public class SAPPriceFactory extends AbstractPriceFactory implements DistPriceFactory, Serializable {

    protected static final long DEFAULT_ORDER_QUANTITY_MINIMUM = 1L;

    private static final Logger LOG = LogManager.getLogger(SAPPriceFactory.class);

    // 0000100010
    public static final String DUMMY_CUSTOMER_ID_PROP = "sap.online.price.dummy.customer.id";    

    private CustomerPriceService customerPriceService;

    private DistSalesOrgService distSalesOrgService;

    private OrderCalculationService orderCalculationService;

    private B2BCustomerService b2BCustomerService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private UserService userService;


    @Autowired
    private ModelService modelService;

    @Override
    public ProductPriceInformations getAllPriceInformations(final SessionContext ctx, final Product product, final Date date, final boolean net)
            throws JaloPriceFactoryException {
        throw new UnsupportedOperationException("Method not supported");
    }

    @Override
    public List<PriceInformation> getProductPriceInformations(final SessionContext ctx, final Product product, final Date date, final boolean net)
            throws JaloPriceFactoryException {
        return getProductPriceInformations(ctx, product, date, net, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.price.DistPriceFactory#getProductPriceInformations(de.hybris.platform.jalo.SessionContext,
     * de.hybris.platform.jalo.product.Product, java.util.Date, boolean, boolean)
     */
    @Override
    public List<PriceInformation> getProductPriceInformations(final SessionContext ctx, final Product product, final Date date, final boolean net, final boolean onlinePrice)
            throws JaloPriceFactoryException {
        return getProductListPriceInformations(ctx, product, date, net, onlinePrice);
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
        return getProductListPriceInformations(ctx, product, date, net, onlinePrice, forceOnline);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.price.DistPriceFactory#getProductListPriceInformations(de.hybris.platform.jalo.SessionContext,
     * de.hybris.platform.jalo.product.Product, java.util.Date, boolean, boolean)
     */
    @Override
    public List<PriceInformation> getProductListPriceInformations(final SessionContext ctx, final Product product, final Date date, final boolean net,
            final boolean onlinePrice) throws JaloPriceFactoryException {
        final List<PriceInformation> sapPricesInformation = new ArrayList<>();
        final User currentUser = ctx.getUser();
        final Europe1PriceFactory europe1PriceFactory = (Europe1PriceFactory) getDefaultPriceFactory();
        final String productCode = product.getCode();

        // get the current prices
        List<PriceInformation> prices = getPriceInformations(ctx, product, europe1PriceFactory.getPPG(ctx, product), currentUser,
                europe1PriceFactory.getUPG(ctx, currentUser), ctx.getCurrency(), net, date, null);
        
        final boolean onlineCustomer = userRequiresCalculation(currentUser);

        if (!onlinePrice || !onlineCustomer /* || productHasSpecialPrice(prices) */) {
            // Return the default prices
            return prices;
        }

        // Check whether the customer currency is same as shop default currency.
        final CurrencyModel defaultCurrency = getBaseStoreService().getCurrentBaseStore().getDefaultCurrency();
        if (!StringUtils.equals(ctx.getCurrency().getIsocode(), defaultCurrency.getIsocode())) {
            prices = getPriceInformations(ctx, product, europe1PriceFactory.getPPG(ctx, product), currentUser, europe1PriceFactory.getUPG(ctx, currentUser),
                    getModelService().getSource(defaultCurrency), net, date, null);
        }
        
        // Get SAP Prices only if the user is NOT anonymous and the price is not marked as special
        final String erpCustomerId = getErpCustomerID(currentUser);
        final String salesOrg = distSalesOrgService.getCurrentSalesOrg().getCode();
        final String currency = ctx.getCurrency().getIsocode();
        final Long minimumOrderQuantity = getMinimumOrderQuantityForProduct(product);
        final List<PriceInformation> filteredPrices = filterPricesBelowMoq(prices, minimumOrderQuantity);

        final boolean empty = CollectionUtils.isEmpty(filteredPrices);
        // Call SAP or retrieve from cache
        
        try {
            if (empty) {
                final Set<ProductPriceData> sapPrices = customerPriceService.getOnlinePriceList(erpCustomerId, salesOrg, currency, productCode, minimumOrderQuantity);
                if (sapPrices.stream().anyMatch(sapPrice -> sapPrice.getArticleNumber().equalsIgnoreCase(productCode))) {
                    final Set<VolumePriceData> p_prices = sapPrices.stream().filter(sapPrice -> sapPrice.getArticleNumber().equalsIgnoreCase(productCode)).findAny().get().getVolumePriceData();
                    p_prices.forEach(price -> {
                        sapPricesInformation.add(createSAPPriceInformation(price, ctx.getCurrency()));
                    }); 
                }
            } else {
                final Set<ProductPriceData> onlinePrices =  customerPriceService.getPricesForPricesList(erpCustomerId, salesOrg, currency, productCode, filteredPrices);
                for (final PriceInformation price : prices) {
                    try {
                        final DistPriceRow row = (DistPriceRow) price.getQualifierValue(PriceRow.PRICEROW);
                        // update the price information with sap prices
                        final Set<VolumePriceData> p_prices = onlinePrices.stream().filter(hybrisPrice -> hybrisPrice.getArticleNumber().equalsIgnoreCase(productCode)).findAny().get().getVolumePriceData();
                        VolumePriceData vPrice = p_prices.stream().filter(volPrice -> volPrice.getQuantity().equals(Integer.valueOf(row.getMinqtd().intValue()))).findAny().get();
                        sapPricesInformation.add(createSAPPriceInformation(row, vPrice, ctx.getCurrency()));
                    } catch (final Exception ex) {
                        // In case of exception add the default price.
                        LOG.error("Adding default Price, Error while getting online prices for product:{} for customer{}, Exception {}",productCode, currentUser.getUid(), ex);
                        sapPricesInformation.add(price);
                    }
                }
            }
        }catch(Exception ex) {
            LOG.error("Error while getting online prices for products:{} for customer{}, Exception {}",productCode, currentUser.getUid(), ex);
            return prices;
        }
        LOG.debug("getProductListPriceInformations for product:{} on salesOrg: {}, returns: {}", productCode, salesOrg,
                Arrays.toString(sapPricesInformation.toArray()));

        return sapPricesInformation;
    }

    protected List<PriceInformation> filterPricesBelowMoq(final List<PriceInformation> prices, final Long minimumOrderQuantity) {
        return prices.stream() //
                .filter(p -> ((PriceRow) p.getQualifierValue(PriceRow.PRICEROW)).getMinqtd() >= minimumOrderQuantity) //
                .collect(Collectors.toList());
    }

    protected String getErpCustomerID(final User currentUser) {
        return ((B2BCustomerModel) b2BCustomerService.getUserForUID(currentUser.getUid())).getDefaultB2BUnit().getErpCustomerID();
    }

    protected Long getMinimumOrderQuantityForProduct(final Product product) {
        return getMinimumOrderQuantityForProduct((ProductModel) modelService.toModelLayer(product));
    }

    protected Long getMinimumOrderQuantityForProduct(final ProductModel product) {
        final DistSalesOrgProductModel distSalesOrgProduct = getDistSalesOrgProductService().getCurrentSalesOrgProduct(product);
        return (distSalesOrgProduct == null || distSalesOrgProduct.getOrderQuantityMinimum() == null || distSalesOrgProduct.getOrderQuantityMinimum() < 1) ? DEFAULT_ORDER_QUANTITY_MINIMUM
                : distSalesOrgProduct.getOrderQuantityMinimum();
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
        if (!onlinePrice || !userRequiresCalculation) {
            return getProductListPriceInformations(ctx, (Product) getModelService().getSource(product), date, net, false);
        }

        final String productCode = product.getCode();
        final String salesOrg = distSalesOrgService.getCurrentSalesOrg().getCode();
        final String currency = ctx.getCurrency().getIsoCode();

        final User currentUser = ctx.getUser();
        final String erpCustomerId = !currentUser.isAnonymousCustomer()
                ? getErpCustomerID(currentUser)
                : getConfigurationService().getConfiguration().getString(DUMMY_CUSTOMER_ID_PROP, null);

                final Set<ProductPriceData> sapPrices = customerPriceService.getOnlinePriceList(erpCustomerId, salesOrg, currency, productCode,
                getMinimumOrderQuantityForProduct(product));
        if (CollectionUtils.isEmpty(sapPrices) || !sapPrices.stream().anyMatch(products -> products.getArticleNumber().equalsIgnoreCase(productCode))) {
            return Collections.<PriceInformation> emptyList();
        }

        final List<PriceInformation> sapPricesInformation = new ArrayList<>();

        final Set<VolumePriceData> p_prices = sapPrices.stream().filter(pProd -> pProd.getArticleNumber().equalsIgnoreCase(productCode)).findAny().get().getVolumePriceData();
        p_prices.forEach(key-> {
            sapPricesInformation.add(createSAPPriceInformation(key, ctx.getCurrency()));
        }); 

        return sapPricesInformation;
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
        if (!onlinePrice || !forceOnline) {
            return MapUtils.EMPTY_MAP;
        }

        final Map<String, Long> productCodesAndQuantities = products.stream()
                .collect(Collectors.toMap(p -> p.getCode(), p -> getMinimumOrderQuantityForProduct(p)));

        final String salesOrg = distSalesOrgService.getCurrentSalesOrg().getCode();
        final String currency = ctx.getCurrency().getIsoCode();
        final User currentUser = ctx.getUser();
        final String erpCustomerId = !currentUser.isAnonymousCustomer()
                ? getErpCustomerID(currentUser)
                : getConfigurationService().getConfiguration().getString(DUMMY_CUSTOMER_ID_PROP, null);

        final Set<ProductPriceData> sapPrices = customerPriceService.getOnlinePriceList(erpCustomerId, salesOrg, currency,
                productCodesAndQuantities);
        if (CollectionUtils.isEmpty(sapPrices)) {
            return MapUtils.EMPTY_MAP;
        }

        final Map<String, List<PriceInformation>> priceInfoMap = new HashMap<>();

        sapPrices.forEach( sapPrice -> {
            final Set<VolumePriceData> p_prices = sapPrice.getVolumePriceData();
            if (!CollectionUtils.isEmpty(p_prices)) {
                final List<PriceInformation> sapPricesInformation = new ArrayList<>();

                p_prices.forEach(key -> {
                    sapPricesInformation.add(createSAPPriceInformation(key, ctx.getCurrency()));
                });
                priceInfoMap.put(sapPrice.getArticleNumber(), sapPricesInformation);
            }
            
        });

        return priceInfoMap;
    }

    protected boolean userRequiresCalculation(final User currentUser) {

        try {
            // the user must not be anonymous
            if (currentUser.isAnonymousCustomer()) {
                return false;
            }

            // the onlinePriceCalculation flag must be true
            final B2BCustomerModel contact = ((B2BCustomerModel) b2BCustomerService.getUserForUID(currentUser.getUid()));
            return contact != null && contact.getDefaultB2BUnit() != null && BooleanUtils.isTrue(contact.getDefaultB2BUnit().getOnlinePriceCalculation());
        } catch (final Exception ex) {
            return false;
        }
    }

    private PriceInformation createSAPPriceInformation(final DistPriceRow row, final VolumePriceData price, final Currency currency) {
        final double priceValue = price.getPriceWithoutVat();
        final Map<String, Object> qualifiers = new HashMap<>();
        qualifiers.put(PriceRow.MINQTD, Long.valueOf(row.getMinQuantity()));
        qualifiers.put(PriceRow.UNIT, row.getUnit());
        qualifiers.put(PriceRow.PRICEROW, row);
        qualifiers.put(DistPriceRow.DISCOUNTED_PRICE, Boolean.TRUE);
        qualifiers.put(DistPriceRow.PRICEPERX, price.getPricePerX());
        qualifiers.put(DistPriceRow.PRICEPERXUOM, price.getPricePerXUOM());
        qualifiers.put(PRICEPERXUOMDESCRIPTION, price.getPricePerXUOMDesc());
        qualifiers.put(DistPriceRow.PRICEPERXUOMQTY, price.getPricePerXUOMQty());
        qualifiers.put(DistPriceRow.PRICEPERXBASEQTY, price.getPricePerXBaseQty());
        qualifiers.put(DistPriceRow.PRICEWITHVAT, price.getPriceWithVat());
        qualifiers.put(DistPriceRow.VATPERCENTAGE, price.getVatPercentage());
        qualifiers.put(DistPriceRow.VATVALUE, price.getVat());
        final DateRange dateRange = row.getDateRange();
        if (dateRange != null) {
            qualifiers.put(PDTRow.DATERANGE, dateRange);
        }

        // Mark the price as a custom price
        qualifiers.put(DistPriceRow.DISCOUNTED_PRICE, Boolean.TRUE);

        return new PriceInformation(qualifiers, new PriceValue(currency.getIsoCode(), priceValue, row.isNetAsPrimitive()));
    }

    private PriceInformation createSAPPriceInformation(VolumePriceData priceData, final Currency currency) {
        final Map<String, Object> qualifiers = new HashMap<>();
        qualifiers.put(PriceRow.MINQTD, Long.valueOf(priceData.getQuantity()));
        qualifiers.put(PriceRow.UNIT, null);
        qualifiers.put(PriceRow.PRICEROW, null);
        qualifiers.put(DistPriceRow.PRICEPERX, priceData.getPricePerX());
        qualifiers.put(DistPriceRow.PRICEPERXUOM, priceData.getPricePerXUOM());
        qualifiers.put(PRICEPERXUOMDESCRIPTION, priceData.getPricePerXUOMDesc());
        qualifiers.put(DistPriceRow.PRICEPERXUOMQTY, priceData.getPricePerXUOMQty());
        qualifiers.put(DistPriceRow.PRICEPERXBASEQTY, priceData.getPricePerXBaseQty());
        // Mark the price as a custom price
        qualifiers.put(DistPriceRow.DISCOUNTED_PRICE, Boolean.TRUE);
        qualifiers.put(DistPriceRow.PRICEWITHVAT, priceData.getPriceWithVat());
        qualifiers.put(DistPriceRow.VATPERCENTAGE, priceData.getVatPercentage());
        qualifiers.put(DistPriceRow.VATVALUE, priceData.getVat());

        return new PriceInformation(qualifiers, new PriceValue(currency.getIsoCode(), priceData.getPriceWithoutVat(), true));
    }

    @Override
    public List getProductListPriceInformations(final SessionContext ctx, final Product product, final Date date, final boolean net)
            throws JaloPriceFactoryException {
        final Europe1PriceFactory europe1PriceFactory = (Europe1PriceFactory) getDefaultPriceFactory();
        return getListPriceInformations(ctx, product, europe1PriceFactory.getPPG(ctx, product), ctx.getUser(), europe1PriceFactory.getUPG(ctx, ctx.getUser()),
                ctx.getCurrency(), net, date, null);
    }

    @Override
    public List getProductTaxInformations(final SessionContext ctx, final Product product, final Date date) throws JaloPriceFactoryException {
        return getTaxInformations(ctx, product, date);
    }

    @Override
    public List getProductDiscountInformations(final SessionContext ctx, final Product product, final Date date, final boolean net)
            throws JaloPriceFactoryException {
        throw new UnsupportedOperationException("Method not supported");
    }

    @Override
    public boolean isNetUser(final User user) {
        return isNet(user);
    }

    @Override
    public Collection getTaxValues(final AbstractOrderEntry abstractOrderEntry) throws JaloPriceFactoryException {
        // TODO: this is copied from the MovexPriceFactory, check if this is correct
        final EnumerationValue ptg = getSalesOrgSpecificPTG(abstractOrderEntry.getProduct());
        if (ptg == null) {
            LOG.error("{} Can not determine sales org specific product tax class for product {}. Please check the product tax class definitions for this product!",ErrorLogCode.ORDER_RELATED_ERROR.getCode() , abstractOrderEntry.getProduct());
            return Collections.emptyList();
        } else {
            try {
                final SessionContext localCtx = JaloSession.getCurrentSession().createLocalSessionContext(JaloSession.getCurrentSession().getSessionContext());
                localCtx.setAttribute(Europe1Constants.PARAMS.PTG, ptg);
                final Collection<TaxValue> taxValues = getDefaultPriceFactory().getTaxValues(abstractOrderEntry);
                JaloSession.getCurrentSession().removeLocalSessionContext();
                return taxValues;
            } catch (final JaloPriceFactoryException e) {
                LOG.error("{} Error in retrieving tax values for Order Entry {}",ErrorLogCode.ORDER_RELATED_ERROR.getCode(),abstractOrderEntry.getEntryNumber(),e);
                return Collections.emptyList();
            }
        }
    }

    @Override
    public PriceValue getBasePrice(final AbstractOrderEntry abstractOrderEntry) throws JaloPriceFactoryException {
        throw new UnsupportedOperationException("This function is not available for SAP.");
    }

    @Override
    public List getDiscountValues(final AbstractOrderEntry abstractOrderEntry) throws JaloPriceFactoryException {
        throw new UnsupportedOperationException("This function is not available for SAP.");
    }

    @Override
    public List getDiscountValues(final AbstractOrder abstractOrder) throws JaloPriceFactoryException {
        return super.getDefaultPriceFactory().getDiscountValues(abstractOrder);
        // throw new UnsupportedOperationException("This function is not available for SAP.");
        // return ListUtils.EMPTY_LIST;
    }

    @Override
    public DistPriceRow createDistPriceRow(final Product product, final User user, final EnumerationValue userPriceGroup, final DistSalesOrg salesOrg,
            final long minQuantity, final Currency currency, final Unit unit, final int unitFactor, final boolean net, final DateRange dateRange,
            final double price, final String priceConditionIdErp, final DistErpPriceConditionType erpPriceConditionType, final Date lastModifiedErpDate,
            final Long sequenceId) throws JaloPriceFactoryException {
        return createPriceRow(product, user, userPriceGroup, minQuantity, currency, unit, unitFactor, net, dateRange, price, priceConditionIdErp,
                erpPriceConditionType, lastModifiedErpDate, sequenceId);
    }

    public CustomerPriceService getCustomerPriceService() {
        return customerPriceService;
    }

    public void setCustomerPriceService(final CustomerPriceService customerPriceService) {
        this.customerPriceService = customerPriceService;
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

    public OrderCalculationService getOrderCalculationService() {
        return orderCalculationService;
    }

    @Required
    public void setOrderCalculationService(final OrderCalculationService orderCalculationService) {
        this.orderCalculationService = orderCalculationService;
    }

    @Required
    public void setB2BCustomerService(final B2BCustomerService b2BCustomerService) {
        this.b2BCustomerService = b2BCustomerService;
    }

    public B2BCustomerService getB2BCustomerService() {
        return b2BCustomerService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
    // internal methods
}
