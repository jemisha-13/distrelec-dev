package com.namics.distrelec.b2b.facades.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import de.hybris.platform.servicelayer.i18n.I18NService;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class DistDateFormatPerLocaleHelper {

    private static final String DATE_PATTERN = "dd/MM/yyyy";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private I18NService i18nService;

    public String formatDateForCurrentLocale(Date date) {
        return formatDateForLocale(date, i18nService.getCurrentLocale());
    }

    public String formatDateForLocale(Date date, Locale locale) {
        if (date != null) {
            String format = messageSource.getMessage("text.store.dateformat", null, DATE_PATTERN, locale);
            @SuppressWarnings("ConstantConditions") // suppress warning for possible null of the "format" variable - it can't be null as it has a default value
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(date);
        }

        return EMPTY;
    }

}
