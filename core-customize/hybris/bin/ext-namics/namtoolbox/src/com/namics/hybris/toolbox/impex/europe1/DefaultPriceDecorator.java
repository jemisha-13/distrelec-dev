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
 * Decorates a number to act as a price for the {@link Europe1PricesTranslator}
 * </p>
 * 
 * 
 * @author jweiss, namics ag
 * @since MGB MEL 1.0
 * 
 */
public class DefaultPriceDecorator extends AbstractImpExCSVCellDecorator {
    private static final Logger LOG = Logger.getLogger(DefaultPriceDecorator.class.getName());

    public static final String CURRENCY_MODIFIER = "currency";
    public static final String UNIT_MODIFIER = "unit";
    public static final String MINQTY_MODIFIER = "minqty";
    public static final String NET_MODIFIER = "net";
    public static final String PRICE_SEPERATOR_MODIFIER = "priceSeperator";
    public static final String VALUE_SEPERATOR_MODIFIER = "valueSeperator";

    private String currency = "CHF";
    private String unit = "pieces";
    private int minqty = 1;
    private boolean net = true;
    private String priceSeperator = ";";
    private String valueSeperator = "-";

    @Override
    public void init(final AbstractColumnDescriptor column) throws HeaderValidationException {
        super.init(column);

        if (column.getDescriptorData().getModifier(CURRENCY_MODIFIER) != null) {
            currency = column.getDescriptorData().getModifier(CURRENCY_MODIFIER);
        }
        if (column.getDescriptorData().getModifier(UNIT_MODIFIER) != null) {
            unit = column.getDescriptorData().getModifier(UNIT_MODIFIER);
        }
        if (column.getDescriptorData().getModifier(MINQTY_MODIFIER) != null) {
            minqty = Integer.parseInt(column.getDescriptorData().getModifier(MINQTY_MODIFIER));
        }

        if (column.getDescriptorData().getModifier(NET_MODIFIER) != null) {
            net = column.getDescriptorData().getModifier(NET_MODIFIER).equals("true");
        }

        if (column.getDescriptorData().getModifier(PRICE_SEPERATOR_MODIFIER) != null) {
            priceSeperator = column.getDescriptorData().getModifier(PRICE_SEPERATOR_MODIFIER);
        }

        if (column.getDescriptorData().getModifier(VALUE_SEPERATOR_MODIFIER) != null) {
            valueSeperator = column.getDescriptorData().getModifier(VALUE_SEPERATOR_MODIFIER);
        }

        if (!StringUtils.hasText(currency)) {
            throw new HeaderValidationException("The modifier [" + CURRENCY_MODIFIER + "] in " + DefaultPriceDecorator.class + " must be set.", -1);
        }
        if (!StringUtils.hasText(unit)) {
            throw new HeaderValidationException("The modifier [" + UNIT_MODIFIER + "] in " + DefaultPriceDecorator.class + " must be set.", -1);
        }
        if (minqty < 1) {
            throw new HeaderValidationException("The modifier [" + MINQTY_MODIFIER + "] in " + DefaultPriceDecorator.class + " must be bigger than zero.", -1);
        }

    }

    @Override
    public String decorate(final int paramInt, final Map<Integer, String> paramMap) {

        final String importValue = paramMap.get(Integer.valueOf(paramInt));
        final String[] tokens = importValue.split(valueSeperator);
        //
        final StringBuffer resultValue = new StringBuffer();

        if (tokens.length >= 1 && tokens[0].contains(priceSeperator)) {
            // Wenn mehrere Preiszeilen geliefert werden

            final String prices[] = tokens[0].split(priceSeperator);
            String datesFrom[] = null;
            String datesTo[] = null;

            if (tokens.length == 3) {
                // Wenn Datum 'von' und 'bis' angegeben sind
                datesFrom = tokens[1].split(priceSeperator);
                datesTo = tokens[2].split(priceSeperator);
            }

            for (int i = 0; i < prices.length; i++) {
                // Für jeden price value

                if (datesFrom == null && !prices[i].equals("")) {
                    // "21 pieces = 10 EUR N"
                    resultValue.append(minqty + " " + unit + " = " + prices[i] + " " + currency + " " + (net ? " N" : "G") + ",");
                } else if (datesFrom != null && !prices[i].equals("")) {
                    // "21 pieces = 10 EUR N [01.01.2000,31.12.2000]"
                    resultValue.append(minqty + " " + unit + " = " + prices[i] + " " + currency + " " + (net ? " N" : "G") + " [" + datesFrom[i] + ","
                            + datesTo[i] + "],");
                }
            }
        } else if (tokens.length == 1) {
            // Einzelne Preiszeile ohne Datuminformationen
            // "21 pieces = 10 EUR N"

            resultValue.append(minqty + " " + unit + " = " + importValue + " " + currency + " " + (net ? " N" : "G"));
        } else if (tokens.length == 3) {
            // Einzelne Preiszeile mit Datuminformationen
            // "21 pieces = 10 EUR N [01.01.2000,31.12.2000]"

            resultValue.append(minqty + " " + unit + " = " + tokens[0] + " " + currency + " " + (net ? " N" : "G") + " [" + tokens[1] + "," + tokens[2] + "],");
        }

        LOG.debug(resultValue);
        return resultValue.toString();
    }
}
