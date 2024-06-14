/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.identify;

public class MethodSignature {
    private String returnType;
    private Object returnValue;

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(final String returnType) {
        this.returnType = returnType;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(final Object returnValue) {
        this.returnValue = returnValue;
    }

}
