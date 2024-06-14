/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.recommendation;

/**
 * POJO for a recommendation request.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class RecommendationRequest {

    private String channel;
    private String productCode;

    // BEGIN GENERATED CODE

    public String getChannel() {
        return channel;
    }

    public void setChannel(final String channel) {
        this.channel = channel;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(final String productCode) {
        this.productCode = productCode;
    }

}
