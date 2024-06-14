package com.namics.distrelec.b2b.storefront.validation.annotations;

import com.namics.distrelec.b2b.storefront.validation.validators.BizContactValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Repeatable(BizContact.List.class)
@Retention(RUNTIME)
@Constraint(validatedBy = BizContactValidator.class)
@Documented
@Inherited
public @interface BizContact {
    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    
    String[] value();
    
    @Documented
    @Retention(RUNTIME)
    @Inherited
    @interface List {
        BizContact[] value();
    }
}
