package com.namics.distrelec.occ.core.validator;

import com.namics.distrelec.occ.core.v2.forms.OrderHistoryForm;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderHistoryFormValidator implements Validator {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private I18NService i18NService;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        final OrderHistoryForm orderHistoryForm = (OrderHistoryForm) target;
        Assert.notNull(errors, "Errors object must not be null");
        Date fromDate = null;
        Date toDate = null;

        if (StringUtils.isNotEmpty(orderHistoryForm.getFromDate())) {
            try {
                fromDate = parseLocalSpecificDate(orderHistoryForm.getFromDate());
            } catch (final ParseException e) {
                errors.rejectValue("fromDate", "validate.error.date.format");
            }
        }

        if (StringUtils.isNotEmpty(orderHistoryForm.getToDate())) {
            try {
                toDate = parseLocalSpecificDate(orderHistoryForm.getToDate());
            } catch (final ParseException e) {
                errors.rejectValue("toDate", "validate.error.date.format");
            }
        }

        if ((fromDate != null) && (toDate != null) && fromDate.after(toDate)) {
            errors.rejectValue("fromDate", "validate.error.date.before");
        }

        if (orderHistoryForm.getOrderNumber() != null && !orderHistoryForm.getOrderNumber().trim().matches("[A-Za-z0-9\\*]*")) {
            errors.rejectValue("orderNumber", "validate.error.alphanumeric");
        }
    }

    private Date parseLocalSpecificDate(final String date) throws ParseException {
        return new SimpleDateFormat(getDataFormatForCurrentCmsSite()).parse(date);
    }

    private String getDataFormatForCurrentCmsSite() {
        final Locale locale = new Locale(getI18NService().getCurrentLocale().getLanguage(), getCmsSiteService().getCurrentSite().getCountry().getIsocode());
        return getMessageSource().getMessage("text.store.dateformat.datepicker.selection", null, "MM/dd/yyyy", locale);
    }

    protected MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    protected I18NService getI18NService() {
        return i18NService;
    }

    public void setI18NService(I18NService i18NService) {
        this.i18NService = i18NService;
    }

    protected CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }
}
