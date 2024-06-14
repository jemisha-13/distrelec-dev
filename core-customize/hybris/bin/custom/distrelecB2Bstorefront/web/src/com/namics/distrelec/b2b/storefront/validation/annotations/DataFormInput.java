/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.validation.annotations;

import com.namics.distrelec.b2b.storefront.validation.validators.DataFormInputValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@code DataFormInput}
 * <p>
 * The annotated {@code CharSequence} must match the regular expression specified by its key. The regular expression follows the Java
 * regular expression conventions see {@link java.util.regex.Pattern}. <br/>
 * Accepts {@code CharSequence}. {@code null} elements are considered valid.
 * </p>
 * 
 * @author <a href="laurent.zamofing@distrelec.com">Laurent Zamofing</a>, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = { DataFormInputValidator.class })
@Documented
public @interface DataFormInput {

    /**
     * The error message key/content
     * 
     * @return the error message template
     */
    public String message() default "";

    /**
     * The regular expression to match
     * 
     * @return the regular expression, given by its key, to match
     */
    public String patternKey() default "";

    /**
     * The constraint groups
     * 
     * @return the groups the constraint belongs to
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.ANNOTATION_TYPE,
            java.lang.annotation.ElementType.CONSTRUCTOR, java.lang.annotation.ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public static @interface List {
        public abstract DataFormInput[] value();
    }
}