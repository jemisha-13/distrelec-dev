/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.validation.annotations;

import com.namics.distrelec.b2b.storefront.validation.validators.CancellationPolicyConfirmationValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to validate cancellation policy on checkout review form.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.1
 */
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CancellationPolicyConfirmationValidator.class)
public @interface CancellationPolicyConfirmation {

    String message() default "javax.validation.constraints.AssertTrue.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
