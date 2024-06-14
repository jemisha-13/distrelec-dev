/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.i18n.hmc;

import de.hybris.platform.hmc.attribute.DoubleDisplayChip;
import de.hybris.platform.hmc.webchips.Chip;
import de.hybris.platform.hmc.webchips.DisplayState;
import de.hybris.platform.jalo.Item;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Map;

public class NamicsDoubleDisplayChip extends DoubleDisplayChip {

    private String decimalSeparator;
    private String groupingSeparator;

    public String getDecimalSeparator() {
        return decimalSeparator;
    }

    public void setDecimalSeparator(final String decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    public String getGroupingSeparator() {
        return groupingSeparator;
    }

    public void setGroupingSeparator(final String groupingSeparator) {
        this.groupingSeparator = groupingSeparator;
    }

    public NamicsDoubleDisplayChip(final DisplayState displayState, final Chip parent, final String jspURI, final Item item, final String qualifier) {
        super(displayState, parent, jspURI, item, qualifier);
    }

    @Override
    protected NumberFormat getNumberFormat() {
        final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(getDisplayState().getLocale());
        if (getDecimalSeparator() != null && !getDecimalSeparator().isEmpty()) {
            otherSymbols.setDecimalSeparator(getDecimalSeparator().charAt(0));
        }
        if (getGroupingSeparator() != null && !getGroupingSeparator().isEmpty()) {
            otherSymbols.setGroupingSeparator(getGroupingSeparator().charAt(0));
        }
        return new DecimalFormat(getNumberPattern(), otherSymbols);
    }

    public void setParameters(@SuppressWarnings("rawtypes")
    final Map parameters) {
        super.setParameters(parameters);
        if (parameters != null && !parameters.isEmpty()) {
            if (parameters.containsKey("groupingSeparator")) {
                setGroupingSeparator((String) parameters.get("groupingSeparator"));
            }
            if (parameters.containsKey("decimalSeparator")) {
                setDecimalSeparator((String) parameters.get("decimalSeparator"));
            }
        }
    }

}
