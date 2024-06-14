/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex.europe1;

import java.util.Map;

import org.apache.log4j.Logger;

/**
 * <p>
 * Berechnet aus value1:value2 -> value1 - value2
 * 
 * Beispiel:
 * 
 * $discounts=europe1Discounts[translator=de.hybris.platform.europe1.jalo.impex.Europe1ProductDiscountTranslator][ dateformat=EEE MMM dd
 * HH:mm:ss z yyyy][cellDecorator=com.namics.hybris.toolbox.impex.europe1.DiscountCalcDecorator]
 * 
 * INSERT_UPDATE Product;product(code)[unique=true];$discounts;$CatalogVersion[unique=true]
 * 
 * ;616031500000;100:5.0-Sat Jan 01 00:00:00 CET 2011-Thu Dec 31 00:00:00 CET 2099
 * 
 * </p>
 * 
 * 
 * @author mwegener, namics ag
 * @since MGB MEL 1.0
 * 
 */
public class DiscountCalcDecorator extends DefaultDiscountDecorator {
    private static final Logger LOG = Logger.getLogger(DiscountCalcDecorator.class.getName());

    public static final String DISCOUNT_COLLECTION_SEPERATOR_MODIFIER = "discountCollectionSeperator";
    public static final String VALUE_SEPERATOR_MODIFIER = "valueSeperator";
    public static final String PRICE_DISCOUNT_SEPERATOR_MODIFIER = "priceDiscountSeperator";

    private final static String PRICE_DISCOUNT_SEPARATOR = ":";
    private final static String DISCOUNT_COLLECTION_SEPARATOR = ",";
    private final static String VALUE_SEPARATOR = "-";

    @Override
    public String decorate(final int paramInt, final Map<Integer, String> paramMap) {
        final String[] importValue = paramMap.get(Integer.valueOf(paramInt)).split(VALUE_SEPARATOR);
        final String[] desiredValue = importValue[0].split(PRICE_DISCOUNT_SEPARATOR);

        final StringBuffer buffer = new StringBuffer();
        if (desiredValue.length == 2) {
            final double price = Double.parseDouble(desiredValue[0]);
            final String[] discountValues = desiredValue[1].split(DISCOUNT_COLLECTION_SEPARATOR);

            for (final String discountValue : discountValues) {
                final double discount = Double.parseDouble(discountValue);
                double calulatedResult = price - discount;
                if (calulatedResult < 0) {
                    calulatedResult = 0.0;
                }
                buffer.append(calulatedResult + DISCOUNT_COLLECTION_SEPARATOR);
            }

            for (int i = 1; i < importValue.length; i++) {
                // unverarbeitete Werte wieder hinzufügen für super call
                buffer.append(VALUE_SEPARATOR + importValue[i]);
            }
        }

        String resultValue = buffer.toString();
        LOG.debug(resultValue);

        paramMap.put(Integer.valueOf(paramInt), buffer.toString());
        // calls DefaultDiscountDecorator.decorate with preprocessed paramMap
        resultValue = super.decorate(paramInt, paramMap);

        return resultValue;
    }
}
