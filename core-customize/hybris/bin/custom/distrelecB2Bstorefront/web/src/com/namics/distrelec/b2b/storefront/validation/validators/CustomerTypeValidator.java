package com.namics.distrelec.b2b.storefront.validation.validators;

import com.namics.distrelec.b2b.storefront.validation.annotations.CustomerType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;


public class CustomerTypeValidator implements ConstraintValidator<CustomerType, Object> {

	private List<String> validCustomerTypeList;
	@Override
	public void initialize(CustomerType constraintAnnotation) {
		validCustomerTypeList=new ArrayList<String>();
		for(String type:constraintAnnotation.validCustomerTypes())
		{
			validCustomerTypeList.add(type.toUpperCase());
		}
		
	}
	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		
		if(validCustomerTypeList.contains(value.toString().toUpperCase()))
		{
		return true;
		}
		return false;
	}
//
	
	
	
}
