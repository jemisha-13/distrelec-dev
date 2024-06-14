/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.validation.annotations;

import com.namics.distrelec.b2b.storefront.validation.validators.PhonenumberValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.ANNOTATION_TYPE,
        java.lang.annotation.ElementType.CONSTRUCTOR, java.lang.annotation.ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { PhonenumberValidator.class })
@Documented
public @interface Phonenumber {
    String message();

    Class<?>[] groups() default {};

    boolean checkForCurrentCountry() default false;

    Class<? extends Payload>[] payload() default {};

    boolean required() default false;

    @Target({ java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.ANNOTATION_TYPE,
            java.lang.annotation.ElementType.CONSTRUCTOR, java.lang.annotation.ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        Phonenumber[] value();
    }
}