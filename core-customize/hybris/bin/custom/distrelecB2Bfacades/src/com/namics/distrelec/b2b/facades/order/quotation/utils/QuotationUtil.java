package com.namics.distrelec.b2b.facades.order.quotation.utils;


import com.namics.distrelec.b2b.core.enums.QuoteModificationType;

public class QuotationUtil {

    public QuotationUtil() {
    }

    public static QuoteModificationType getFromCode(final String code) {
        if (code == null) {
            return QuoteModificationType.ALL;
        }

        if ("01".equals(code)) {
            return QuoteModificationType.INCREASE;
        }
        if ("02".equals(code)) {
            return QuoteModificationType.DECREASE;
        }
        if ("03".equals(code)) {
            return QuoteModificationType.OFF;
        }

        return QuoteModificationType.ALL;
    }
}
