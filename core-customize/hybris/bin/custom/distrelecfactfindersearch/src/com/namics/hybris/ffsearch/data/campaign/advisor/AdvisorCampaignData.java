/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.campaign.advisor;

import java.util.List;

/**
 * POJO for Advisor Campaign data.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.1
 * 
 * @param <STATE>
 */
public class AdvisorCampaignData<STATE> {

    private List<AdvisorCampaignQuestionData<STATE>> questions;

    public List<AdvisorCampaignQuestionData<STATE>> getQuestions() {
        return questions;
    }

    public void setQuestions(final List<AdvisorCampaignQuestionData<STATE>> questions) {
        this.questions = questions;
    }

}
