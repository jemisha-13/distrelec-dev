package com.namics.distrelec.b2b.storefront.validation.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.namics.distrelec.b2b.storefront.validation.validators.NotBlankForCountryValidator;

@Retention(RUNTIME)
@Constraint(validatedBy = NotBlankForCountryValidator.class)
@Documented
@Inherited
public @interface NotBlankForCountry {
    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] countries();
}
