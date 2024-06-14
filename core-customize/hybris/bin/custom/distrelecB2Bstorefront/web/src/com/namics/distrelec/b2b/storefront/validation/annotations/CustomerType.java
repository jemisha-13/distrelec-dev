package com.namics.distrelec.b2b.storefront.validation.annotations;

import com.namics.distrelec.b2b.storefront.validation.validators.CustomerTypeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.ANNOTATION_TYPE,
    java.lang.annotation.ElementType.CONSTRUCTOR, java.lang.annotation.ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { CustomerTypeValidator.class })
@Documented
public @interface CustomerType {
	
	
	   public abstract String message();

	    Class<?>[] groups() default {};

	    Class<? extends Payload>[] payload() default {};

	    String[] validCustomerTypes();
	  
}
