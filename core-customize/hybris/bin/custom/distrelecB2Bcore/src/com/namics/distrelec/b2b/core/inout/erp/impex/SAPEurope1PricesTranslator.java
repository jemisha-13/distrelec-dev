/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.namics.distrelec.b2b.core.jalo.DistPriceRow;
import com.namics.distrelec.b2b.core.model.DistPriceRowModel;

import de.hybris.platform.impex.jalo.translators.AbstractValueTranslator;
import de.hybris.platform.impex.jalo.translators.CollectionValueTranslator;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.jalo.type.CollectionType;
import de.hybris.platform.jalo.type.TypeManager;

/**
 * Price translator for SAP price conditions.
 * 
 * @author daehusir, Distrelec
 * 
 */
@SuppressWarnings("deprecation")
public class SAPEurope1PricesTranslator extends CollectionValueTranslator {

    public static final char BRUTTO = 66;
    public static final char NETTO = 78;
    public static final char GROSS = 71;
    private static final Logger LOG = LogManager.getLogger(SAPEurope1PricesTranslator.class);
    // dummy placeholder for price condition
    private static final String DUMMY_PRODUCT_CODE = "DUMMYPRODUCTCODE";

    public SAPEurope1PricesTranslator() {
        super((CollectionType) TypeManager.getInstance().getType("PriceRowCollectionType"), new SAPEurope1PriceRowTranslator());
    }

    public SAPEurope1PricesTranslator(final AbstractValueTranslator elementTranslator) {
        super((CollectionType) TypeManager.getInstance().getType("PriceRowCollectionType"), elementTranslator);
    }

    @Override
    public Object importValue(final String valueExpr, final Item forItem) {
        final Product product = (Product) forItem;
        final DistPriceRow priceRowToDelete = parseDeletedPriceCondition(valueExpr, product);
        if (priceRowToDelete != null) {
            if (LOG.isInfoEnabled()) {
                try {
                    LOG.info("Price row with priceConditionIdErp [" + priceRowToDelete.getAttribute(DistPriceRowModel.PRICECONDITIONIDERP)
                            + "], user price group [" + getUserGroup(priceRowToDelete) + "], scale [" + priceRowToDelete.getMinqtdAsPrimitive()
                            + "] and currency [" + priceRowToDelete.getCurrency().getIsocode() + "] for product [" + product
                            + "] is marked as deleted and will be removed!");
                } catch (final JaloSecurityException e) {
                    throw new JaloSystemException(e);
                }
            }
            try {
                priceRowToDelete.remove();
            } catch (final ConsistencyCheckException e) {
                throw new JaloSystemException(e);
            }
            return null;
        } else if (valueExpr.endsWith("false")) {
            final Object ret = super.importValue(valueExpr, forItem);
            if (ret instanceof List && ((List) ret).size() > 0) {
                final List<DistPriceRow> prices = (List) ret;
                final DistPriceRow priceRowTemplate = prices.get(prices.size() - 1);
                if (priceRowTemplate != null) {
                    final Long sequenceId = parseSequenceId(valueExpr);
                    try {
                        final Map<String, Object> params = new HashMap<String, Object>();
                        params.put(DistPriceRowModel.PRODUCT, product);
                        params.put(DistPriceRowModel.SEQUENCEID, sequenceId);
                        params.put(DistPriceRowModel.CURRENCY, priceRowTemplate.getCurrency());
                        params.put(DistPriceRowModel.UG, priceRowTemplate.getUg());
                        params.put(DistPriceRowModel.PRICECONDITIONIDERP, priceRowTemplate.getPriceConditionIdErp());
                        params.put(DistPriceRowModel.ERPPRICECONDITIONTYPE, priceRowTemplate.getErpPriceConditionType());

                        if (priceRowTemplate.getUserGroup() != null) {
                            final List<DistPriceRow> priceRowsToRemove = FlexibleSearch.getInstance()
                                    .search("SELECT {PK} FROM {" + DistPriceRowModel._TYPECODE + "} WHERE {" + DistPriceRowModel.PRODUCT + "}=?"
                                            + DistPriceRowModel.PRODUCT + " AND {" + DistPriceRowModel.CURRENCY + "}=?" + DistPriceRowModel.CURRENCY + " AND {"
                                            + DistPriceRowModel.UG + "}=?" + DistPriceRowModel.UG + " AND {" + DistPriceRowModel.PRICECONDITIONIDERP
                                            + "}=?priceConditionIdErp AND {" + DistPriceRowModel.SEQUENCEID + "}!=?" + DistPriceRowModel.SEQUENCEID, params,
                                            DistPriceRow.class)
                                    .getResult();

                            if (!priceRowsToRemove.isEmpty()) {
                                for (final DistPriceRow priceRow : priceRowsToRemove) {
                                    // Price row is not longer valid and will be removed
                                    if (LOG.isInfoEnabled()) {
                                        LOG.info("Price row with priceConditionIdErp [" + priceRow.getAttribute(DistPriceRowModel.PRICECONDITIONIDERP)
                                                + "], user price group [" + getUserGroup(priceRow) + "], scale [" + priceRow.getMinqtdAsPrimitive()
                                                + "] and currency [" + priceRow.getCurrency().getIsocode() + "] is not longer valid and will be removed!");
                                    }
                                    // First remove the price row from the returning list and than remove the price row in hybris
                                    ((List) ret).remove(priceRow);
                                    priceRow.remove();
                                }
                            }
                        } else {
                            throw new IllegalStateException(
                                    "User price group for price row " + priceRowTemplate + " was null! This is not a legal state. Please fix it!");
                        }
                    } catch (final JaloInvalidParameterException e) {
                        throw new JaloSystemException(e);
                    } catch (final JaloSecurityException e) {
                        throw new JaloSystemException(e);
                    } catch (final ConsistencyCheckException e) {
                        throw new JaloSystemException(e);
                    }
                } else {
                    throw new JaloSystemException("No price row found that has been update within this value: " + valueExpr);
                }
            }
            return ret;
        } else {
            return null;
        }
    }

    @Override
    protected List splitAndUnescape(final String valueExpr) {
        final List<String> tokens = super.splitAndUnescape(valueExpr);
        if (tokens == null || tokens.size() < 2) {
            return tokens;
        }

        final List<String> ret = new ArrayList<String>(tokens.size());
        ret.add(tokens.get(0));
        for (int i = 1; i < tokens.size(); ++i) {
            final String prev = tokens.get(i - 1);
            final String current = tokens.get(i);

            if (Character.isDigit(prev.charAt(prev.length() - 1))) {
                final String sxx = ret.get(ret.size() - 1);
                ret.set(ret.size() - 1, sxx + getCollectionValueDelimiter() + current);
            } else {
                ret.add(current);
            }
        }
        return ret;
    }

    protected DistPriceRow parseDeletedPriceCondition(final String valueExpr, final Product product) {
        if (valueExpr.endsWith("true")) {
            // Price condition is marked as deleted in SAP
            final String priceConditionIdErp = parsePriceConditionIdErp(valueExpr, product);

            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("product", product);
            params.put("priceConditionIdErp", priceConditionIdErp);
            final List<DistPriceRow> priceRowToDelete = FlexibleSearch.getInstance()
                    .search("SELECT {PK} FROM {" + DistPriceRowModel._TYPECODE + "} WHERE {" + DistPriceRowModel.PRODUCT + "}=?product AND {"
                            + DistPriceRowModel.PRICECONDITIONIDERP + "}=?priceConditionIdErp", params, DistPriceRow.class)
                    .getResult();
            if (priceRowToDelete.isEmpty()) {
                LOG.warn("No price row with priceConditionIdErp " + priceConditionIdErp + " for product " + product + " found! Duplicate?");
            } else if (priceRowToDelete.size() > 1) {
                throw new JaloSystemException(
                        "More than one price row with priceConditionIdErp " + priceConditionIdErp + " for product " + product + " found!");
            } else {
                return priceRowToDelete.get(0);
            }
        }
        return null;
    }

    private String getUserGroup(final DistPriceRow priceRow) {
        if (priceRow.getUserGroup() != null) {
            return priceRow.getUserGroup().getCode();
        } else {
            return "null";
        }
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

    protected Long parseSequenceId(final String valueExpr) {
        try {
            return Long.valueOf(getMetaData(valueExpr)[2]);
        } catch (final Exception e) {
            throw new JaloInvalidParameterException("Unable to find the sequenceId definition within " + valueExpr, 123);
        }
    }

    protected String[] getMetaData(final String valueExpr) {
        final int startPosOfMetadData = valueExpr.indexOf(' ', valueExpr.indexOf(']') + 2) + 1;
        return valueExpr.substring(startPosOfMetadData, valueExpr.indexOf(' ', startPosOfMetadData)).split("_");
    }

    /**
     * Modifiers for price translator.
     * 
     * @author daehusir, Distrelec
     * 
     */
    public static class Modifiers {
        public static final String NUMBER_FORMAT = "price-format";
        public static final String DATE_FORMAT = "date-format";
    }
}
