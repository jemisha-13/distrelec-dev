/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.data.campaign.advisor;

import java.util.List;

/**
 * POJO for Advisor Campaign questions.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.1
 * 
 * @param <STATE>
 */
public class AdvisorCampaignQuestionData<STATE> {

    private String question;
    private List<AdvisorCampaignAnswerData<STATE>> answers;

    public List<AdvisorCampaignAnswerData<STATE>> getAnswers() {
        return answers;
    }

    public void setAnswers(final List<AdvisorCampaignAnswerData<STATE>> answers) {
        this.answers = answers;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(final String question) {
        this.question = question;
    }

}
