/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.catplus.impex.decorator;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.namics.distrelec.b2b.core.inout.impex.AbstractDistCellDecorator;

import de.hybris.platform.jalo.JaloSystemException;

public class DistCheckPriceCellDecorator extends AbstractDistCellDecorator {

    @Override
    public String decorate(final int position, final Map<Integer, String> line) {

        String priceExpressions = line.get(Integer.valueOf(position));
        if (StringUtils.isEmpty(priceExpressions)) {
            throw new JaloSystemException("Price expression is empty in impex line!");
        }
        String[] priceExpressionParts = priceExpressions.split(", ");

        for (int i = 0; i < priceExpressionParts.length; i++) {

            String impexPriceExpression = priceExpressionParts[i];

            String[] impexPriceExpressionParts = impexPriceExpression.split(" ");

            if (impexPriceExpressionParts.length != 10) {
                throw new JaloSystemException("Price expression line specified in impex: " + impexPriceExpression
                        + " is not valid. The price expression is in position: " + i);
            }

            // start manipulating scale and unitFactor
            String scaleUnitExpression = impexPriceExpressionParts[1];

            String[] scaleUnitExpressionParts = scaleUnitExpression.split("/");

            if (scaleUnitExpressionParts.length < 2 || StringUtils.isEmpty(scaleUnitExpressionParts[0]) || StringUtils.isEmpty(scaleUnitExpressionParts[0])) {
                // we add zero for both even if only one value for scale or unitfactor is filled
                scaleUnitExpressionParts = new String[2];
                // set the default price value equals to ZERO
                scaleUnitExpressionParts[0] = "0";
                scaleUnitExpressionParts[1] = "0";

                final String operator = "/";
                scaleUnitExpression = scaleUnitExpressionParts[0] + operator + scaleUnitExpressionParts[1];
                impexPriceExpressionParts[1] = scaleUnitExpression;
                impexPriceExpression = StringUtils.join(impexPriceExpressionParts, " ");
            }
            priceExpressionParts[i] = impexPriceExpression;

            // start manipulating price value
            String priceValueExpression = impexPriceExpressionParts[3];

            final String[] priceValueExpressionParts = priceValueExpression.split("[-+*/]");

            if (StringUtils.isEmpty(priceValueExpressionParts[0])) {
                final String originalPriceValue = priceValueExpressionParts[0];
                // set the default price value equals to ZERO
                priceValueExpressionParts[0] = "0";
                if (priceValueExpressionParts.length == 2) {
                    final String operator = priceValueExpression.substring(originalPriceValue.length(), originalPriceValue.length() + 1);
                    priceValueExpression = priceValueExpressionParts[0] + operator + priceValueExpressionParts[1];
                } else {
                    priceValueExpression = priceValueExpressionParts[0];
                }
                impexPriceExpressionParts[3] = priceValueExpression;
                impexPriceExpression = StringUtils.join(impexPriceExpressionParts, " ");
            }
            priceExpressionParts[i] = impexPriceExpression;
        }
        priceExpressions = StringUtils.join(priceExpressionParts, ", ");

        return priceExpressions;
    }
}
