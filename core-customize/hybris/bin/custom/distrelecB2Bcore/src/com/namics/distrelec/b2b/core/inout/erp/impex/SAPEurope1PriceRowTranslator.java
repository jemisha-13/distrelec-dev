/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impex;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.namics.distrelec.b2b.core.jalo.DistErpPriceConditionType;
import com.namics.distrelec.b2b.core.jalo.DistPriceRow;
import com.namics.distrelec.b2b.core.jalo.DistSalesOrg;
import com.namics.distrelec.b2b.core.jalo.Namb2bacceleratorCoreManager;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.price.DistPriceFactory;
import com.namics.hybris.toolbox.spring.SpringUtil;

import de.hybris.platform.acceleratorservices.jalo.AcceleratorServicesManager;
import de.hybris.platform.europe1.jalo.Europe1PriceFactory;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.impex.jalo.header.StandardColumnDescriptor;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.enumeration.EnumerationManager;
import de.hybris.platform.jalo.enumeration.EnumerationType;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.product.Unit;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.DateRange;
import de.hybris.platform.util.StandardDateRange;

/**
 * Price translator for a single price.
 * 
 * @author daehusir, Distrelec
 * 
 */
@SuppressWarnings("deprecation")
public class SAPEurope1PriceRowTranslator extends SAPEurope1RowTranslator {

    private static final Logger LOG = LogManager.getLogger(SAPEurope1PricesTranslator.class);

    private final ModelService modelService = SpringUtil.getBean("modelService", ModelService.class);

    private final Map<String, EnumerationValue> priceUserGroupsMap = new HashMap<String, EnumerationValue>();
    private final Map<String, DistSalesOrg> salesOrgMap = new HashMap<String, DistSalesOrg>();
    private final EnumerationType pug = EnumerationManager.getInstance().getEnumerationType("UserPriceGroup");
    private static final String DUMMY_PRODUCT_CODE = "DUMMYPRODUCTCODE";

    public SAPEurope1PriceRowTranslator() {
        // Empty constructor
    }

    public SAPEurope1PriceRowTranslator(final SimpleDateFormat dateFormat, final NumberFormat numberFormat, final Locale loc) {
        super(dateFormat, numberFormat, loc);
    }

    @Override
    public void validate(final StandardColumnDescriptor standardcd) throws HeaderValidationException {
        super.validate(standardcd);
        if (Product.class.isAssignableFrom(standardcd.getHeader().getConfiguredComposedType().getJaloClass())) {
            return;
        }
        throw new HeaderValidationException(standardcd.getHeader(), "SAPEurope1PriceRowTranslator may only be used within product headers", 0);
    }

    @Override
    public void init(final StandardColumnDescriptor columnDescriptor) {
        if (columnDescriptor != null) {
            super.init(columnDescriptor);
        }

        final List<EnumerationValue> priceUserGroupsList = pug.getValues();
        for (final EnumerationValue enumval : priceUserGroupsList) {
            priceUserGroupsMap.put(enumval.getCode().toLowerCase(), enumval);
        }

        final FlexibleSearch flexibleSearch = FlexibleSearch.getInstance();
        final List<DistSalesOrg> result = flexibleSearch.search("SELECT {PK} FROM {DistSalesOrg}", DistSalesOrg.class).getResult();
        for (final DistSalesOrg salesOrg : result) {
            salesOrgMap.put(salesOrg.getCode().toLowerCase(), salesOrg);
        }
    }

    @Override
    public Object convertToJalo(final String valueExpr, final Item forItem) {
        double price = 0.0D;
        Unit unit2use = null;
        User user = null;
        int unitFactor = 1;
        long quantity = 1L;
        boolean net = false;
        DateRange dateRange = null;
        EnumerationValue userPriceGroup = null;
        DistSalesOrg salesOrg = null;
        final Product product = (Product) forItem;
        final SAPEurope1RowTranslator.ParsedSAPCurrency pCurr = parseSAPCurrency(valueExpr);
        final int startOfCurrencyDef = pCurr.getPos();
        final Currency currency2use = pCurr.getCurr();

        if (currency2use == null) {
            throw new JaloInvalidParameterException("Unable to find the currency definition within " + valueExpr, 123);
        }

        if (valueExpr.indexOf(" N ") != -1 || valueExpr.endsWith(" N")) {
            net = true;
        }

        dateRange = parseDateRange(valueExpr);
        final int equalsPos = valueExpr.indexOf(61);
        if (equalsPos != -1) {
            final int lastNumericPos = getLastNumericPos(valueExpr, equalsPos);
            final int firstNumericPos = getFirstNumericPos(valueExpr, lastNumericPos);

            // Get price
            price = parsePrice(valueExpr, equalsPos, startOfCurrencyDef);

            // Get unit
            unit2use = parseUnit(valueExpr, lastNumericPos, equalsPos, product);

            // Get scale
            final String quantityExpr = valueExpr.substring(firstNumericPos, lastNumericPos + 1).trim();
            unitFactor = Integer.parseInt(quantityExpr.split("/")[0]);
            quantity = Long.parseLong(quantityExpr.split("/")[1]);

            // Get user or price user group and sales org
            if (firstNumericPos > 2) {
                user = parseUser(valueExpr, firstNumericPos);

                if (user == null) {
                    userPriceGroup = parseUserPriceGroup(valueExpr, firstNumericPos);
                }

                salesOrg = parseSalesOrg(valueExpr, firstNumericPos);
            }
        } else {
            final String priceDef = valueExpr.substring(0, startOfCurrencyDef).trim();
            try {
                final NumberFormat numberformat = getNumberFormat();
                synchronized (numberformat) {
                    price = numberformat.parse(priceDef).doubleValue();
                }
            } catch (final ParseException e) {
                throw new JaloSystemException(e);
            }

            if (product.getUnit() == null) {
                throw new JaloInvalidParameterException("missing unit within " + valueExpr, 123);
            }

            unit2use = product.getUnit();
            quantity = 1L;
        }

        return createOrUpdatePriceRow(product, getErpPriceConditionType(valueExpr), user, userPriceGroup, salesOrg, quantity, currency2use, unit2use,
                unitFactor, net, dateRange, price, parsePriceConditionIdErp(valueExpr, product), parseSequenceId(valueExpr),
                parseLastModifiedErpDate(valueExpr));
    }

    @Override
    protected String convertToString(final Object value) {
        throw new NotImplementedException("An export of price rows with this translator is not implemented yet!");
    }

    protected DistErpPriceConditionType getErpPriceConditionType(final String valueExpr) {
        if (valueExpr.indexOf(']') > -1) {
            final String erpPriceConditionTypeCode = valueExpr.substring(valueExpr.indexOf(']') + 2, valueExpr.indexOf(' ', valueExpr.indexOf(']') + 2));
            return Namb2bacceleratorCoreManager.getInstance().getErpPriceConditionTypeByCode(erpPriceConditionTypeCode);
        }
        throw new JaloInvalidParameterException("Unable to find the price type definition within " + valueExpr, 123);
    }

    protected String parsePriceConditionIdErp(final String valueExpr, final Product product) {
        try {

            String priceConditionIdErp = getMetaData(valueExpr)[0];
            try {
                // check if the pricecondition is an expression (example for CatalogPlus import)
                final String[] priceConditionIdErpParts = priceConditionIdErp.split("-");

                // the structure of the experssion could be [prefix]-DUMMYPLACEHOLDER-salesOrganization
                if (priceConditionIdErpParts.length == 2 && DUMMY_PRODUCT_CODE.equals(priceConditionIdErpParts[0])) {
                    priceConditionIdErp = product.getCode() + priceConditionIdErpParts[1];
                } else if (priceConditionIdErpParts.length == 3 && DUMMY_PRODUCT_CODE.equals(priceConditionIdErpParts[1])) {
                    priceConditionIdErp = priceConditionIdErpParts[0] + product.getCode() + priceConditionIdErpParts[2];
                }
            } catch (final Exception ex) {
                LOG.error("There were problem while processing priceCondition " + priceConditionIdErp + " for product " + product);
            }
            return priceConditionIdErp;
        } catch (final Exception e) {
            throw new JaloInvalidParameterException("Unable to find the priceConditionIdErp definition within " + valueExpr, 123);
        }
    }

    protected Date parseLastModifiedErpDate(final String valueExpr) {
        try {
            final SimpleDateFormat erpDateFormat = new SimpleDateFormat("yyyyMMddHHmmss"); // NOPMD
            try {
                return erpDateFormat.parse(getMetaData(valueExpr)[1]);
            } catch (final ParseException e) {
                LOG.error(e);
                throw new JaloInvalidParameterException("Can not parse last modified ERP date definition within " + valueExpr, 123); // NOPMD
            }
        } catch (final Exception e) {
            LOG.error(e);
            throw new JaloInvalidParameterException("Unable to find the last modified ERP date definition within " + valueExpr, 123); // NOPMD
        }
    }

    protected void setLastModifiedErpDate(final DistPriceRow priceRow, final Date lastModifiedErpDate) {
        priceRow.setLastModifiedErp(lastModifiedErpDate);
    }

    protected Long parseSequenceId(final String valueExpr) {
        try {
            return Long.valueOf(getMetaData(valueExpr)[2]);
        } catch (final Exception e) {
            LOG.error(e);
            throw new JaloInvalidParameterException("Unable to find the sequenceId definition within " + valueExpr, 123); // NOPMD
        }
    }

    protected void setSequenceId(final DistPriceRow priceRow, final Long sequenceId) {
        // Set current sequenceId on price row to mark this price row as updated.
        final AcceleratorServicesManager accServiceManager = AcceleratorServicesManager.getInstance();
        accServiceManager.setSequenceId(priceRow, sequenceId);
    }

    protected String[] getMetaData(final String valueExpr) {
        final int startPosOfMetadData = valueExpr.indexOf(' ', valueExpr.indexOf(']') + 2) + 1;
        return valueExpr.substring(startPosOfMetadData, valueExpr.indexOf(' ', startPosOfMetadData)).split("_");
    }

    protected double parsePrice(final String valueExpr, final int equalsPos, final int startOfCurrencyDef) {
        String priceExpr = valueExpr.substring(equalsPos + 1, startOfCurrencyDef).trim();

        try {
            final NumberFormat numberformat = getNumberFormat();
            synchronized (numberformat) {
                priceExpr = parseMathExpression(priceExpr);
                // Check if the price is a decimal number or if the price is delivered in cents
                if (!priceExpr.contains(".")) {
                    return numberformat.parse(priceExpr).doubleValue() / 100;
                }
                return numberformat.parse(priceExpr).doubleValue();
            }
        } catch (final ParseException e) {
            throw new JaloSystemException(e);
        }
    }

    /**
     * This method evaluates the price specified in impex expression and check if it contains arithmetic expression Supports multiply (*)
     * and divide(/)
     * 
     * @param priceExpr
     *            the price expression
     * @return the calculated value
     */
    private String parseMathExpression(final String priceExpr) {
        try {
            final String[] parts = priceExpr.split("[-+*/]");

            if (parts.length == 2) {
                final double value1 = Double.parseDouble(parts[0]);
                // get operator
                final char operator = priceExpr.trim().substring(priceExpr.indexOf(parts[0]) + parts[0].length(),
                        priceExpr.indexOf(parts[1], priceExpr.indexOf(parts[0]) + parts[0].length())).charAt(0);

                final double value2 = Double.parseDouble(parts[1]);

                Double returnValue;
                switch (operator) {
                case '*':
                    returnValue = Double.valueOf(value1 * value2);
                    break;
                case '/':
                    returnValue = Double.valueOf(value1 / value2);
                    break;
                case '+':
                    returnValue = Double.valueOf(value1 + value2);
                    break;
                case '-':
                    returnValue = Double.valueOf(value1 - value2);
                    break;
                default:
                    returnValue = null;
                    break;
                }

                if (returnValue == null) {
                    return StringUtils.EMPTY;
                } else {
                    return returnValue.toString();
                }

            }
        } catch (final Exception e) {
            throw new JaloSystemException(e);
        }

        return priceExpr;
    }

    protected int getFirstNumericPos(final String valueExpr, final int lastNumericPos) {
        int firstNumericPos = -1;
        final int firstSpacePos = valueExpr.indexOf(32);
        int firstSearchPos = 0;
        if ((firstSpacePos != -1) && (firstSpacePos < lastNumericPos)) {
            firstSearchPos = firstSpacePos;
        }

        for (int pos = firstSearchPos; pos < lastNumericPos; ++pos) {
            final char digit = valueExpr.charAt(pos);
            if (!(Character.isDigit(digit))) {
                continue;
            }
            firstNumericPos = pos;
            break;
        }

        if (firstNumericPos == -1) {
            firstNumericPos = lastNumericPos;
        }

        return firstNumericPos;
    }

    protected int getLastNumericPos(final String valueExpr, final int equalsPos) {
        int lastNumericPos = -1;
        for (int pos = 0; pos < equalsPos; ++pos) {
            final char digit = valueExpr.charAt(pos);
            if (!(Character.isDigit(digit))) {
                continue;
            }
            lastNumericPos = pos;
        }

        if (lastNumericPos == -1) {
            throw new JaloInvalidParameterException("Unable to find the unit definition within " + valueExpr, 123);
        }

        return lastNumericPos;
    }

    protected Unit parseUnit(final String valueExpr, final int lastNumericPos, final int equalsPos, final Product product) {
        final String unitCode = valueExpr.substring(lastNumericPos + 1, equalsPos).trim();
        Unit unit2use = null;
        if (!StringUtils.isBlank(unitCode)) {
            final Collection units = ProductManager.getInstance().getUnitsByCode(unitCode);
            if (units.isEmpty()) {
                throw new JaloInvalidParameterException("Unable to find the unit definition within " + valueExpr, 123);
            }
            if (units.size() > 1) {
                throw new JaloInvalidParameterException("Ambiguous unit definition within " + valueExpr, 123);
            }
            unit2use = (Unit) units.iterator().next();
        }

        if (unit2use == null) {
            if (product.getUnit() == null) {
                throw new JaloInvalidParameterException("missing unit within " + valueExpr, 123);
            }
            unit2use = product.getUnit();
        }

        return unit2use;
    }

    protected User parseUser(final String valueExpr, final int firstNumericPos) {
        final String userOrUsergroupExpr = valueExpr.substring(0, firstNumericPos).trim();

        if (StringUtils.isNotBlank(userOrUsergroupExpr) && !userOrUsergroupExpr.startsWith("SalesOrg")) {
            try {
                return UserManager.getInstance().getUserByLogin(userOrUsergroupExpr);
            } catch (final JaloItemNotFoundException e) {
                LOG.error(e);
                throw new JaloInvalidParameterException("given user can not be found within " + valueExpr, 123); // NOPMD
            }
        }

        LOG.debug("No user found. Trying to find a user price group.");
        return null;
    }

    protected EnumerationValue parseUserPriceGroup(final String valueExpr, final int firstNumericPos) {
        final String userOrUsergroupExpr = valueExpr.substring(0, firstNumericPos).trim();

        if (priceUserGroupsMap.containsKey(userOrUsergroupExpr.toLowerCase())) { // NOPMD
            return priceUserGroupsMap.get(userOrUsergroupExpr.toLowerCase()); // NOPMD
        }

        throw new JaloInvalidParameterException("missing user price group within " + valueExpr, 123);
    }

    protected DistSalesOrg parseSalesOrg(final String valueExpr, final int firstNumericPos) {
        final String userOrUsergroupExpr = valueExpr.substring(0, firstNumericPos).trim();
        final String salesOrgCode = userOrUsergroupExpr.split("_")[2].toLowerCase();
        if (salesOrgMap.containsKey(salesOrgCode)) {
            return salesOrgMap.get(salesOrgCode);
        }

        throw new JaloInvalidParameterException("missing sales org within " + valueExpr, 123);
    }

    protected DistPriceRow createOrUpdatePriceRow(final Product product, final DistErpPriceConditionType erpPriceConditionType, final User user,
            final EnumerationValue userPriceGroup, final DistSalesOrg salesOrg, final long quantity, final Currency currency2use, final Unit unit2use,
            final int unitFactor, final boolean net, final DateRange dateRange, final double price, final String priceConditionIdErp, final Long sequenceId,
            final Date lastModifiedErpDate) {
        if (quantity > 0) {
            if (unitFactor > 0) {
                // Do not use the PriceFactory from the current JaloSession, because Movex and SAP implementation was always null. It is not
                // clear why the values are null! So this is just a work around!
                final DistPriceFactory priceFactory = SpringUtil.getBean("erp.priceFactory", DistPriceFactory.class);
                final Europe1PriceFactory europe1PriceFactory = (Europe1PriceFactory) priceFactory
                        .getDefaultPriceFactory(modelService.<DistSalesOrgModel> get(salesOrg));
                try {
                    for (final PriceRow priceRow : europe1PriceFactory.getEurope1Prices(product)) {
                        if (!(priceRow instanceof DistPriceRow)) {
                            LOG.error("The current price row (" + priceRow + ") is no instance of DistPriceRow! Please delete it!");
                            continue;
                        }
                        final DistPriceRow distPriceRow = (DistPriceRow) priceRow;
                        if (isSamePriceRow(distPriceRow, priceConditionIdErp, userPriceGroup, currency2use, quantity, unit2use, unitFactor)) {
                            // Check if the last modified ERP date is newer, if not the price condition will be skipped
                            if (priceInformationIsNewer(distPriceRow, lastModifiedErpDate)) {
                                final User oldUser = distPriceRow.getUser();
                                if (oldUser == null && user != null) {
                                    distPriceRow.setUser(user);
                                } else if (oldUser != null && user != null && !oldUser.equals(user)) {
                                    distPriceRow.setUser(user);
                                }

                                final boolean oldIsNet = distPriceRow.isNetAsPrimitive();
                                if (oldIsNet != net) {
                                    distPriceRow.setNet(net);
                                }

                                final DateRange oldDateRange = distPriceRow.getDateRange();
                                if (oldDateRange == null && dateRange != null) {
                                    distPriceRow.setDateRange((StandardDateRange) dateRange);
                                } else if (oldDateRange != null && dateRange != null && !oldDateRange.equals(dateRange)) {
                                    distPriceRow.setDateRange((StandardDateRange) dateRange);
                                }

                                final double oldPrice = distPriceRow.getPriceAsPrimitive();
                                if (oldPrice != price) {
                                    distPriceRow.setPrice(price);
                                }

                                final DistErpPriceConditionType oldErpPriceConditionType = distPriceRow.getErpPriceConditionType();
                                if (oldErpPriceConditionType != erpPriceConditionType) {
                                    distPriceRow.setErpPriceConditionType(erpPriceConditionType);
                                }

                                // Set current sequenceId on price row to mark this price row as updated.
                                setSequenceId(distPriceRow, sequenceId);
                                setLastModifiedErpDate(distPriceRow, lastModifiedErpDate);
                                return distPriceRow;
                            } else {
                                // Set current sequenceId on price row to mark this price row as updated.
                                setSequenceId(distPriceRow, sequenceId);
                                return distPriceRow;
                            }
                        }
                    }

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("### Creating PriceRow with...");
                        LOG.debug("forItem: " + product);
                        LOG.debug("priceConditionIdErp: " + priceConditionIdErp);
                        LOG.debug("user: " + user);
                        LOG.debug("userPriceGroup: " + userPriceGroup);
                        LOG.debug("quantity: " + quantity);
                        LOG.debug("currency2use: " + currency2use);
                        LOG.debug("unit2use: " + unit2use);
                        LOG.debug("unitFactor: " + unitFactor);
                        LOG.debug("net: " + net);
                        LOG.debug("dateRange: " + dateRange);
                        LOG.debug("price: " + price);
                        LOG.debug("sequenceId: " + sequenceId);
                        LOG.debug("lastModifiedErpDate: " + lastModifiedErpDate);
                        LOG.debug("erpPriceConditionType: " + erpPriceConditionType);
                    }

                    return priceFactory.createDistPriceRow(product, user, userPriceGroup, salesOrg, quantity, currency2use, unit2use, unitFactor, net,
                            dateRange, price, priceConditionIdErp, erpPriceConditionType, lastModifiedErpDate, sequenceId);
                } catch (final JaloPriceFactoryException e) {
                    throw new JaloSystemException(e);
                } catch (final JaloInvalidParameterException e) {
                    throw new JaloSystemException(e);
                } catch (final JaloSecurityException e) {
                    throw new JaloSystemException(e);
                }
            } else {
                LOG.error("The current price row has a unit factor of " + unitFactor + ", which is zero or below zero! The price row will be skipped!");
            }
        }
        return null;
    }

    protected boolean isSamePriceRow(final DistPriceRow existingPriceRow, final String priceConditionIdErp, final EnumerationValue userPriceGroup,
            final Currency currency2use, final long quantity, final Unit unit2use, final int unitFactor) {
        // A PriceRow is just identical if the sales organization (ug), the currency the minqtd, the unit and the unit factor is equal
        final String existingPriceConditionIdErp = existingPriceRow.getPriceConditionIdErp();
        if (existingPriceConditionIdErp != null) {
            if (existingPriceConditionIdErp.equals(priceConditionIdErp)) {
                if ((existingPriceRow.getUg() == null || (existingPriceRow.getUg() != null && existingPriceRow.getUg().equals(userPriceGroup)))
                        && existingPriceRow.getCurrency().equals(currency2use) && existingPriceRow.getMinqtdAsPrimitive() == quantity
                        && existingPriceRow.getUnit().equals(unit2use) && existingPriceRow.getUnitFactorAsPrimitive() == unitFactor) {
                    return true;
                }
            }
        } else {
            throw new IllegalStateException("Found an existing price row (" + existingPriceRow + ") without a ERP price condition id!");
        }

        return false;
    }

    protected boolean priceInformationIsNewer(final DistPriceRow priceRow, final Date lastModifiedErpDate) throws JaloSecurityException {
        final Date existingLastModifiedErpDate = priceRow.getLastModifiedErp();
        if (existingLastModifiedErpDate == null || existingLastModifiedErpDate.before(lastModifiedErpDate)) {
            return true;
        }

        return false;
    }
}