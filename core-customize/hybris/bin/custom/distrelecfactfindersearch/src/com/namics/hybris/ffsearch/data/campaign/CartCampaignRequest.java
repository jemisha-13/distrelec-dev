/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.campaign;

import java.util.List;

/**
 * POJO for a campaign request.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class CartCampaignRequest {

    private String channel;
    private List<String> productCodes;

    // BEGIN GENERATED CODE

    public String getChannel() {
        return channel;
    }

    public void setChannel(final String channel) {
        this.channel = channel;
    }

    public List<String> getProductCodes() {
        return productCodes;
    }

    public void setProductCodes(final List<String> productCodes) {
        this.productCodes = productCodes;
    }

}
