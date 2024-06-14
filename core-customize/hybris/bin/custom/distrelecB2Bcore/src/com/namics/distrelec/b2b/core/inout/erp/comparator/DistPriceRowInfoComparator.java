/*
 * Copyright 2000-2017 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.comparator;

import java.util.Comparator;

import com.namics.distrelec.b2b.core.jalo.DistPriceRow;

import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.c2l.Currency;

/**
 * DistPriceRowInfoComparator
 * 
 * @author dathusir, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class DistPriceRowInfoComparator implements Comparator<PriceRow> {

    private final Currency curr;
    private final boolean net;

    /**
     * Create a new instance of {@code DistPriceRowInfoComparator}
     * 
     * @param currency
     * @param net
     */
    public DistPriceRowInfoComparator(final Currency currency, final boolean net) {
        this.curr = currency;
        this.net = net;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(final PriceRow row1, final PriceRow row2) {
        final long u1Match = row1.getUnit().getPK().getLongValue();
        final long u2Match = row2.getUnit().getPK().getLongValue();
        if (u1Match != u2Match) {
            return (u1Match < u2Match) ? -1 : 1;
        }

        final long min1 = row1.getMinqtdAsPrimitive();
        final long min2 = row2.getMinqtdAsPrimitive();
        if (min1 != min2) {
            return (min1 > min2) ? 1 : -1;
        }

        final int m1 = ((DistPriceRow) row1).getMatchValueAsPrimitive();
        final int m2 = ((DistPriceRow) row2).getMatchValueAsPrimitive();
        if (m1 != m2) {
            return m2 - m1;
        }

        final DistPriceRow dRow1 = (DistPriceRow) row1;
        final DistPriceRow dRow2 = (DistPriceRow) row2;

        if (dRow1.getErpPriceConditionType() != null && dRow2.getErpPriceConditionType() != null) {
            if (dRow1.getErpPriceConditionType().equals(dRow2.getErpPriceConditionType())) {
                return (dRow1.getStartTime().getTime() - dRow2.getStartTime().getTime()) < 0 ? 1 : -1;
            } else {
                return dRow1.getErpPriceConditionType().getPriority().compareTo(dRow2.getErpPriceConditionType().getPriority());
            }
        }

        final boolean c1Match = this.curr.equals(row1.getCurrency());
        final boolean c2Match = this.curr.equals(row2.getCurrency());
        if (c1Match != c2Match) {
            return c1Match ? -1 : 1;
        }

        final boolean n1Match = this.net == row1.isNetAsPrimitive();
        final boolean n2Match = this.net == row2.isNetAsPrimitive();
        if (n1Match != n2Match) {
            return n1Match ? -1 : 1;
        }

        final boolean row1Range = row1.getStartTime() != null;
        final boolean row2Range = row2.getStartTime() != null;

        if (row1Range != row2Range) {
            return row1Range ? -1 : 1;
        }
        return row1.getPK().compareTo(row2.getPK());
    }

}
