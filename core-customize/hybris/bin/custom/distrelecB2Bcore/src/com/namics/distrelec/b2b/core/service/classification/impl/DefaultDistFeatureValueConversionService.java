package com.namics.distrelec.b2b.core.service.classification.impl;

import com.namics.distrelec.b2b.core.service.classification.DistFeatureValueConversionService;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.servicelayer.i18n.FormatFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.NumberFormat;
import java.util.Locale;

public class DefaultDistFeatureValueConversionService implements DistFeatureValueConversionService {

    @Autowired
    private FormatFactory formatFactory;

    @Override
    public String toString(FeatureValue featureValue) {
        Object value = featureValue.getValue();

        String stringValue;
        if (value instanceof Double) {
            NumberFormat numberFormat = createDoubleNumberFormat();
            stringValue = numberFormat.format(value);
        } else {
            stringValue = String.valueOf(value);
        }
        return stringValue;
    }

    protected NumberFormat createDoubleNumberFormat() {
        // use english number format to get decimal points on all sites
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
        numberFormat.setGroupingUsed(false);
        return numberFormat;
    }

    public FormatFactory getFormatFactory() {
        return formatFactory;
    }

    public void setFormatFactory(FormatFactory formatFactory) {
        this.formatFactory = formatFactory;
    }
}
