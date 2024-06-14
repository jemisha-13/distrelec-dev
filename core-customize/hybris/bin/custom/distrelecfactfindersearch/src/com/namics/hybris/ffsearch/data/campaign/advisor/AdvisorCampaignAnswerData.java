/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.campaign.advisor;

/**
 * POJO for Advisor Campaign answers.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.1
 * 
 * @param <STATE>
 */
public class AdvisorCampaignAnswerData<STATE> {

    private String text;
    private String image;
    private STATE query;

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(final String image) {
        this.image = image;
    }

    public STATE getQuery() {
        return query;
    }

    public void setQuery(final STATE query) {
        this.query = query;
    }

}
