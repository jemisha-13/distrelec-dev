package com.namics.distrelec.b2b.storefront.validation.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.namics.distrelec.b2b.storefront.validation.validators.BizPostalCodeValidator;

@Retention(RUNTIME)
@Constraint(validatedBy = BizPostalCodeValidator.class)
@Documented
@Inherited
public @interface BizPostalCode {
    String message();

    String pattern();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

