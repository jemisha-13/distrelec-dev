package com.namics.distrelec.b2b.storefront.validation.validators;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.storefront.validation.annotations.BizContact;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.Assert;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;

public class BizContactValidator implements ConstraintValidator<BizContact, Object> {

    private static final Logger LOG = LogManager.getLogger(BizContactValidator.class);
    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    private String message;
    private String[] example;
    private String[] messageExample;
    private String countryIsoCode;
    public static final String BIZ_COUNTRY_CODE = "EX";
    public static final String SWISS_COUNTRY_CODE="CH";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private I18NService i18nService;

    @Autowired
    private DistrelecStoreSessionFacade storeSessionFacade;

    protected MessageSource getMessageSource() {
        return messageSource;
    }

    protected I18NService getI18nService() {
        return i18nService;
    }

    protected DistrelecStoreSessionFacade getStoreSessionFacade() {
        return storeSessionFacade;
    }

    @Override
    public void initialize(final BizContact constraintAnnotation) {
        Assert.notEmpty(constraintAnnotation.value());
        Assert.isTrue(constraintAnnotation.value().length > 1);

        example = constraintAnnotation.value();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final Object object, final ConstraintValidatorContext constraintContext) {
        boolean result = true;
        countryIsoCode = getStoreSessionFacade().getCurrentCountry().getIsocode();
        if (object == null) {
            return true;
        }
        
        try {
            if (countryIsoCode != null && countryIsoCode.equalsIgnoreCase(BIZ_COUNTRY_CODE)) {
                initializeData(object);
                try {
                    String number = (String) PropertyUtils.getProperty(object, example[1]);
                    if (StringUtils.isEmpty(number)) {
                        return true;
                    }
                    final PhoneNumber numberProto = phoneUtil.parse(number,countryIsoCode);
                    result = phoneUtil.isPossibleNumber(numberProto);
                } catch (final NumberParseException e) {
                    result = false;
                }
                if (!result) {
                    constraintContext.disableDefaultConstraintViolation();
                    constraintContext.buildConstraintViolationWithTemplate(
                            getMessageSource().getMessage(message.substring(1, message.length() - 1), messageExample, message, getI18nService().getCurrentLocale())).addNode(example[1]).addConstraintViolation();
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
            // YTODO Auto-generated catch block
            LOG.warn("Invalid property");
        }

        return result;
    }

    private void initializeData(Object object) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String countryIsoCode = (String) PropertyUtils.getProperty(object, example[0]);
        if(countryIsoCode==null) {
            countryIsoCode=SWISS_COUNTRY_CODE;
        }
        final PhoneNumber nr = phoneUtil.getExampleNumber(countryIsoCode);
        messageExample = nr == null ? new String[] {}
                : new String[] { phoneUtil.format(nr, PhoneNumberFormat.NATIONAL), phoneUtil.format(nr, PhoneNumberFormat.INTERNATIONAL),
                        phoneUtil.format(nr, PhoneNumberFormat.E164) };
    }
}
