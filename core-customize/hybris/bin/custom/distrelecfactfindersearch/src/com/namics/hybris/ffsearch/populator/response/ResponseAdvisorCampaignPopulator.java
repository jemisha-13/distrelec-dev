/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.response;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.namics.hybris.ffsearch.data.campaign.advisor.AdvisorCampaignAnswerData;
import com.namics.hybris.ffsearch.data.campaign.advisor.AdvisorCampaignData;
import com.namics.hybris.ffsearch.data.campaign.advisor.AdvisorCampaignQuestionData;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.AdvisorStatusData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.populator.response.helper.SearchQueryTransformer;

import de.factfinder.webservice.ws71.FFsearch.AdvisorCampaignStatusHolder;
import de.factfinder.webservice.ws71.FFsearch.Answer;
import de.factfinder.webservice.ws71.FFsearch.Campaign;
import de.factfinder.webservice.ws71.FFsearch.Question;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;

/**
 * Populator for Advisor Campaigns.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.1
 */
public class ResponseAdvisorCampaignPopulator implements Populator<List<Campaign>, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> {

    private SearchQueryTransformer searchQueryTransformer;

    @Override
    public void populate(final List<Campaign> source, final FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData> target) {
        final List<AdvisorCampaignData<SearchQueryData>> campaigns = new ArrayList<AdvisorCampaignData<SearchQueryData>>();
        for (final Campaign campaign : source) {
            final AdvisorCampaignData campagneData = createAdvisorCampaigneData(campaign, target.getCurrentQuery());
            if (campagneData != null) {
                campaigns.add(campagneData);
            }
        }
        target.setAdvisorCampaigns(campaigns);
    }

    protected AdvisorCampaignData createAdvisorCampaigneData(final Campaign campaign, final SearchQueryData searchQueryData) {
        if (campaign != null && campaign.getActiveQuestions() != null && CollectionUtils.isNotEmpty(campaign.getActiveQuestions().getQuestion())) {
            final AdvisorCampaignData campagneData = new AdvisorCampaignData();
            final List<AdvisorCampaignQuestionData> questions = new ArrayList<AdvisorCampaignQuestionData>();
            for (final Question question : campaign.getActiveQuestions().getQuestion()) {
                if (question.isVisible().booleanValue()) {
                    final AdvisorCampaignQuestionData questionData = createAdvisorCampaignQuestionAnswerData(question, searchQueryData);
                    if (questionData != null) {
                        questions.add(questionData);
                    }
                }
            }
            campagneData.setQuestions(questions);
            return campagneData;
        }
        return null;
    }

    protected AdvisorCampaignQuestionData createAdvisorCampaignQuestionAnswerData(final Question question, final SearchQueryData searchQueryData) {
        if (question != null) {
            if (StringUtils.isNotBlank(question.getText())) {
                // populate question
                final AdvisorCampaignQuestionData<SearchQueryData> questionData = new AdvisorCampaignQuestionData<SearchQueryData>();
                questionData.setQuestion(question.getText());

                // populate answers
                if (question.getAnswers() != null && CollectionUtils.isNotEmpty(question.getAnswers().getAnswer())) {
                    final List<AdvisorCampaignAnswerData<SearchQueryData>> answers = new ArrayList<AdvisorCampaignAnswerData<SearchQueryData>>();
                    for (final Answer answer : question.getAnswers().getAnswer()) {
                        final AdvisorCampaignAnswerData<SearchQueryData> answerData = createAdvisorCampagneAnswerData(answer, searchQueryData);
                        if (answerData != null) {
                            answers.add(answerData);
                        }
                    }
                    questionData.setAnswers(answers);

                }

                return questionData;
            }
        }
        return null;
    }

    protected AdvisorCampaignAnswerData<SearchQueryData> createAdvisorCampagneAnswerData(final Answer answer, final SearchQueryData searchQueryData) {
        if (answer == null || StringUtils.isBlank(answer.getText())) {
            return null;
        }

        if (answer.getParams() == null || StringUtils.isBlank(answer.getParams().getAdvisorStatus().getAnswerPath())
                || StringUtils.isBlank(answer.getParams().getAdvisorStatus().getCampaignId())) {
            return null;
        }

        final AdvisorCampaignAnswerData<SearchQueryData> answerData = new AdvisorCampaignAnswerData<SearchQueryData>();

        // Handling for text and image answers
        final int posOfTextAttribute = answer.getText().indexOf("text=");
        final int posOfImageAttribute = answer.getText().indexOf("image=");
        if (posOfTextAttribute > -1 || posOfImageAttribute > -1) {
            if (posOfTextAttribute > -1 && posOfImageAttribute > -1) {
                // text and image attribute are present
                if (posOfImageAttribute > posOfTextAttribute) {
                    answerData.setText(answer.getText().substring(posOfTextAttribute + 5, posOfImageAttribute).trim());
                    answerData.setImage(answer.getText().substring(posOfImageAttribute + 6).trim());
                } else {
                    answerData.setText(answer.getText().substring(posOfTextAttribute + 5).trim());
                    answerData.setImage(answer.getText().substring(posOfImageAttribute + 6, posOfTextAttribute).trim());
                }
            } else if (posOfTextAttribute > -1) {
                // just text is present
                answerData.setText(answer.getText().substring(posOfTextAttribute + 5).trim());
            } else if (posOfImageAttribute > -1) {
                // just image is present
                answerData.setImage(answer.getText().substring(posOfImageAttribute + 6).trim());
            }
        } else {
            // just text is present
            answerData.setText(answer.getText());
        }

        final AdvisorCampaignStatusHolder advisorStatus = answer.getParams().getAdvisorStatus();
        answerData.setQuery(buildSearchQueryData(advisorStatus, searchQueryData));
        return answerData;
    }

    protected SearchQueryData buildSearchQueryData(final AdvisorCampaignStatusHolder advisorCampaignStatusHolder, final SearchQueryData searchQueryData) {
        final SearchQueryData clonedSearchQueryData = getSearchQueryTransformer().cloneSearchQueryData(searchQueryData);
        final AdvisorStatusData advisorStatus = new AdvisorStatusData();
        advisorStatus.setAnswerPath(advisorCampaignStatusHolder.getAnswerPath());
        advisorStatus.setCampaignId(advisorCampaignStatusHolder.getCampaignId());
        clonedSearchQueryData.setAdvisorStatus(advisorStatus);

        return clonedSearchQueryData;
    }

    public SearchQueryTransformer getSearchQueryTransformer() {
        return searchQueryTransformer;
    }

    @Required
    public void setSearchQueryTransformer(final SearchQueryTransformer searchQueryTransformer) {
        this.searchQueryTransformer = searchQueryTransformer;
    }

}
