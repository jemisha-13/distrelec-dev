/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.validation.validators;

import com.namics.distrelec.b2b.storefront.validation.annotations.AtLeastOneNotBlank;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Assert;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for at least one field.
 */
public class AtLeastOneNotBlankValidator implements ConstraintValidator<AtLeastOneNotBlank, Object> {

    private static final Logger LOG = LogManager.getLogger(AtLeastOneNotBlankValidator.class);

    private String[] fieldnames;
    private String message;

    @Override
    public void initialize(final AtLeastOneNotBlank constraintAnnotation) {
        Assert.notEmpty(constraintAnnotation.value());
        Assert.isTrue(constraintAnnotation.value().length > 1);
        fieldnames = constraintAnnotation.value();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final Object object, final ConstraintValidatorContext constraintContext) {

        for (final String number : fieldnames) {
            try {
                final String entry = (String) PropertyUtils.getProperty(object, number);
                if (StringUtils.isNotBlank(entry)) {
                    return true;
                }
            } catch (final Exception e) {
                LOG.error("Could not validate", e);
                throw new IllegalArgumentException(e);
            }

        }

        constraintContext.buildConstraintViolationWithTemplate(message)
                         .addPropertyNode(fieldnames[0])
                         .addConstraintViolation();
        constraintContext.disableDefaultConstraintViolation();
        return false;
    }
}
