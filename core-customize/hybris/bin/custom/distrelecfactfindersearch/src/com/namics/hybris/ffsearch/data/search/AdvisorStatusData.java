/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.search;

/**
 * POJO for an Advisor Campaign status.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.1
 */
public class AdvisorStatusData {

    private String answerPath;
    private String campaignId;

    public String getAnswerPath() {
        return answerPath;

    }

    public void setAnswerPath(final String answerPath) {
        this.answerPath = answerPath;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(final String campaignId) {
        this.campaignId = campaignId;
    }

}
