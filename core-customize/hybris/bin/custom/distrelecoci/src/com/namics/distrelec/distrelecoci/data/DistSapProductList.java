/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.distrelecoci.data;

import java.util.Map;

/**
 * DistSapProductList
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 3.0
 * 
 */
public abstract interface DistSapProductList {

    Map<String, String> getHeaders();

    DistSapProduct getProduct(int paramInt);

    int size();
}
