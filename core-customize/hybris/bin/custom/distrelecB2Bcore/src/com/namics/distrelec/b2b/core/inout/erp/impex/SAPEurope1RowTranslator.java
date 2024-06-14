/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impex;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;

import de.hybris.platform.europe1.jalo.impex.Europe1RowTranslator;
import de.hybris.platform.jalo.c2l.Currency;

/**
 * SAPEurope1RowTranslator.
 * 
 * @author daehusir, Distrelec
 * 
 */
public abstract class SAPEurope1RowTranslator extends Europe1RowTranslator {

    public SAPEurope1RowTranslator() {
        super();
    }

    public SAPEurope1RowTranslator(final SimpleDateFormat dateFormat, final NumberFormat numberFormat, final Locale loc) {
        super(dateFormat, numberFormat, loc);
    }

    protected static class ParsedSAPCurrency {
        protected final Currency curr;
        protected final int pos;

        ParsedSAPCurrency(final Currency curr, final int pos) {
            this.curr = curr;
            this.pos = pos;
        }

        public Currency getCurr() {
            return curr;
        }

        public int getPos() {
            return pos;
        }
    }

    protected ParsedSAPCurrency parseSAPCurrency(final String valueExpr) {
        int startOfCurrencyDef = -1;
        Currency currency2use = null;
        for (final Iterator<String> it = getCurrenciesISOs().keySet().iterator(); it.hasNext() && currency2use == null;) {
            final String iso = it.next().toLowerCase();
            startOfCurrencyDef = valueExpr.toLowerCase().indexOf(iso);
            if (startOfCurrencyDef == -1) {
                continue;
            }
            currency2use = (Currency) getCurrenciesISOs().get(iso);
        }

        for (final Iterator<String> it = getCurrenciesSymbols().keySet().iterator(); it.hasNext() && currency2use == null;) {
            final String symbol = it.next().toLowerCase();
            startOfCurrencyDef = valueExpr.toLowerCase().indexOf(symbol);
            if (startOfCurrencyDef == -1) {
                continue;
            }
            currency2use = (Currency) getCurrenciesSymbols().get(symbol);

        }

        return new ParsedSAPCurrency(currency2use, startOfCurrencyDef);
    }

}
