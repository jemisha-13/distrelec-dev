/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.namics.distrelec.b2b.storefront.validation.annotations;

import com.namics.distrelec.b2b.storefront.validation.validators.EqualAttributesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Constraint(validatedBy = EqualAttributesValidator.class)
@Documented
@Inherited
public @interface EqualAttributes {
    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] value();
}
