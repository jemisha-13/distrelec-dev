/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex.europe1;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import de.hybris.platform.europe1.jalo.impex.Europe1PricesTranslator;
import de.hybris.platform.impex.jalo.header.AbstractColumnDescriptor;
import de.hybris.platform.impex.jalo.header.AbstractImpExCSVCellDecorator;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;

/**
 * <p>
 * Decorates a number to act as a discount for the {@link Europe1PricesTranslator}
 * </p>
 * 
 * 
 * @author mwegener, namics ag
 * @since MGB MEL 1.0
 * 
 */
public class DefaultDiscountDecorator extends AbstractImpExCSVCellDecorator {
    private static final Logger LOG = Logger.getLogger(DefaultDiscountDecorator.class.getName());

    public static final String CURRENCY_MODIFIER = "currency";
    public static final String DISCOUNT_MODIFIER = "discount";
    public static final String DISCOUNT_SEPERATOR_MODIFIER = "discountSeperator";
    public static final String VALUE_SEPERATOR_MODIFIER = "valueSeperator";

    private String currency = "CHF";
    private String discountType = "SA";
    private static final String DISCOUNT_SEPARATOR = ",";
    private static final String VALUE_SEPARATOR = "-";

    @Override
    public void init(final AbstractColumnDescriptor column) throws HeaderValidationException {
        super.init(column);

        if (column.getDescriptorData().getModifier(CURRENCY_MODIFIER) != null) {
            currency = column.getDescriptorData().getModifier(CURRENCY_MODIFIER);
        }
        if (column.getDescriptorData().getModifier(DISCOUNT_MODIFIER) != null) {
            discountType = column.getDescriptorData().getModifier(DISCOUNT_MODIFIER);
        }

        if (!StringUtils.hasText(currency)) {
            throw new HeaderValidationException("The modifier [" + CURRENCY_MODIFIER + "] in " + DefaultDiscountDecorator.class + " must be set.", -1);
        }

        if (!StringUtils.hasText(discountType)) {
            throw new HeaderValidationException("The modifier [" + DISCOUNT_MODIFIER + "] in " + DefaultDiscountDecorator.class + " must be set.", -1);
        }

    }

    @Override
    public String decorate(final int paramInt, final Map<Integer, String> paramMap) {
        final String importValue = paramMap.get(Integer.valueOf(paramInt));
        final String[] tokens = importValue.split(VALUE_SEPARATOR);

        final StringBuffer resultValue = new StringBuffer();

        if (tokens.length == 3) {
            // Wenn die Werte 'value' , 'dateFrom' , 'dateTo' geliefert werden
            final String discounts[] = tokens[0].split(DISCOUNT_SEPARATOR);
            final String datesFrom[] = tokens[1].split(DISCOUNT_SEPARATOR);
            final String datesTo[] = tokens[2].split(DISCOUNT_SEPARATOR);

            for (int i = 0; i < discounts.length; i++) {
                // Für jeden discount value

                if (datesFrom[i].equals("") && !discounts[i].equals("")) {
                    // "12.30 CHF SA "
                    resultValue.append(discounts[i] + " " + currency + " " + discountType + ",");
                } else if (!discounts[i].equals("")) {
                    // "12.30 CHF SA [01.01.2000,31.12.2000]"
                    resultValue.append(discounts[i] + " " + currency + " " + discountType + " [" + datesFrom[i] + "," + datesTo[i] + "],");
                }
            }
        } else if (tokens.length == 1 && !"".equals(importValue)) {
            // Einzelne Discount Zeile ohne Datuminformationen
            // "21 pieces = 10 EUR N"
            resultValue.append(importValue + " " + currency + " " + discountType);
        }

        LOG.debug(resultValue);
        return resultValue.toString();
    }

}
