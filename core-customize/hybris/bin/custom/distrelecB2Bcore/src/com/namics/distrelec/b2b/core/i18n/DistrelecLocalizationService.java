package com.namics.distrelec.b2b.core.i18n;

import de.hybris.platform.servicelayer.internal.i18n.impl.DefaultLocalizationService;
import org.apache.commons.lang.LocaleUtils;

import java.util.Locale;

public class DistrelecLocalizationService extends DefaultLocalizationService {

    @Override
    protected DataLocale matchDataLocale(Locale loc, boolean throwError) {
        Locale usedLocale = calculateLocale(loc);
        return super.matchDataLocale(usedLocale, throwError);
    }

    private Locale calculateLocale(Locale loc) {
        Locale usedLocale = loc;

        String localeString = loc.toString();

        if(localeString.contains("_")){
            String[] localeStringArray = localeString.split("_");
            usedLocale = LocaleUtils.toLocale(localeStringArray[0]+"_"+localeStringArray[1].toUpperCase());
        }
        return usedLocale;
    }
}
