/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.campaign;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import de.factfinder.webservice.ws71.FFcampaign.ArrayOfCampaign;

/**
 * POJO for a campaign response.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class CampaignResponse {

    private ArrayOfCampaign campaigns;

    // BEGIN GENERATED CODE

    public ArrayOfCampaign getCampaigns() {
        if (campaigns == null) {
            campaigns = new ArrayOfCampaign();
        }
        return campaigns;
    }

    public void setCampaigns(final ArrayOfCampaign campaigns) {
        this.campaigns = campaigns;
    }

    @Override
    public String toString() {
        return "CampaignResponse [campaigns=" + ReflectionToStringBuilder.toString(getCampaigns().getCampaign()) + "]";
    }

}
