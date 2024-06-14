/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */
package com.namics.distrelec.b2b.storefront.validation.annotations;

import com.namics.distrelec.b2b.storefront.validation.validators.AtLeastOneNotBlankValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Constraint(validatedBy = AtLeastOneNotBlankValidator.class)
@Documented
@Inherited
public @interface AtLeastOneNotBlank {
    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] value();
}
