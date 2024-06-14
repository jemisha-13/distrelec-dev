/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.i18n.hmc;

import de.hybris.platform.hmc.attribute.DoubleEditorChip;
import de.hybris.platform.hmc.webchips.Chip;
import de.hybris.platform.hmc.webchips.DisplayState;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Map;

public class NamicsDoubleEditorChip extends DoubleEditorChip {

    // parameters are not read from hmc.xml?! so set defaultvalues fix
    private String decimalSeparator = ".";
    private String groupingSeparator = "'";

    private ConfigurationService configurationService;

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

    public NamicsDoubleEditorChip(final DisplayState displayState, final Chip parent) {
        super(displayState, parent);
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
        // hybris does not call this method?
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
